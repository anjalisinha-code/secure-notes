package com.cryptographydemo.securenotes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class NoteEncryptionService {

    @Value("${encryption.secret}")
    private String secretHex;

    private static final int IV_LENGTH = 12;     // 96-bit IV (required for GCM)
    private static final int TAG_LENGTH = 128;   // authentication tag (bits)

    private SecretKeySpec keySpec;
    private final SecureRandom rng = new SecureRandom();

    @PostConstruct
    public void init() {
        if (secretHex == null || secretHex.isBlank()) {
            throw new IllegalStateException("ENCRYPTION_SECRET must be set (hex string)");
        }

        byte[] key = hexToBytes(secretHex);
        if (key.length != 16 && key.length != 32) {
            throw new IllegalArgumentException("AES key must be 16 or 32 bytes");
        }

        this.keySpec = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String input) {
        try {
            byte[] iv = randomIV();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            // output = IV || ciphertext
            return Base64.getEncoder().encodeToString(join(iv, ciphertext));
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    public String decrypt(String encoded) {
        try {
            byte[] combined = Base64.getDecoder().decode(encoded);

            byte[] iv = slice(combined, 0, IV_LENGTH);
            byte[] ct = slice(combined, IV_LENGTH, combined.length - IV_LENGTH);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] plain = cipher.doFinal(ct);
            return new String(plain, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    // --------- private helpers ---------

    private byte[] randomIV() {
        byte[] iv = new byte[IV_LENGTH];
        rng.nextBytes(iv);
        return iv;
    }

    private static byte[] join(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    private static byte[] slice(byte[] src, int start, int length) {
        byte[] out = new byte[length];
        System.arraycopy(src, start, out, 0, length);
        return out;
    }

    private static byte[] hexToBytes(String hex) {
        hex = hex.replaceAll("\\s", "");
        int len = hex.length();
        if (len % 2 != 0) throw new IllegalArgumentException("Invalid hex");

        byte[] result = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
        }

        return result;
    }
}
