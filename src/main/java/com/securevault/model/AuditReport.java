package com.securevault.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Detailed security audit breakdown.
 */
public class AuditReport {
    public static class AuditIssue {
        public enum Severity {
            CRITICAL, // Deep Burgundy / Crimson
            WARNING,  // Soft Amber
            SECURE    // Muted Teal
        }

        private final Severity severity;
        private final String title;
        private final String description;
        private final Credential credential;

        public AuditIssue(Severity severity, String title, String description, Credential credential) {
            this.severity = severity;
            this.title = title;
            this.description = description;
            this.credential = credential;
        }

        public Severity getSeverity() {
            return severity;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Credential getCredential() {
            return credential;
        }
    }

    private final int healthScore;
    private final List<Credential> weakCredentials;
    private final List<Credential> reusedCredentials;
    private final List<Credential> oldCredentials;
    private final List<Credential> missingInfoCredentials;
    private final List<AuditIssue> allIssues;
    private final List<String> recommendations;

    public AuditReport(int healthScore,
                       List<Credential> weakCredentials,
                       List<Credential> reusedCredentials,
                       List<Credential> oldCredentials,
                       List<Credential> missingInfoCredentials,
                       List<AuditIssue> allIssues,
                       List<String> recommendations) {
        this.healthScore = healthScore;
        this.weakCredentials = weakCredentials != null ? weakCredentials : Collections.emptyList();
        this.reusedCredentials = reusedCredentials != null ? reusedCredentials : Collections.emptyList();
        this.oldCredentials = oldCredentials != null ? oldCredentials : Collections.emptyList();
        this.missingInfoCredentials = missingInfoCredentials != null ? missingInfoCredentials : Collections.emptyList();
        this.allIssues = allIssues != null ? allIssues : Collections.emptyList();
        this.recommendations = recommendations != null ? recommendations : Collections.emptyList();
    }

    public int getHealthScore() {
        return healthScore;
    }

    public String getStatusLabel() {
        if (healthScore >= 90) return "Optimal Security";
        if (healthScore >= 75) return "Healthy Protection";
        if (healthScore >= 50) return "Action Advised";
        return "Critical Vulnerabilities";
    }

    public List<Credential> getWeakCredentials() {
        return weakCredentials;
    }

    public List<Credential> getReusedCredentials() {
        return reusedCredentials;
    }

    public List<Credential> getOldCredentials() {
        return oldCredentials;
    }

    public List<Credential> getMissingInfoCredentials() {
        return missingInfoCredentials;
    }

    public List<AuditIssue> getAllIssues() {
        return allIssues;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}

