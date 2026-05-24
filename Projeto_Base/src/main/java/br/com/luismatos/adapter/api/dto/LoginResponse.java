package br.com.luismatos.adapter.api.dto;

public record LoginResponse(
    String token,
    String email,
    String tipo,
    long   expiresInMs
) {}
