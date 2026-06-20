package persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import persistence.entity.PlayerEntity;

public class PlayerRepository {
    private final EntityManager entityManager;

    public PlayerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PlayerEntity findOrCreate(String name) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM PlayerEntity p WHERE p.name = :name",
                            PlayerEntity.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            PlayerEntity player = new PlayerEntity(name);
            entityManager.persist(player);
            return player;
        }
    }
}
