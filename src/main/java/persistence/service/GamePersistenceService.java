package persistence.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.Instant;
import java.util.List;
import persistence.entity.GameEntity;
import persistence.entity.RoundEntity;
import persistence.entity.ScoreEntity;
import persistence.repository.GameRepository;
import persistence.repository.PlayerRepository;
import persistence.repository.RoundRepository;
import persistence.repository.ScoreRepository;

public class GamePersistenceService {
    private final EntityManagerFactory entityManagerFactory;

    public GamePersistenceService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void saveSession(Instant startedAt, Instant endedAt, List<PlayerScoreData> players, List<RoundData> rounds) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            String winnerName = determineSessionWinner(players);
            GameRepository gameRepository = new GameRepository(entityManager);
            PlayerRepository playerRepository = new PlayerRepository(entityManager);
            RoundRepository roundRepository = new RoundRepository(entityManager);
            ScoreRepository scoreRepository = new ScoreRepository(entityManager);

            GameEntity game = new GameEntity(startedAt, endedAt, rounds.size(), winnerName);
            gameRepository.save(game);

            for (int i = 0; i < players.size(); i++) {
                PlayerScoreData player = players.get(i);
                ScoreEntity score = new ScoreEntity(
                        game,
                        playerRepository.findOrCreate(player.name),
                        player.score);
                scoreRepository.save(score);
            }

            for (int i = 0; i < rounds.size(); i++) {
                RoundData round = rounds.get(i);
                RoundEntity roundEntity = new RoundEntity(
                        game,
                        i + 1,
                        round.winnerName,
                        round.winnerPoints,
                        round.endedAt);
                roundRepository.save(roundEntity);
            }

            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    static String determineSessionWinner(List<PlayerScoreData> players) {
        String winner = players.get(0).name;
        int bestScore = players.get(0).score;
        for (int i = 1; i < players.size(); i++) {
            if (players.get(i).score > bestScore) {
                bestScore = players.get(i).score;
                winner = players.get(i).name;
            }
        }
        return winner;
    }

    public static final class PlayerScoreData {
        public final String name;
        public final int score;

        public PlayerScoreData(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static final class RoundData {
        public final String winnerName;
        public final int winnerPoints;
        public final Instant endedAt;

        public RoundData(String winnerName, int winnerPoints, Instant endedAt) {
            this.winnerName = winnerName;
            this.winnerPoints = winnerPoints;
            this.endedAt = endedAt;
        }
    }
}
