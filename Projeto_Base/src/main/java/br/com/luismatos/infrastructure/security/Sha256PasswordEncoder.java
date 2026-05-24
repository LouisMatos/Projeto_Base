package br.com.luismatos.infrastructure.security;

import br.com.luismatos.application.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class Sha256PasswordEncoder implements PasswordEncoder {

    private static final String ALGORITMO = "SHA-256";

    @Override
    public String encode(String senhaRaw) {
        return hash(senhaRaw);
    }

    String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível: " + ALGORITMO, e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hashHex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hashHex.append(String.format("%02X", 0xFF & b));
        }
        return hashHex.toString();
    }
}
