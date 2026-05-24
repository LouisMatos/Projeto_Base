package br.com.luismatos.adapter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // AccessDeniedException é normalmente interceptada pelo Spring Security antes de
    // chegar aqui. Este handler serve como fallback caso ela escape do filtro.
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acesso negado: {}", ex.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("mensagem", "A página que você tentou acessar não existe.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Erro inesperado na aplicação", ex);
        model.addAttribute("status", 500);
        model.addAttribute("mensagem", "Ocorreu um erro inesperado. Tente novamente ou contate o suporte.");
        return "error/500";
    }
}
