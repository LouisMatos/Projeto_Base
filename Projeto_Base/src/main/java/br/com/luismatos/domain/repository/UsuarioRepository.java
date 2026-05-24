package br.com.luismatos.domain.repository;

import br.com.luismatos.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    boolean deletar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    List<Usuario> listarTodos();

    boolean existeEmail(String email);

    boolean existeEmailParaOutroUsuario(String email, Long idExcluir);

    Optional<Usuario> buscarPorEmailESenha(String email, String senhaHash);

    // Necessário para o UserDetailsService do Spring Security
    Optional<Usuario> buscarPorEmail(String email);
}
