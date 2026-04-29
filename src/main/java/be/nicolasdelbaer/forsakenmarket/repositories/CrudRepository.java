package be.nicolasdelbaer.forsakenmarket.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CrudRepository<T, I> {

    Class<T> classReference;
    String entityName;

    public CrudRepository(Class<T> classReference) {
        this.classReference = classReference;
        Entity annotation = classReference.getAnnotation(Entity.class);
        this.entityName = (annotation != null && !annotation.name().isEmpty())
                ? annotation.name()
                : classReference.getSimpleName();
    }

    public List<T> findAll(EntityManager entityManager){
        List<T> itemList = new ArrayList<>();
            itemList = entityManager.createQuery(
                    "select t from %s t".formatted(entityName),
                    classReference).getResultList();
        return itemList;
    }

    public Optional<T> findById(EntityManager entityManager, I itemId){
        T item = entityManager.find(classReference, itemId);
        return Optional.ofNullable(item);
    }

    public T save(EntityManager entityManager, T item){
        entityManager.persist(item);
        return item;
    }

    public T update(EntityManager entityManager, T item){
        item = entityManager.merge(item);
        return item;
    }

    public T delete(EntityManager entityManager, T item){
        item = entityManager.merge(item);
        entityManager.remove(item);
        return item;
    }

    public T deleteById(EntityManager entityManager, I id){
        T reference = entityManager.getReference(classReference, id);
        return reference;
    }

    public Integer count(EntityManager entityManager){
        Integer count = entityManager.createQuery("select count(t) from %s t".formatted(entityName), Integer.class).getSingleResult();
        return count;
    }

    public boolean exists(EntityManager entityManager, I itemId){
        boolean exists = !entityManager.createQuery("select 1 from %s t where t.id = :id".formatted(entityName), Integer.class)
                .setParameter("id", itemId)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
        return exists;
    }
}
