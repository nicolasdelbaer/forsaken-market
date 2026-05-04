package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.PlayerRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PlayerRoleRepository extends CrudRepository<PlayerRole, Integer> {
    public PlayerRoleRepository() {
        super(PlayerRole.class);
    }

    public PlayerRole findByRoleName(EntityManager entityManager, String roleName) {
        return entityManager.createQuery("""
                    select pr from PlayerRole pr
                    where :roleName = pr.name
                    """, PlayerRole.class)
                .setParameter("roleName", roleName)
                .getSingleResult();
    }
}
