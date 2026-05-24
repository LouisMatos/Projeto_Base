package br.com.luismatos.infrastructure.persistence;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericRepositoryImpl<T> {

    private static final Logger logger = Logger.getLogger(GenericRepositoryImpl.class.getName());

    protected final EntityManager entityManager;
    private final Class<T> type;

    public GenericRepositoryImpl(Class<T> type) {
        this.type = type;
        this.entityManager = ConnectionFactory.createEntityManager();
    }

    protected T salvar(T object) {
        try {
            beginTransaction();
            object = entityManager.merge(object);
            entityManager.flush();
            entityManager.getTransaction().commit();
            return object;
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao salvar entidade", e);
            throw new RuntimeException("Erro ao salvar entidade", e);
        }
    }

    protected boolean deletar(T object) {
        try {
            beginTransaction();
            T managed = entityManager.contains(object) ? object : entityManager.merge(object);
            entityManager.remove(managed);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao deletar entidade", e);
            throw new RuntimeException("Erro ao deletar entidade", e);
        }
    }

    protected Optional<T> buscarPorId(Long id) {
        try {
            beginTransaction();
            T object = entityManager.find(type, id);
            entityManager.getTransaction().commit();
            return Optional.ofNullable(object);
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao buscar entidade por id: " + id, e);
            throw new RuntimeException("Erro ao buscar entidade por id", e);
        }
    }

    protected List<T> listarTodos() {
        try {
            beginTransaction();
            List<T> resultados = entityManager
                .createQuery("SELECT e FROM " + type.getSimpleName() + " e", type)
                .getResultList();
            entityManager.getTransaction().commit();
            return resultados;
        } catch (Exception e) {
            rollbackIfActive();
            logger.log(Level.SEVERE, "Erro ao listar entidades: " + type.getSimpleName(), e);
            throw new RuntimeException("Erro ao listar entidades", e);
        }
    }

    protected void beginTransaction() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    protected void rollbackIfActive() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }
}
