package persistence.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import persistence.entity.RoundEntity;

public class RoundRepository {
    private final EntityManager entityManager;

    public RoundRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(RoundEntity round) {
        entityManager.persist(round);
    }

    public List<Object[]> countWinsByPlayer() {
        return entityManager.createQuery(
                        "SELECT r.winnerName, COUNT(r) FROM RoundEntity r "
                                + "WHERE r.winnerName IS NOT NULL "
                                + "GROUP BY r.winnerName ORDER BY COUNT(r) DESC",
                        Object[].class)
                .getResultList();
    }
}
