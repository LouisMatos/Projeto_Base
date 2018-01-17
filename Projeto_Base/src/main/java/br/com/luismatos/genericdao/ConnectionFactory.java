package br.com.luismatos.genericdao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@SuppressWarnings("serial")
public class ConnectionFactory implements Serializable{
	private static EntityManagerFactory emF = null;
	private static EntityManager em = null;
	
	public static EntityManager getEntityManager(){
		if(emF == null){
			emF = Persistence.createEntityManagerFactory("agenda");
		}
		if(em == null){
			em = emF.createEntityManager();
		}
		return em;
	}
}
