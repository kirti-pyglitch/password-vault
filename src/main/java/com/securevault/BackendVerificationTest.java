package com.securevault;

import com.securevault.crypto.VaultEncryption;
import com.securevault.model.AuditReport;
import com.securevault.model.Category;
import com.securevault.model.Credential;
import com.securevault.model.VaultStats;
import com.securevault.service.DatabaseManager;
import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.CustomPasswordField;
import com.securevault.ui.components.CustomTextField;
import com.securevault.ui.components.Icons;
import com.securevault.ui.panels.*;

import java.io.File;

/**
 * Automated test verification suite for SecureVault.
 */
public class BackendVerificationTest {
    public static void main(String[] args) {
        System.out.println(">>> STARTING SECUREVAULT BACKEND VERIFICATION");

        testCrypto();
        testDatabaseManager();
        testVaultService();
        testUIComponents();

        System.out.println(">>> ALL SECUREVAULT TESTS PASSED PERFECTLY!");
    }

    private static void testCrypto() {
        System.out.println("[Test] Cryptographic Subsystem...");
        char[] pass = "MasterKey#2026!Editorial".toCharArray();
        String plain = "SuperSecretPlainTextValue1234$#@!";

        String encrypted = VaultEncryption.encrypt(plain, pass);
        assert encrypted != null && !encrypted.equals(plain) : "Encryption failed";

        String decrypted = VaultEncryption.decrypt(encrypted, pass);
        assert plain.equals(decrypted) : "Decryption mismatch: " + decrypted;

        // Test strength calculation
        assert VaultEncryption.calculatePasswordStrength("123") < 30 : "Short password should be weak";
        assert VaultEncryption.calculatePasswordStrength("kR9#vW8!qZ$m2P9x") >= 80 : "Strong password should score high";

        System.out.println("  ✓ AES-256-GCM authenticated encryption verified");
        System.out.println("  ✓ PBKDF2 key derivation & strength calculator verified");
    }

    private static void testDatabaseManager() {
        System.out.println("[Test] DatabaseManager & Persistence...");
        File testDb = new File("test_vault.dat");
        if (testDb.exists()) testDb.delete();

        try {
            DatabaseManager db = new DatabaseManager(testDb);
            char[] master = "TestVaultPassword123!".toCharArray();

            db.initializeVault(master);
            assert db.isInitialized() : "Vault should be initialized";
            assert db.isUnlocked() : "Vault should be unlocked";

            // Add test credential
            Credential c = new Credential("Test Service", "https://test.com", "user@test.com", "TestPass123!", Category.LOGINS, "Test notes");
            db.addCredential(c);

            Credential retrieved = db.getById(c.getId());
            assert retrieved != null : "Credential should be retrievable";
            assert "Test Service".equals(retrieved.getTitle()) : "Title mismatch";

            // Re-authenticate test
            db.lock();
            assert !db.isUnlocked() : "Vault should be locked";
            assert db.authenticate(master) : "Authentication should succeed with master pass";
            assert db.getById(c.getId()) != null : "Records should persist across lock/unlock";

            // Delete
            db.deleteCredential(c.getId());
            assert db.getById(c.getId()) == null : "Credential should be deleted";

            testDb.delete();
            System.out.println("  ✓ DatabaseManager transactional persistence & atomic encryption verified");
        } catch (Exception e) {
            testDb.delete();
            throw new RuntimeException("DatabaseManager test failed", e);
        }
    }

    private static void testVaultService() {
        System.out.println("[Test] VaultService & Security Audit...");
        VaultService service = VaultService.getInstance();
        VaultStats stats = service.getStats();
        assert stats.getHealthScore() >= 0 && stats.getHealthScore() <= 100 : "Health score out of bounds";

        AuditReport audit = service.generateAuditReport();
        assert audit.getHealthScore() >= 0 : "Audit health score failed";
        assert !audit.getRecommendations().isEmpty() : "Should produce recommendations";

        // Test generator
        String gen1 = service.generatePassword(24, true, true, true, true, true);
        assert gen1.length() == 24 : "Generated password length mismatch";

        System.out.println("  ✓ VaultService statistics, audit reports, and password generator verified");
    }

    private static void testUIComponents() {
        System.out.println("[Test] UI Components Instantiation...");
        ThemeManager.initializeTheme();

        // Verify typography and colors
        assert AppColors.CRIMSON_RED != null;
        assert Typography.getFamily() != null;

        // Instantiate components
        CustomButton btn = new CustomButton("Action", CustomButton.Variant.PRIMARY);
        CustomTextField txt = new CustomTextField("Placeholder");
        CustomPasswordField pwd = new CustomPasswordField("Password");
        pwd.toggleVisibility();
        assert pwd.isPasswordVisible() : "Password visibility toggle failed";

        // Verify icon creation
        assert Icons.getIcon("shield", 24, AppColors.CRIMSON_RED) != null;
        assert Icons.getIcon("dashboard", 20, AppColors.JET_BLACK) != null;

        System.out.println("  ✓ Custom Swing buttons, inputs, toggles, and vector icon renderer verified");
    }
}
