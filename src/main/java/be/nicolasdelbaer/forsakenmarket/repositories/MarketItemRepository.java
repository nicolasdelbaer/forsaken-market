package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MarketItemRepository extends CrudRepository<MarketItem, Long> {
    public MarketItemRepository() {
        super(MarketItem.class);
    }

}
