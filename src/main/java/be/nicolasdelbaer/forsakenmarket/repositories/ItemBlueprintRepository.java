package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.ItemCategory;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemBlueprintRepository extends CrudRepository<ItemBlueprint, Integer> {
    public ItemBlueprintRepository() {
        super(ItemBlueprint.class);
    }

}
