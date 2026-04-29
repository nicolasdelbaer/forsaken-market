package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class BoughtItemRepository extends CrudRepository<BoughtItem, Long> {
    public BoughtItemRepository() {
        super(BoughtItem.class);
    }

    public List<BoughtItem> findAllBought(EntityManager entityManager) {
        return entityManager.createQuery("""
        select bi from BoughtItem bi
        where bi.status = :status
        """, BoughtItem.class)
                .setParameter("status", MarketItemStatus.BOUGHT.name())
            .getResultList();
    }
}
