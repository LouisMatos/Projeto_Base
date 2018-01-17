package br.com.luismatos.genericdao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class GenericDAOImplementation<T> implements GenericDAOInterface<T> {
	protected EntityManager entityManager = ConnectionFactory.getEntityManager();
	private Class<T> type;

	public GenericDAOImplementation(){
		
	}
	
	public GenericDAOImplementation(Class<T> type){
		this.type = type;
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	
	@Override
	public T save(T object){
		try{
			if(!entityManager.getTransaction().isActive()){
				entityManager.getTransaction().begin();
			}
			object = entityManager.merge(object);
			entityManager.refresh(object);
			entityManager.getTransaction().commit();
			return object;
		}catch(Exception ex){
			return null;
		}
	}
	
	@Override
	public Boolean delete(T object){
		try{
			if(!entityManager.getTransaction().isActive()){
				entityManager.getTransaction().begin();
			}
			//Verifico se o Entity Manager possui o objeto, se não tiver coloco no Entity Manager
			entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
			entityManager.getTransaction().commit();
		}catch(Exception ex){
			System.out.println(ex.getMessage().toString());
			return null;
		}
		return true;
	}
	
	@Override
	public T edit(T object){
		try{
			if(!entityManager.getTransaction().isActive()){
				entityManager.getTransaction().begin();
			}
			object = entityManager.merge(object);
			entityManager.refresh(object);
			entityManager.getTransaction().commit();
			return object;
		}catch(Exception ex){
			return null;
		}
	}
	
	@Override
	public T find(Long objectId){
		T object;
		if(!entityManager.getTransaction().isActive()){
			entityManager.getTransaction().begin();
		}
		object = entityManager.find(type, objectId);
		entityManager.flush();
		entityManager.getTransaction().commit();
		if(entityManager.getTransaction().isActive()){
			entityManager.getTransaction().commit();
		}
		return object;
	}
}