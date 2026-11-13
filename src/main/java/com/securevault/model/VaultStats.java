package com.securevault.model;

import java.util.List;

/**
 * Encapsulates high-level dashboard metrics for the current vault state.
 */
public class VaultStats {
    private final int totalCount;
    private final int weakCount;
    private final int strongCount;
    private final int reusedCount;
    private final int healthScore; // 0 - 100
    private final List<Credential> recentCredentials;

    public VaultStats(int totalCount, int weakCount, int strongCount, int reusedCount, int healthScore, List<Credential> recentCredentials) {
        this.totalCount = totalCount;
        this.weakCount = weakCount;
        this.strongCount = strongCount;
        this.reusedCount = reusedCount;
        this.healthScore = healthScore;
        this.recentCredentials = recentCredentials;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getWeakCount() {
        return weakCount;
    }

    public int getStrongCount() {
        return strongCount;
    }

    public int getReusedCount() {
        return reusedCount;
    }

    public int getHealthScore() {
        return healthScore;
    }

    public List<Credential> getRecentCredentials() {
        return recentCredentials;
    }
}

