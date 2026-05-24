package br.com.luismatos.domain.repository;

import br.com.luismatos.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    boolean deletar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    /** Retorna todos os usuários ordenados por e-mail. */
    List<Usuario> listarTodos();

    boolean existeEmail(String email);

    /**
     * Verifica se o e-mail já está em uso por outro usuário (diferente de idExcluir).
     * Usado na edição para permitir manter o próprio e-mail.
     */
    boolean existeEmailParaOutroUsuario(String email, Long idExcluir);

    Optional<Usuario> buscarPorEmailESenha(String email, String senhaHash);
}
