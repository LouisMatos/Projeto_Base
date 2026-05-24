package br.com.luismatos.adapter.controller;

import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aplicacao/sistema/usuario")
public class ListarUsuariosController {

    private final UsuarioService usuarioService;

    public ListarUsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        model.addAttribute("usuarioEdicao", new Usuario());
        return "aplicacao/sistema/listarUsuarios";
    }

    @PostMapping("/editar")
    public String editar(@ModelAttribute("usuarioEdicao") Usuario usuario,
                         RedirectAttributes redirectAttrs) {
        String erro = validarSenhasEdicao(usuario);
        if (erro != null) {
            redirectAttrs.addFlashAttribute("erro", erro);
            return "redirect:/aplicacao/sistema/usuario/listar";
        }
        try {
            usuarioService.atualizar(usuario);
            redirectAttrs.addFlashAttribute("sucesso",
                "Usuário " + usuario.getEmail() + " atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/aplicacao/sistema/usuario/listar";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.excluir(id);
            redirectAttrs.addFlashAttribute("sucesso", "Usuário excluído com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/aplicacao/sistema/usuario/listar";
    }

    private String validarSenhasEdicao(Usuario usuario) {
        String nova     = usuario.getSenha();
        String confirma = usuario.getConfirmaSenha();
        boolean novaInformada     = nova     != null && !nova.trim().isEmpty();
        boolean confirmaInformada = confirma != null && !confirma.trim().isEmpty();
        if (novaInformada != confirmaInformada) {
            return "Preencha ambos os campos de senha ou deixe-os em branco para manter a senha atual.";
        }
        if (novaInformada && !nova.equals(confirma)) {
            return "As senhas não conferem. Verifique e tente novamente.";
        }
        return null;
    }
}
