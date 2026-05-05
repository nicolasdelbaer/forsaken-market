package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.CollectionItem;
import be.nicolasdelbaer.forsakenmarket.models.inventory.CollectionItemResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class CollectionItemRepository extends CrudRepository<CollectionItem, Long> {
    public CollectionItemRepository() {
        super(CollectionItem.class);
    }

    public List<CollectionItemResponse> findAllForPlayer(EntityManager entityManager, Integer playerId) {
        return entityManager.createQuery("""
                        select 
                            ib.id as blueprintId,
                            ib.title as title,
                            ib.description as description,
                            ib.icon as icon,
                            cast(ib.rarity as string) as rarity,
                            CASE WHEN ci.player IS null THEN FALSE
                            ELSE TRUE END as isCollected
                         from ItemBlueprint ib
                         left join fetch CollectionItem ci on ci.itemBlueprint = ib
                        where ci.player.id = :playerId or ci.player IS null
                        """, CollectionItemResponse.class)
                .setParameter("playerId", playerId)
                .getResultList();
    }

    public boolean isCollected(EntityManager entityManager, Integer playerId, Long itemBlueprintId) {
        return !entityManager.createQuery("""
                        select 1 from CollectionItem ci
                        where ci.itemBlueprint.id = :itemBlueprintId
                        and ci.player.id = :playerId
                        """, Integer.class)
                .setParameter("playerId", playerId)
                .setParameter("itemBlueprintId", itemBlueprintId)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }
}
