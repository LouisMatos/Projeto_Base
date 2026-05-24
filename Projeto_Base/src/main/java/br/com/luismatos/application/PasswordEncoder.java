package br.com.luismatos.application;

public interface PasswordEncoder {
    String encode(String rawPassword);
}
