package br.com.luismatos.adapter.controller;

import br.com.luismatos.adapter.shared.FacesUtil;
import br.com.luismatos.adapter.shared.SessionContext;
import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.infrastructure.persistence.UsuarioRepositoryImpl;
import br.com.luismatos.infrastructure.security.Sha256PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Controlador da tela de listagem de usuários.
 *
 * Responsabilidades:
 *   - Listar todos os usuários cadastrados
 *   - Preparar e executar a edição via modal (somente ADMINISTRADOR)
 *   - Preparar e executar a exclusão com confirmação (somente ADMINISTRADOR)
 */
@ManagedBean
@ViewScoped
public class ListarUsuariosController implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Usuario> usuarios;
    private Usuario       usuarioEdicao;
    private Long          idParaExcluir;

    private final UsuarioService usuarioService;

    public ListarUsuariosController() {
        usuarioService = new UsuarioService(
            new UsuarioRepositoryImpl(),
            new Sha256PasswordEncoder()
        );
    }

    @PostConstruct
    public void inicializar() {
        carregarUsuarios();
        usuarioEdicao = new Usuario();
    }

    // ── Listagem ──────────────────────────────────────────────────────────────

    private void carregarUsuarios() {
        usuarios = usuarioService.listarTodos();
    }

    // ── Edição ────────────────────────────────────────────────────────────────

    /**
     * Chamado pelo botão "Editar" de cada linha.
     * Copia os dados do usuário selecionado para {@code usuarioEdicao} e abre o modal.
     */
    public void prepararEdicao(Usuario usuario) {
        usuarioEdicao = new Usuario();
        usuarioEdicao.setId(usuario.getId());
        usuarioEdicao.setEmail(usuario.getEmail());
        usuarioEdicao.setTipoUsuario(usuario.getTipoUsuario());
        FacesUtil.executeScript("$('#editarUsuarioModal').modal('show');");
    }

    /**
     * Salva as alterações feitas no modal de edição.
     * Regra de senha: se os campos de senha estiverem em branco, a senha atual é mantida.
     */
    public String salvarEdicao() {
        String mensagemValidacao = validarSenhasEdicao();
        if (mensagemValidacao != null) {
            FacesUtil.executeScript("AppModal.showAviso('" + FacesUtil.escapeJs(mensagemValidacao) + "');");
            return null;
        }

        try {
            String email = usuarioEdicao.getEmail();
            usuarioService.atualizar(usuarioEdicao);
            carregarUsuarios();
            FacesUtil.executeScript(
                "$('#editarUsuarioModal').modal('hide');" +
                "AppModal.showSucesso('Usuário " + FacesUtil.escapeJs(email) + " atualizado com sucesso!');"
            );
        } catch (IllegalArgumentException e) {
            FacesUtil.executeScript(
                "AppModal.showErro('" + FacesUtil.escapeJs(e.getMessage()) + "');"
            );
        } catch (Exception e) {
            FacesUtil.executeScript(
                "AppModal.showErro('Erro inesperado ao atualizar o usuário. Tente novamente.');"
            );
        }
        return null;
    }

    // ── Exclusão ──────────────────────────────────────────────────────────────

    /**
     * Chamado pelo botão "Excluir" de cada linha.
     * Guarda o ID e exibe o modal de confirmação via AppConfirm.
     */
    public void prepararExclusao(Usuario usuario) {
        idParaExcluir = usuario.getId();
        FacesUtil.executeScript(
            "AppConfirm.show({" +
            "  titulo:    'Excluir Usuário'," +
            "  mensagem:  'Deseja realmente excluir o usuário " + FacesUtil.escapeJs(usuario.getEmail()) + "?'," +
            "  onConfirm: function() {" +
            "    document.getElementById('formExclusao:btnExcluirConfirmado').click();" +
            "  }" +
            "});"
        );
    }

    /**
     * Executado pelo botão oculto {@code formExclusao:btnExcluirConfirmado}
     * após o usuário confirmar no modal AppConfirm.
     */
    public String excluirConfirmado() {
        if (idParaExcluir == null) return null;

        try {
            usuarioService.excluir(idParaExcluir);
            carregarUsuarios();
            FacesUtil.executeScript("AppModal.showSucesso('Usuário excluído com sucesso.');" );
        } catch (IllegalArgumentException e) {
            FacesUtil.executeScript(
                "AppModal.showErro('" + FacesUtil.escapeJs(e.getMessage()) + "');"
            );
        } catch (Exception e) {
            FacesUtil.executeScript(
                "AppModal.showErro('Erro inesperado ao excluir o usuário. Tente novamente.');"
            );
        } finally {
            idParaExcluir = null;
        }
        return null;
    }

    // ── Suporte à view ────────────────────────────────────────────────────────

    public TipoUsuario[] getTiposUsuario() {
        return TipoUsuario.values();
    }

    public boolean isUsuarioLogadoAdmin() {
        Usuario logado = SessionContext.getInstance().getUsuarioLogado();
        return logado != null && TipoUsuario.ADMINISTRADOR.equals(logado.getTipoUsuario());
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    /**
     * Valida os campos de senha do formulário de edição.
     *
     * @return mensagem de erro se inválido; {@code null} se tudo estiver correto.
     */
    private String validarSenhasEdicao() {
        String novaSenha = usuarioEdicao.getSenha();
        String confirma  = usuarioEdicao.getConfirmaSenha();

        boolean novaSenhaInformada   = novaSenha != null && !novaSenha.trim().isEmpty();
        boolean confirmaSenhaInformada = confirma  != null && !confirma.trim().isEmpty();

        if (novaSenhaInformada != confirmaSenhaInformada) {
            return "Preencha ambos os campos de senha ou deixe-os em branco para manter a senha atual.";
        }
        if (novaSenhaInformada && !novaSenha.equals(confirma)) {
            return "As senhas não conferem. Verifique e tente novamente.";
        }
        return null;
    }

    // ── Getters/Setters ───────────────────────────────────────────────────────

    public List<Usuario> getUsuarios()      { return usuarios; }
    public Usuario       getUsuarioEdicao() { return usuarioEdicao; }
    public void          setUsuarioEdicao(Usuario u) { this.usuarioEdicao = u; }
    public Long          getIdParaExcluir() { return idParaExcluir; }
    public void          setIdParaExcluir(Long id) { this.idParaExcluir = id; }
}
