package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.GameState;
import be.nicolasdelbaer.forsakenmarket.exceptions.core.CannotFindGameState;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class GameStateRepository extends CrudRepository<GameState, Integer>{

    public GameStateRepository() {
        super(GameState.class);
    }

    public GameState getData(EntityManager entityManager) throws CannotFindGameState {
        return findById(entityManager,1)
                .orElseThrow(() -> new CannotFindGameState(BadResponseUtils.CannotFindGameState));
    }
}
