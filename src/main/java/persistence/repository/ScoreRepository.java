package persistence.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import persistence.entity.ScoreEntity;

public class ScoreRepository {
    private final EntityManager entityManager;

    public ScoreRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(ScoreEntity score) {
        entityManager.persist(score);
    }

    public List<ScoreEntity> findHighestScores(int limit) {
        return entityManager.createQuery(
                        "SELECT s FROM ScoreEntity s "
                                + "JOIN FETCH s.player JOIN FETCH s.game "
                                + "ORDER BY s.score DESC, s.id ASC",
                        ScoreEntity.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
