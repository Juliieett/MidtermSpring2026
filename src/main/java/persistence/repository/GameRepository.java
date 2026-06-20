package persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import persistence.entity.GameEntity;

public class GameRepository {
    private final EntityManager entityManager;

    public GameRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(GameEntity game) {
        entityManager.persist(game);
    }

    public List<GameEntity> findRecent(int limit) {
        TypedQuery<GameEntity> query = entityManager.createQuery(
                "SELECT g FROM GameEntity g ORDER BY g.endedAt DESC",
                GameEntity.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
