package com.securevault.service;

import com.securevault.crypto.VaultEncryption;
import com.securevault.model.Category;
import com.securevault.model.Credential;

import java.io.*;
import java.util.*;

/**
 * High-reliability encrypted persistent storage manager.
 * Serializes and encrypts vault records using AES-256-GCM with PBKDF2 authentication.
 */
public class DatabaseManager {
    private static final String DEFAULT_VAULT_FILE = "securevault.dat";
    private final File vaultFile;
    private byte[] masterSalt;
    private String masterHash;
    private final Map<String, Credential> credentials = new LinkedHashMap<>();
    private char[] currentMasterPassword;
    private boolean isUnlocked = false;

    public DatabaseManager() {
        this(new File(DEFAULT_VAULT_FILE));
    }

    public DatabaseManager(File vaultFile) {
        this.vaultFile = vaultFile;
        loadMetadata();
    }

    public boolean isInitialized() {
        return vaultFile.exists() && masterHash != null && !masterHash.isEmpty();
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public synchronized void initializeVault(char[] masterPassword) throws Exception {
        this.masterSalt = VaultEncryption.generateSalt();
        this.masterHash = VaultEncryption.hashMasterPassword(masterPassword, masterSalt);
        this.currentMasterPassword = Arrays.copyOf(masterPassword, masterPassword.length);
        this.isUnlocked = true;

        credentials.clear();
        seedDefaultAccounts();
        saveEncryptedVault();
    }

    public synchronized boolean authenticate(char[] masterPassword) {
        if (!isInitialized()) {
            return false;
        }
        String attemptHash = VaultEncryption.hashMasterPassword(masterPassword, masterSalt);
        if (attemptHash.equals(masterHash)) {
            this.currentMasterPassword = Arrays.copyOf(masterPassword, masterPassword.length);
            this.isUnlocked = true;
            try {
                loadEncryptedRecords();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                lock();
                return false;
            }
        }
        return false;
    }

    public synchronized void lock() {
        if (currentMasterPassword != null) {
            Arrays.fill(currentMasterPassword, '0');
            currentMasterPassword = null;
        }
        isUnlocked = false;
        credentials.clear();
    }

    public synchronized void addCredential(Credential credential) throws Exception {
        ensureUnlocked();
        credentials.put(credential.getId(), credential.copy());
        saveEncryptedVault();
    }

    public synchronized void updateCredential(Credential credential) throws Exception {
        ensureUnlocked();
        credential.setUpdatedAt(System.currentTimeMillis());
        credentials.put(credential.getId(), credential.copy());
        saveEncryptedVault();
    }

    public synchronized boolean deleteCredential(String id) throws Exception {
        ensureUnlocked();
        boolean removed = credentials.remove(id) != null;
        if (removed) {
            saveEncryptedVault();
        }
        return removed;
    }

    public synchronized List<Credential> getAll() {
        List<Credential> list = new ArrayList<>();
        for (Credential c : credentials.values()) {
            list.add(c.copy());
        }
        return list;
    }

    public synchronized Credential getById(String id) {
        Credential c = credentials.get(id);
        return c != null ? c.copy() : null;
    }

    public synchronized void changeMasterPassword(char[] newPassword) throws Exception {
        ensureUnlocked();
        this.masterSalt = VaultEncryption.generateSalt();
        this.masterHash = VaultEncryption.hashMasterPassword(newPassword, masterSalt);
        this.currentMasterPassword = Arrays.copyOf(newPassword, newPassword.length);
        saveEncryptedVault();
    }

    public synchronized void clearAll() throws Exception {
        ensureUnlocked();
        credentials.clear();
        saveEncryptedVault();
    }

    private void ensureUnlocked() {
        if (!isUnlocked || currentMasterPassword == null) {
            throw new IllegalStateException("Vault is locked");
        }
    }

    private void loadMetadata() {
        if (!vaultFile.exists()) return;
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(vaultFile)))) {
            int magic = in.readInt();
            if (magic != 0x53564C54) { // "SVLT"
                return;
            }
            int version = in.readInt();
            int saltLen = in.readInt();
            masterSalt = new byte[saltLen];
            in.readFully(masterSalt);
            masterHash = in.readUTF();
        } catch (Exception ignored) {
        }
    }

    private synchronized void saveEncryptedVault() throws Exception {
        if (currentMasterPassword == null) return;

        // Serialize credentials to JSON or formatted string
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int idx = 0;
        int size = credentials.size();
        for (Credential c : credentials.values()) {
            sb.append("  {")
              .append("\"id\":\"").append(escapeJson(c.getId())).append("\",")
              .append("\"title\":\"").append(escapeJson(c.getTitle())).append("\",")
              .append("\"url\":\"").append(escapeJson(c.getUrl())).append("\",")
              .append("\"username\":\"").append(escapeJson(c.getUsername())).append("\",")
              .append("\"password\":\"").append(escapeJson(c.getPassword())).append("\",")
              .append("\"category\":\"").append(c.getCategory().name()).append("\",")
              .append("\"notes\":\"").append(escapeJson(c.getNotes())).append("\",")
              .append("\"createdAt\":").append(c.getCreatedAt()).append(",")
              .append("\"updatedAt\":").append(c.getUpdatedAt()).append(",")
              .append("\"favorite\":").append(c.isFavorite())
              .append("}");
            if (++idx < size) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        String encryptedPayload = VaultEncryption.encrypt(sb.toString(), currentMasterPassword);

        // Atomic write
        File tempFile = new File(vaultFile.getAbsolutePath() + ".tmp");
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)))) {
            out.writeInt(0x53564C54); // Magic "SVLT"
            out.writeInt(1); // Version 1
            out.writeInt(masterSalt.length);
            out.write(masterSalt);
            out.writeUTF(masterHash);
            out.writeUTF(encryptedPayload);
            out.flush();
        }

        if (vaultFile.exists()) {
            vaultFile.delete();
        }
        tempFile.renameTo(vaultFile);
    }

    private synchronized void loadEncryptedRecords() throws Exception {
        if (!vaultFile.exists()) return;
        credentials.clear();

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(vaultFile)))) {
            int magic = in.readInt();
            if (magic != 0x53564C54) throw new IOException("Invalid file format");
            int version = in.readInt();
            int saltLen = in.readInt();
            byte[] salt = new byte[saltLen];
            in.readFully(salt);
            String hash = in.readUTF();
            String encryptedPayload = in.readUTF();

            String json = VaultEncryption.decrypt(encryptedPayload, currentMasterPassword);
            parseCredentialsJson(json);
        }
    }

    private void parseCredentialsJson(String json) {
        if (json == null || json.trim().isEmpty()) return;
        // Lightweight parser for JSON array of credentials
        String content = json.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
        }
        if (content.isEmpty()) return;

        // Split objects by "},{"
        String[] objects = content.split("\\},\\s*\\{");
        for (String raw : objects) {
            String item = raw.replace("{", "").replace("}", "").trim();
            Credential c = new Credential();
            String[] pairs = item.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replace("\"", "");
                    String val = kv[1].trim();
                    if (val.startsWith("\"") && val.endsWith("\"")) {
                        val = val.substring(1, val.length() - 1);
                    }
                    val = unescapeJson(val);

                    switch (key) {
                        case "id": c.setId(val); break;
                        case "title": c.setTitle(val); break;
                        case "url": c.setUrl(val); break;
                        case "username": c.setUsername(val); break;
                        case "password": c.setPassword(val); break;
                        case "category": c.setCategory(Category.fromString(val)); break;
                        case "notes": c.setNotes(val); break;
                        case "createdAt": try { c.setCreatedAt(Long.parseLong(val)); } catch (Exception ignored) {} break;
                        case "updatedAt": try { c.setUpdatedAt(Long.parseLong(val)); } catch (Exception ignored) {} break;
                        case "favorite": c.setFavorite(Boolean.parseBoolean(val)); break;
                    }
                }
            }
            if (c.getId() != null && !c.getId().isEmpty()) {
                credentials.put(c.getId(), c);
            }
        }
    }

    private void seedDefaultAccounts() {
        long now = System.currentTimeMillis();
        long day = 24L * 60L * 60L * 1000L;

        Credential c1 = new Credential("GitHub Enterprise", "https://github.com", "kirti.lead@security.io", "kR9#vW8!qZ$m2P9x", Category.DEVELOPMENT, "Primary developer organization account with 2FA enabled.");
        c1.setUpdatedAt(now - 4 * day);

        Credential c2 = new Credential("ProtonMail Secure", "https://mail.proton.me", "kirti@proton.me", "P7@mB4#kL9*eR1!q", Category.LOGINS, "Encrypted mail client for critical communications.");
        c2.setUpdatedAt(now - 12 * day);

        Credential c3 = new Credential("AWS Cloud Console", "https://console.aws.amazon.com", "iam-admin@cloud.infra", "9x#Lp$8Wq!2KmZ#4", Category.CLOUD, "Root security administrator credentials for primary region.");
        c3.setUpdatedAt(now - 28 * day);

        Credential c4 = new Credential("Linear Workspace", "https://linear.app", "kirti@tripsure.com", "Linear2024!Secure", Category.LOGINS, "Sprint management and task assignment.");
        c4.setUpdatedAt(now - 45 * day);

        Credential c5 = new Credential("Mercury Bank", "https://mercury.com", "finance@tripsure.com", "Merc#94821*Safe", Category.BANKING, "Corporate treasury checking account.");
        c5.setUpdatedAt(now - 105 * day); // Old credential for audit illustration

        Credential c6 = new Credential("Legacy Test DB", "https://testdb.internal", "admin", "12345678", Category.DEVELOPMENT, "Internal staging database instance.");
        c6.setUpdatedAt(now - 120 * day); // Weak credential for audit illustration

        credentials.put(c1.getId(), c1);
        credentials.put(c2.getId(), c2);
        credentials.put(c3.getId(), c3);
        credentials.put(c4.getId(), c4);
        credentials.put(c5.getId(), c5);
        credentials.put(c6.getId(), c6);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}
