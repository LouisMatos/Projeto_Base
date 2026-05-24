package br.com.luismatos.infrastructure.persistence;

import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return jpa.save(usuario);
    }

    @Override
    public boolean deletar(Usuario usuario) {
        jpa.delete(usuario);
        return true;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return jpa.findAllByOrderByEmailAsc();
    }

    @Override
    public boolean existeEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existeEmailParaOutroUsuario(String email, Long idExcluir) {
        return jpa.existsByEmailAndIdNot(email, idExcluir);
    }

    @Override
    public Optional<Usuario> buscarPorEmailESenha(String email, String senhaHash) {
        return jpa.findByEmailAndSenha(email, senhaHash);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpa.findByEmail(email);
    }
}
