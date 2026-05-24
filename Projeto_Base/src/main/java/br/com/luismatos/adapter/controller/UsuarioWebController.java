package br.com.luismatos.adapter.controller;

import br.com.luismatos.adapter.navigation.Routes;
import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioWebController {

    private final UsuarioService usuarioService;

    public UsuarioWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ── Cadastro ─────────────────────────────────────────────────────────────

    @GetMapping(Routes.Web.USUARIO_NOVO)
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return Routes.Views.NOVO_USUARIO;
    }

    @PostMapping(Routes.Web.USUARIO_NOVO)
    public String salvar(@ModelAttribute Usuario usuario, Model model,
                         RedirectAttributes redirectAttrs) {
        if (!senhasConferem(usuario)) {
            model.addAttribute("erro", "As senhas não conferem. Verifique os campos e tente novamente.");
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return Routes.Views.NOVO_USUARIO;
        }
        try {
            usuarioService.registrar(usuario);
            redirectAttrs.addFlashAttribute("sucesso",
                "Usuário " + usuario.getEmail() + " cadastrado com sucesso!");
            return "redirect:" + Routes.Web.USUARIO_NOVO;
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            return Routes.Views.NOVO_USUARIO;
        }
    }

    // ── Listagem / Edição / Exclusão ─────────────────────────────────────────

    @GetMapping(Routes.Web.USUARIO_LISTAR)
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        model.addAttribute("usuarioEdicao", new Usuario());
        return Routes.Views.LISTAR_USUARIOS;
    }

    @PostMapping(Routes.Web.USUARIO_EDITAR)
    public String editar(@ModelAttribute("usuarioEdicao") Usuario usuario,
                         RedirectAttributes redirectAttrs) {
        String erro = validarSenhasEdicao(usuario);
        if (erro != null) {
            redirectAttrs.addFlashAttribute("erro", erro);
            return "redirect:" + Routes.Web.USUARIO_LISTAR;
        }
        try {
            usuarioService.atualizar(usuario);
            redirectAttrs.addFlashAttribute("sucesso",
                "Usuário " + usuario.getEmail() + " atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:" + Routes.Web.USUARIO_LISTAR;
    }

    @PostMapping(Routes.Web.USUARIO_EXCLUIR_ID)
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.excluir(id);
            redirectAttrs.addFlashAttribute("sucesso", "Usuário excluído com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:" + Routes.Web.USUARIO_LISTAR;
    }

    // ── Validações ────────────────────────────────────────────────────────────

    private boolean senhasConferem(Usuario usuario) {
        String senha    = usuario.getSenha();
        String confirma = usuario.getConfirmaSenha();
        return senha != null && !senha.isEmpty() && senha.equals(confirma);
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
