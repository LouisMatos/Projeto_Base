package br.com.luismatos.infrastructure.persistence;

import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.domain.repository.UsuarioRepository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioRepositoryImpl extends GenericRepositoryImpl<Usuario> implements UsuarioRepository {

    private static final Logger logger = Logger.getLogger(UsuarioRepositoryImpl.class.getName());

    public UsuarioRepositoryImpl() {
        super(Usuario.class);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return super.salvar(usuario);
    }

    @Override
    public boolean deletar(Usuario usuario) {
        return super.deletar(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return super.buscarPorId(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        try {
            beginTransaction();
            List<Usuario> usuarios = entityManager
                .createQuery("SELECT u FROM Usuario u ORDER BY u.email ASC", Usuario.class)
                .getResultList();
            entityManager.getTransaction().commit();
            return usuarios;
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao listar usuários", e);
            throw new RuntimeException("Erro ao listar usuários", e);
        }
    }

    @Override
    public boolean existeEmail(String email) {
        try {
            beginTransaction();
            TypedQuery<Long> query = entityManager
                .createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.email = :pEmail", Long.class)
                .setParameter("pEmail", email);
            boolean existe = query.getSingleResult() > 0;
            entityManager.getTransaction().commit();
            return existe;
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao verificar existência de e-mail: " + email, e);
            throw new RuntimeException("Erro ao verificar e-mail", e);
        }
    }

    @Override
    public boolean existeEmailParaOutroUsuario(String email, Long idExcluir) {
        try {
            beginTransaction();
            TypedQuery<Long> query = entityManager
                .createQuery(
                    "SELECT COUNT(u) FROM Usuario u WHERE u.email = :pEmail AND u.id <> :pId",
                    Long.class)
                .setParameter("pEmail", email)
                .setParameter("pId",    idExcluir);
            boolean existe = query.getSingleResult() > 0;
            entityManager.getTransaction().commit();
            return existe;
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao verificar e-mail para outro usuário: " + email, e);
            throw new RuntimeException("Erro ao verificar e-mail", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmailESenha(String email, String senhaHash) {
        try {
            beginTransaction();
            TypedQuery<Usuario> query = entityManager
                .createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :pEmail AND u.senha = :pSenha",
                    Usuario.class)
                .setParameter("pEmail", email)
                .setParameter("pSenha", senhaHash);
            Usuario usuario = query.getSingleResult();
            entityManager.getTransaction().commit();
            logger.info("Autenticação bem-sucedida: " + email);
            return Optional.of(usuario);
        } catch (NoResultException e) {
            entityManager.getTransaction().commit();
            logger.warning("Falha de autenticação para: " + email);
            return Optional.empty();
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao buscar usuário por credenciais", e);
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
    }
}
