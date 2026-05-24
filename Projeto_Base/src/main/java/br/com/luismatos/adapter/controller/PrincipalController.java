package br.com.luismatos.adapter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalController {

    @GetMapping("/aplicacao/principal")
    public String principal() {
        return "aplicacao/principal";
    }
}
