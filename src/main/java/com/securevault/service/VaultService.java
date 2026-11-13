package com.securevault.service;

import com.securevault.crypto.VaultEncryption;
import com.securevault.model.AuditReport;
import com.securevault.model.Category;
import com.securevault.model.Credential;
import com.securevault.model.VaultStats;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * High-level orchestration service for SecureVault state and security computations.
 */
public class VaultService {
    private static final VaultService INSTANCE = new VaultService();

    private final DatabaseManager databaseManager = new DatabaseManager();
    private final List<Runnable> dataChangeListeners = new CopyOnWriteArrayList<>();
    private final SecureRandom random = new SecureRandom();

    private VaultService() {}

    public static VaultService getInstance() {
        return INSTANCE;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void addListener(Runnable r) {
        dataChangeListeners.add(r);
    }

    public void removeListener(Runnable r) {
        dataChangeListeners.remove(r);
    }

    public void notifyChanged() {
        for (Runnable r : dataChangeListeners) {
            try {
                r.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Credential> getCredentials() {
        return databaseManager.getAll();
    }

    public void saveCredential(Credential credential) throws Exception {
        if (databaseManager.getById(credential.getId()) != null) {
            databaseManager.updateCredential(credential);
        } else {
            databaseManager.addCredential(credential);
        }
        notifyChanged();
    }

    public void deleteCredential(String id) throws Exception {
        databaseManager.deleteCredential(id);
        notifyChanged();
    }

    public List<Credential> search(String query, Category category) {
        List<Credential> all = getCredentials();
        String q = (query == null) ? "" : query.trim().toLowerCase();

        return all.stream()
            .filter(c -> {
                if (category != null && category != Category.ALL && c.getCategory() != category) {
                    return false;
                }
                if (q.isEmpty()) return true;
                return c.getTitle().toLowerCase().contains(q)
                    || c.getUsername().toLowerCase().contains(q)
                    || c.getUrl().toLowerCase().contains(q)
                    || c.getNotes().toLowerCase().contains(q);
            })
            .sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()))
            .collect(Collectors.toList());
    }

    public VaultStats getStats() {
        List<Credential> all = getCredentials();
        int total = all.size();
        int weak = 0;
        int strong = 0;

        Map<String, Integer> passCounts = new HashMap<>();
        for (Credential c : all) {
            String p = c.getPassword();
            int score = VaultEncryption.calculatePasswordStrength(p);
            if (score < 45 || p.length() < 9) {
                weak++;
            } else if (score >= 70 && p.length() >= 12) {
                strong++;
            }
            if (!p.isEmpty()) {
                passCounts.put(p, passCounts.getOrDefault(p, 0) + 1);
            }
        }

        int reused = (int) passCounts.values().stream().filter(cnt -> cnt > 1).count();

        // Calculate security health score 0 - 100
        int health = 100;
        if (total > 0) {
            int weakPenalty = (int) Math.round(((double) weak / total) * 40.0);
            int reusedPenalty = Math.min(30, reused * 10);
            health = Math.max(10, 100 - weakPenalty - reusedPenalty);
        }

        List<Credential> recent = all.stream()
            .sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()))
            .limit(5)
            .collect(Collectors.toList());

        return new VaultStats(total, weak, strong, reused, health, recent);
    }

    public AuditReport generateAuditReport() {
        List<Credential> all = getCredentials();
        List<Credential> weakList = new ArrayList<>();
        List<Credential> reusedList = new ArrayList<>();
        List<Credential> oldList = new ArrayList<>();
        List<Credential> missingInfoList = new ArrayList<>();
        List<AuditReport.AuditIssue> issues = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        long now = System.currentTimeMillis();
        long ninetyDaysMs = 90L * 24L * 60L * 60L * 1000L;

        Map<String, List<Credential>> passwordGroups = new HashMap<>();
        for (Credential c : all) {
            String p = c.getPassword();
            if (!p.isEmpty()) {
                passwordGroups.computeIfAbsent(p, k -> new ArrayList<>()).add(c);
            }

            int strength = VaultEncryption.calculatePasswordStrength(p);
            if (strength < 45 || p.length() < 9) {
                weakList.add(c);
                issues.add(new AuditReport.AuditIssue(
                    AuditReport.AuditIssue.Severity.CRITICAL,
                    "Weak Master Password: " + c.getTitle(),
                    "Password length is under 9 characters or lacks entropy. Easily brute-forced.",
                    c
                ));
            }

            if (now - c.getUpdatedAt() > ninetyDaysMs) {
                oldList.add(c);
                issues.add(new AuditReport.AuditIssue(
                    AuditReport.AuditIssue.Severity.WARNING,
                    "Stale Credential: " + c.getTitle(),
                    "Has not been rotated in over 90 days. Periodic rotation minimizes exposure.",
                    c
                ));
            }

            if (c.getUsername().trim().isEmpty() || c.getUrl().trim().isEmpty()) {
                missingInfoList.add(c);
                issues.add(new AuditReport.AuditIssue(
                    AuditReport.AuditIssue.Severity.WARNING,
                    "Incomplete Profile: " + c.getTitle(),
                    "Missing domain URL or username, preventing auto-matching and breach monitoring.",
                    c
                ));
            }
        }

        for (Map.Entry<String, List<Credential>> entry : passwordGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (Credential c : entry.getValue()) {
                    if (!reusedList.contains(c)) reusedList.add(c);
                }
                issues.add(new AuditReport.AuditIssue(
                    AuditReport.AuditIssue.Severity.CRITICAL,
                    "Reused Password across " + entry.getValue().size() + " accounts",
                    "Password matches multiple services (" +
                        entry.getValue().stream().map(Credential::getTitle).collect(Collectors.joining(", ")) + ").",
                    entry.getValue().get(0)
                ));
            }
        }

        // Recommendations
        if (!weakList.isEmpty()) {
            recommendations.add("Regenerate " + weakList.size() + " weak password(s) using the built-in generator (16+ chars recommended).");
        }
        if (!reusedList.isEmpty()) {
            recommendations.add("Assign unique passphrases to " + reusedList.size() + " shared account(s) to eliminate credential stuffing risk.");
        }
        if (!oldList.isEmpty()) {
            recommendations.add("Audit and rotate " + oldList.size() + " password(s) older than 90 days.");
        }
        if (all.isEmpty()) {
            recommendations.add("Your vault is currently empty. Add your key accounts to begin automated monitoring.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("All credentials meet rigorous cryptographic standards. Vault health is optimal.");
        }

        int score = 100;
        if (!all.isEmpty()) {
            int penalty = (weakList.size() * 15) + (reusedList.size() * 12) + (oldList.size() * 5);
            score = Math.max(10, 100 - penalty);
        }

        return new AuditReport(score, weakList, reusedList, oldList, missingInfoList, issues, recommendations);
    }

    /**
     * Dedicated password generation method conforming to generator parameters.
     */
    public String generatePassword(int length, boolean upper, boolean lower, boolean digits, boolean special, boolean avoidAmbiguous) {
        String upperChars = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String upperAmbiguous = "IO";
        String lowerChars = "abcdefghijkmnpqrstuvwxyz";
        String lowerAmbiguous = "l";
        String digitChars = "23456789";
        String digitAmbiguous = "01";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        if (!avoidAmbiguous) {
            upperChars += upperAmbiguous;
            lowerChars += lowerAmbiguous;
            digitChars += digitAmbiguous;
        }

        StringBuilder pool = new StringBuilder();
        List<Character> guaranteed = new ArrayList<>();

        if (upper && !upperChars.isEmpty()) {
            pool.append(upperChars);
            guaranteed.add(upperChars.charAt(random.nextInt(upperChars.length())));
        }
        if (lower && !lowerChars.isEmpty()) {
            pool.append(lowerChars);
            guaranteed.add(lowerChars.charAt(random.nextInt(lowerChars.length())));
        }
        if (digits && !digitChars.isEmpty()) {
            pool.append(digitChars);
            guaranteed.add(digitChars.charAt(random.nextInt(digitChars.length())));
        }
        if (special && !specialChars.isEmpty()) {
            pool.append(specialChars);
            guaranteed.add(specialChars.charAt(random.nextInt(specialChars.length())));
        }

        if (pool.length() == 0) {
            pool.append("abcdefghjkmnpqrstuvwxyz23456789");
        }

        List<Character> passwordChars = new ArrayList<>();
        passwordChars.addAll(guaranteed);

        while (passwordChars.size() < length) {
            passwordChars.add(pool.charAt(random.nextInt(pool.length())));
        }

        Collections.shuffle(passwordChars, random);

        StringBuilder res = new StringBuilder();
        for (char c : passwordChars) {
            res.append(c);
        }
        return res.toString();
    }
}

