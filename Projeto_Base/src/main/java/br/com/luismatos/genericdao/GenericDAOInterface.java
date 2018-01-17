package br.com.luismatos.genericdao;

public interface GenericDAOInterface<T> {
	public T save(T object);
	public Boolean delete(T Object);
	public T edit(T object);
	public T find(Long objectId);
}
