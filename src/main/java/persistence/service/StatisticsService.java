package persistence.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import persistence.entity.GameEntity;
import persistence.entity.ScoreEntity;
import persistence.repository.GameRepository;
import persistence.repository.RoundRepository;
import persistence.repository.ScoreRepository;

public class StatisticsService {
    private final EntityManagerFactory entityManagerFactory;

    public StatisticsService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<GameEntity> listRecentGames(int limit) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return new GameRepository(entityManager).findRecent(limit);
        } finally {
            entityManager.close();
        }
    }

    public List<WinCount> playerWinCounts() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<Object[]> rows = new RoundRepository(entityManager).countWinsByPlayer();
            List<WinCount> counts = new ArrayList<WinCount>();
            for (int i = 0; i < rows.size(); i++) {
                Object[] row = rows.get(i);
                counts.add(new WinCount((String) row[0], ((Number) row[1]).intValue()));
            }
            return counts;
        } finally {
            entityManager.close();
        }
    }

    public List<HighScore> highestScores(int limit) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<ScoreEntity> scores = new ScoreRepository(entityManager).findHighestScores(limit);
            List<HighScore> highScores = new ArrayList<HighScore>();
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntity score = scores.get(i);
                highScores.add(new HighScore(
                        score.getPlayer().getName(),
                        score.getScore(),
                        score.getGame().getId()));
            }
            return highScores;
        } finally {
            entityManager.close();
        }
    }

    public static final class WinCount {
        public final String playerName;
        public final int wins;

        WinCount(String playerName, int wins) {
            this.playerName = playerName;
            this.wins = wins;
        }
    }

    public static final class HighScore {
        public final String playerName;
        public final int score;
        public final long gameId;

        HighScore(String playerName, int score, long gameId) {
            this.playerName = playerName;
            this.score = score;
            this.gameId = gameId;
        }
    }
}
