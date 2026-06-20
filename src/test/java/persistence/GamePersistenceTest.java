package persistence;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.GameEntity;
import persistence.service.GamePersistenceService;
import persistence.service.StatisticsService;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePersistenceTest {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        String jdbcUrl = "jdbc:h2:mem:uno_persistence_test_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
        entityManagerFactory = PersistenceConfig.createEntityManagerFactory(jdbcUrl);
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    void savesSessionAndQueriesRecentGames() {
        Instant startedAt = Instant.parse("2026-05-23T10:00:00Z");
        Instant endedAt = Instant.parse("2026-05-23T10:05:00Z");
        List<GamePersistenceService.PlayerScoreData> players = Arrays.asList(
                new GamePersistenceService.PlayerScoreData("Bot1", 42),
                new GamePersistenceService.PlayerScoreData("Bot2", 10));
        List<GamePersistenceService.RoundData> rounds = Arrays.asList(
                new GamePersistenceService.RoundData("Bot1", 42, endedAt));

        new GamePersistenceService(entityManagerFactory).saveSession(startedAt, endedAt, players, rounds);

        StatisticsService statisticsService = new StatisticsService(entityManagerFactory);
        List<GameEntity> recentGames = statisticsService.listRecentGames(5);

        assertFalse(recentGames.isEmpty());
        assertEquals("Bot1", recentGames.get(0).getWinnerName());
        assertEquals(1, recentGames.get(0).getRoundsPlayed());
    }

    @Test
    void countsPlayerWins() {
        Instant startedAt = Instant.parse("2026-05-23T11:00:00Z");
        Instant endedAt = Instant.parse("2026-05-23T11:10:00Z");
        List<GamePersistenceService.PlayerScoreData> players = Arrays.asList(
                new GamePersistenceService.PlayerScoreData("Bot1", 20),
                new GamePersistenceService.PlayerScoreData("Bot2", 40));
        List<GamePersistenceService.RoundData> rounds = Arrays.asList(
                new GamePersistenceService.RoundData("Bot1", 20, endedAt),
                new GamePersistenceService.RoundData("Bot2", 40, endedAt));

        new GamePersistenceService(entityManagerFactory).saveSession(startedAt, endedAt, players, rounds);

        StatisticsService statisticsService = new StatisticsService(entityManagerFactory);
        List<StatisticsService.WinCount> wins = statisticsService.playerWinCounts();

        assertEquals(2, wins.size());
        assertTrue(containsWinCount(wins, "Bot1", 1));
        assertTrue(containsWinCount(wins, "Bot2", 1));
    }

    @Test
    void findsHighestScores() {
        Instant startedAt = Instant.parse("2026-05-23T12:00:00Z");
        Instant endedAt = Instant.parse("2026-05-23T12:15:00Z");
        List<GamePersistenceService.PlayerScoreData> players = Arrays.asList(
                new GamePersistenceService.PlayerScoreData("Bot1", 99),
                new GamePersistenceService.PlayerScoreData("Bot2", 15));
        List<GamePersistenceService.RoundData> rounds = Arrays.asList(
                new GamePersistenceService.RoundData("Bot1", 99, endedAt));

        new GamePersistenceService(entityManagerFactory).saveSession(startedAt, endedAt, players, rounds);

        StatisticsService statisticsService = new StatisticsService(entityManagerFactory);
        List<StatisticsService.HighScore> highScores = statisticsService.highestScores(3);

        assertFalse(highScores.isEmpty());
        assertEquals("Bot1", highScores.get(0).playerName);
        assertEquals(99, highScores.get(0).score);
    }

    private static boolean containsWinCount(List<StatisticsService.WinCount> wins, String playerName, int expected) {
        for (int i = 0; i < wins.size(); i++) {
            StatisticsService.WinCount win = wins.get(i);
            if (win.playerName.equals(playerName) && win.wins == expected) {
                return true;
            }
        }
        return false;
    }
}
