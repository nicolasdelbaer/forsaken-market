package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BoughtItemRepository extends CrudRepository<BoughtItem, Long> {
    public BoughtItemRepository() {
        super(BoughtItem.class);
    }

}
