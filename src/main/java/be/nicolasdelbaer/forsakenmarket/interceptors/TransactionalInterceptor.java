package be.nicolasdelbaer.forsakenmarket.interceptors;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

@Interceptor
@Transactional
@Priority(Interceptor.Priority.APPLICATION)
public class TransactionalInterceptor {

    @Inject
    private EntityManagerFactory entityManagerFactory;

    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws Exception {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            try {
                Object result = context.proceed();
                entityTransaction.commit();
                return result;
            } catch (Exception e) {
                entityTransaction.rollback();
                throw e;
            }
        }
    }
}