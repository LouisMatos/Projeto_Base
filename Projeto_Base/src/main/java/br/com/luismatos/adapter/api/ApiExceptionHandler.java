package br.com.luismatos.adapter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tratamento centralizado de exceções para a camada REST (/api/**).
 * Retorna JSON em vez de páginas HTML de erro.
 */
@RestControllerAdvice(basePackages = "br.com.luismatos.adapter.api")
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // ── Regras de negócio / dados inválidos ───────────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Requisição inválida: {}", ex.getMessage());
        return errorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── Falhas de validação Bean Validation (@Valid) ───────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        log.warn("Validação falhou: {}", erros);

        Map<String, Object> body = errorBody(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.");
        body.put("erros", erros);
        return body;
    }

    // ── Erros inesperados ─────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneric(Exception ex) {
        log.error("Erro interno não tratado", ex);
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente mais tarde.");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Map<String, Object> errorBody(HttpStatus status, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status",    status.value());
        body.put("erro",      status.getReasonPhrase());
        body.put("mensagem",  mensagem);
        return body;
    }
}
