package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.ItemCategory;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ItemCategoryRepository extends CrudRepository<ItemCategory, Integer> {
    public ItemCategoryRepository() {
        super(ItemCategory.class);
    }

}
