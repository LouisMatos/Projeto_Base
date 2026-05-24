package br.com.luismatos.infrastructure.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ConnectionFactory {

    private static final String PERSISTENCE_UNIT = "agenda";
    private static volatile EntityManagerFactory emFactory;

    private ConnectionFactory() {}

    // Retorna um novo EntityManager por chamada. Cada instância de repositório
    // gerencia seu próprio EM, evitando compartilhamento entre threads.
    public static EntityManager createEntityManager() {
        if (emFactory == null) {
            synchronized (ConnectionFactory.class) {
                if (emFactory == null) {
                    emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                }
            }
        }
        return emFactory.createEntityManager();
    }
}
