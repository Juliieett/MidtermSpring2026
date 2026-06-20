import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import persistence.entity.GameEntity;
import persistence.service.StatisticsService;

public class ReportCommands {
    private ReportCommands() {
    }

    static void showRecentGames(EntityManagerFactory entityManagerFactory, int limit) {
        StatisticsService statisticsService = new StatisticsService(entityManagerFactory);
        List<GameEntity> games = statisticsService.listRecentGames(limit);
        System.out.println("Recent games:");
        if (games.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        for (int i = 0; i < games.size(); i++) {
            GameEntity game = games.get(i);
            System.out.println("  #" + game.getId()
                    + " winner=" + game.getWinnerName()
                    + " rounds=" + game.getRoundsPlayed()
                    + " ended=" + game.getEndedAt());
        }
    }

    static void showPlayerWins(EntityManagerFactory entityManagerFactory) {
        StatisticsService statisticsService = new StatisticsService(entityManagerFactory);
        List<StatisticsService.WinCount> wins = statisticsService.playerWinCounts();
        System.out.println("Player win counts:");
        if (wins.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        for (int i = 0; i < wins.size(); i++) {
            StatisticsService.WinCount win = wins.get(i);
            System.out.println("  " + win.playerName + ": " + win.wins);
        }
    }

    static void showHighScores(EntityManagerFactory entityManagerFactory, int limit) {
        StatisticsService statisticsService = new StatisticsService(entityManagerFactory);
        List<StatisticsService.HighScore> scores = statisticsService.highestScores(limit);
        System.out.println("Highest scores:");
        if (scores.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        for (int i = 0; i < scores.size(); i++) {
            StatisticsService.HighScore score = scores.get(i);
            System.out.println("  " + score.playerName
                    + ": " + score.score
                    + " (game #" + score.gameId + ")");
        }
    }
}
