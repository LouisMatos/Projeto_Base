package br.com.luismatos.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Conexao {
	
	
	private EntityManagerFactory factory = Persistence.createEntityManagerFactory("agenda");
	private EntityManager em = factory.createEntityManager();
	public EntityManager getEntityManager() {
		return em;
	}
	
	

}
