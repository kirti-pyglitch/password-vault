package com.securevault.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Robust cryptographic subsystem for SecureVault.
 * Implements AES-256-GCM authenticated encryption and PBKDF2 key derivation.
 */
public final class VaultEncryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGO = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int GCM_IV_LENGTH = 12; // 96 bits recommended for GCM
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int SALT_LENGTH = 16;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private VaultEncryption() {}

    /**
     * Derives an AES-256 SecretKey from master password and salt.
     */
    public static SecretKey deriveKey(char[] masterPassword, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGO);
            KeySpec spec = new PBEKeySpec(masterPassword, salt, ITERATIONS, KEY_LENGTH);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to derive encryption key", e);
        }
    }

    /**
     * Encrypts plaintext string using AES-256-GCM.
     * Returns Base64-encoded payload containing: [salt (16b) | iv (12b) | ciphertext].
     */
    public static String encrypt(String plaintext, char[] masterPassword) {
        if (plaintext == null) return null;
        try {
            byte[] salt = new byte[SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);

            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            SecretKey key = deriveKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] cipherBytes = cipher.doFinal(plaintextBytes);

            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherBytes.length);
            byteBuffer.put(salt);
            byteBuffer.put(iv);
            byteBuffer.put(cipherBytes);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Encryption operation failed", e);
        }
    }

    /**
     * Decrypts Base64 payload containing [salt (16b) | iv (12b) | ciphertext].
     */
    public static String decrypt(String base64Payload, char[] masterPassword) {
        if (base64Payload == null || base64Payload.isEmpty()) return "";
        try {
            byte[] decoded = Base64.getDecoder().decode(base64Payload);
            if (decoded.length < SALT_LENGTH + GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Payload too short");
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            byte[] salt = new byte[SALT_LENGTH];
            byteBuffer.get(salt);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherBytes);

            SecretKey key = deriveKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption operation failed. Invalid master password or corrupted payload.", e);
        }
    }

    /**
     * Hashes master password for initial authentication check.
     */
    public static String hashMasterPassword(char[] masterPassword, byte[] salt) {
        SecretKey key = deriveKey(masterPassword, salt);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Generates a cryptographically secure random salt.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Computes qualitative password strength score (0 to 100) and bits of entropy.
     */
    public static int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0;

        int score = 0;
        int length = password.length();

        // Length contribution
        if (length >= 8) score += 20;
        if (length >= 12) score += 20;
        if (length >= 16) score += 15;
        if (length >= 20) score += 10;

        // Character pool diversity
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        int variety = 0;
        if (hasUpper) variety++;
        if (hasLower) variety++;
        if (hasDigit) variety++;
        if (hasSpecial) variety++;

        score += variety * 8;

        // Penalize repetitive or sequential characters
        if (password.toLowerCase().contains("12345") || password.toLowerCase().contains("password") || password.toLowerCase().contains("qwerty")) {
            score -= 30;
        }

        return Math.max(5, Math.min(100, score));
    }
}

