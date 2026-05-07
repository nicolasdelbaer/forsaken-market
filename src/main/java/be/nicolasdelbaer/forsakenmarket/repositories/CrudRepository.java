package be.nicolasdelbaer.forsakenmarket.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

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
        return entityManager.createQuery(
            "select t from %s t".formatted(entityName),
            classReference).getResultList();
    }

    public Optional<T> findById(EntityManager entityManager, I itemId){
        T item = entityManager.find(classReference, itemId);
        return Optional.ofNullable(item);
    }

    public T save(EntityManager entityManager, T item){
        entityManager.persist(item);
        return item;
    }

    public List<T> saveAll(EntityManager entityManager, List<T> itemList){
        int batchSize = 50;
        for (int i = 0; i < itemList.size(); i++) {
            entityManager.persist(itemList.get(i));
            if ((i+1) % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        //flush last batch
        entityManager.flush();
        entityManager.clear();

        return itemList;
    }

    public T update(EntityManager entityManager, T item){
        return entityManager.merge(item);
    }

    public List<T> updateAll(EntityManager entityManager, List<T> itemList){
        int batchSize = 50;
        for (int i = 0; i < itemList.size(); i++) {
            entityManager.merge(itemList.get(i));
            if ((i+1) % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        //flush last batch
        entityManager.flush();
        entityManager.clear();
        return itemList;
    }

    public T delete(EntityManager entityManager, T item){
        item = entityManager.merge(item);
        entityManager.remove(item);
        return item;
    }

    public T deleteById(EntityManager entityManager, I id){
        T reference = entityManager.getReference(classReference, id);
        entityManager.remove(reference);
        return reference;
    }

    public Long count(EntityManager entityManager){
        return entityManager.createQuery("select count(t) from %s t".formatted(entityName), Long.class).getSingleResult();
    }

    public boolean exists(EntityManager entityManager, I itemId){
        return !entityManager.createQuery("select 1 from %s t where t.id = :id".formatted(entityName), Integer.class)
                .setParameter("id", itemId)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }
}
