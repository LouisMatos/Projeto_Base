package br.com.luismatos.infrastructure.security;

import br.com.luismatos.application.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte[] digest = algorithm.digest(rawPassword.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02X", 0xFF & b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new IllegalStateException("Falha ao codificar senha", e);
        }
    }
}
