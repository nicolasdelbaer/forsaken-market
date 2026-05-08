package be.nicolasdelbaer.forsakenmarket.interceptors;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.broadcaster.BroadcastEventQueue;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@Interceptor
@Transactional
@Priority(Interceptor.Priority.APPLICATION)
public class TransactionalInterceptor {

    @Inject private EntityManager entityManager;
    @Inject private BroadcastEventQueue broadcastEventQueue;

    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws Exception {
        EntityTransaction transaction = entityManager.getTransaction();

        //Propagation, if transactional already open, just continue
        boolean isActive = transaction.isActive();

        if(!isActive) transaction.begin();
        try {
            Object result = context.proceed();
            if(!isActive) {
                transaction.commit();
                broadcastEventQueue.flush();
            }
            return result;
        } catch (Exception e) {
            if(!isActive) {
                transaction.rollback();
                broadcastEventQueue.discard();
            }
            throw e;
        }
    }
}