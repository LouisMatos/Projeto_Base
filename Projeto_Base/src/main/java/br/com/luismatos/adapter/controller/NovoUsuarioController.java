package br.com.luismatos.adapter.controller;

import br.com.luismatos.adapter.shared.FacesUtil;
import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.infrastructure.persistence.UsuarioRepositoryImpl;
import br.com.luismatos.infrastructure.security.Sha256PasswordEncoder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Controlador do formulário de cadastro de novo usuário.
 *
 * Escopo ViewScoped: mantém os dados do formulário entre postbacks
 * (ex.: erros de validação) sem perder o que o usuário digitou.
 */
@ManagedBean
@ViewScoped
public class NovoUsuarioController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario;
    private final UsuarioService usuarioService;

    public NovoUsuarioController() {
        usuario = new Usuario();
        usuarioService = new UsuarioService(
            new UsuarioRepositoryImpl(),
            new Sha256PasswordEncoder()
        );
    }

    // ── Ações ────────────────────────────────────────────────────────────────

    /**
     * Valida e persiste o novo usuário.
     * Retorna null para permanecer na mesma página após o AJAX.
     * Em caso de sucesso exibe o modal de confirmação; erros vão para o growl.
     */
    public String salvar() {
        // ── Validação: senhas não conferem (aviso de preenchimento) ──────────
        if (!senhasConferem()) {
            FacesUtil.executeScript(
                "AppModal.showAviso('As senhas não conferem. Verifique os campos e tente novamente.');"
            );
            return null;
        }

        try {
            String email = usuario.getEmail();
            usuarioService.registrar(usuario);
            limpar();

            // ── Sucesso ──────────────────────────────────────────────────────
            FacesUtil.executeScript(
                "AppModal.showSucesso('Usuário " + FacesUtil.escapeJs(email) + " cadastrado com sucesso!');"
            );

        } catch (IllegalArgumentException e) {
            // ── Regra de negócio (ex.: e-mail já cadastrado) ─────────────────
            FacesUtil.executeScript(
                "AppModal.showErro('" + FacesUtil.escapeJs(e.getMessage()) + "');"
            );
        } catch (Exception e) {
            // ── Erro sistêmico ────────────────────────────────────────────────
            FacesUtil.executeScript(
                "AppModal.show({ tipo: 'error'," +
                " titulo: 'Erro inesperado'," +
                " mensagem: 'Não foi possível cadastrar o usuário. Tente novamente ou contate o suporte.' });"
            );
        }
        return null;
    }

    /** Reseta o formulário sem submeter validações. */
    public void limpar() {
        usuario = new Usuario();
    }

    // ── Suporte à view ───────────────────────────────────────────────────────

    /** Lista de tipos disponíveis para o selectOneMenu. */
    public TipoUsuario[] getTiposUsuario() {
        return TipoUsuario.values();
    }

    // ── Lógica interna ────────────────────────────────────────────────────────

    private boolean senhasConferem() {
        String senha    = usuario.getSenha();
        String confirma = usuario.getConfirmaSenha();
        return senha != null && !senha.isEmpty() && senha.equals(confirma);
    }

    // ── Getters/Setters ───────────────────────────────────────────────────────

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
