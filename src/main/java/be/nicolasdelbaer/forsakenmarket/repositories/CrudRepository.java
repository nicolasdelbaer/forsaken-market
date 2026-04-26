package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.utils.EntityFactory;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CrudRepository<T, I> {

    Class<T> classReference;
    String entityName;

    EntityManagerFactory entityManagerFactory;

    public CrudRepository(Class<T> classReference) {
        entityManagerFactory = EntityFactory.getInstance();
        this.classReference = classReference;
        Entity annotation = classReference.getAnnotation(Entity.class);
        this.entityName = (annotation != null && !annotation.name().isEmpty())
                ? annotation.name()
                : classReference.getSimpleName();
    }

    public List<T> findAll(){
        List<T> itemList = new ArrayList<>();
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            itemList = entityManager.createQuery(
                    "select t from %s t".formatted(entityName),
                    classReference).getResultList();
        }
        return itemList;
    }

    public Optional<T> findById(I itemId){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            T item = entityManager.find(classReference, itemId);
            return Optional.ofNullable(item);
        }
    }

    public T save(T item){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(item);
            transaction.commit();
            return item;
        }
    }

    public T update(T item){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            item = entityManager.merge(item);
            transaction.commit();
            return item;
        }
    }

    public T delete(T item){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            item = entityManager.merge(item);
            entityManager.remove(item);
            transaction.commit();
            return item;
        }
    }

    public T deleteById(I id){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            T reference = entityManager.getReference(classReference, id);
            entityManager.remove(reference);
            transaction.commit();
            return reference;
        }
    }

    public Integer count(){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            Integer count = entityManager.createQuery("select count(t) from %s t".formatted(entityName), Integer.class).getSingleResult();
            transaction.commit();
            return count;
        }
    }

    public boolean exists(I itemId){
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            boolean exists = !entityManager.createQuery("select 1 from %s t where t.id = :id".formatted(entityName), Integer.class)
                    .setParameter("id", itemId)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
            transaction.commit();
            return exists;
        }
    }
}
