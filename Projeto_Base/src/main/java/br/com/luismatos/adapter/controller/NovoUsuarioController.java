package br.com.luismatos.adapter.controller;

import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aplicacao/sistema/usuario")
public class NovoUsuarioController {

    private final UsuarioService usuarioService;

    public NovoUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "aplicacao/sistema/novoUsuario";
    }

    @PostMapping("/novo")
    public String salvar(@ModelAttribute Usuario usuario, Model model,
                         RedirectAttributes redirectAttrs) {
        if (!senhasConferem(usuario)) {
            model.addAttribute("erro", "As senhas não conferem. Verifique os campos e tente novamente.");
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "aplicacao/sistema/novoUsuario";
        }
        try {
            usuarioService.registrar(usuario);
            redirectAttrs.addFlashAttribute("sucesso",
                "Usuário " + usuario.getEmail() + " cadastrado com sucesso!");
            return "redirect:/aplicacao/sistema/usuario/novo";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return "aplicacao/sistema/novoUsuario";
        }
    }

    private boolean senhasConferem(Usuario usuario) {
        String senha    = usuario.getSenha();
        String confirma = usuario.getConfirmaSenha();
        return senha != null && !senha.isEmpty() && senha.equals(confirma);
    }
}
