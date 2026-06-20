import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.LogManager;
import persistence.PersistenceConfig;
import persistence.service.GamePersistenceService;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    static {
        try (InputStream in = Main.class.getResourceAsStream("/logging.properties")) {
            if (in != null) {
                LogManager.getLogManager().readConfiguration(in);
            }
        } catch (IOException ignored) {
        }
    }

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        boolean quiet = false;
        long seed = System.currentTimeMillis();
        boolean recentGames = false;
        boolean playerWins = false;
        boolean highScores = false;
        int reportLimit = 10;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--recent-games")) {
                recentGames = true;
            } else if (args[i].equals("--player-wins")) {
                playerWins = true;
            } else if (args[i].equals("--high-scores")) {
                highScores = true;
            } else if (args[i].equals("--report-limit") && i + 1 < args.length) {
                reportLimit = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                printHelp();
                return;
            }
        }

        if (recentGames || playerWins || highScores) {
            runReports(recentGames, playerWins, highScores, reportLimit);
            return;
        }

        Random random = new Random(seed);
        ArrayList<Player> players = setupPlayers(bots, human);
        ConsoleView view = new ConsoleView(quiet);
        ConsoleInput input = new ConsoleInput(scanner, view);

        if (players.size() < 2 || players.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        GameLogger.sessionStart(players.size(), games, seed);
        Instant sessionStart = Instant.now();
        GameRunner gameRunner = new GameRunner(players, random, input, view);
        ArrayList<RoundResult> roundResults = new ArrayList<RoundResult>();
        for (int g = 1; g <= games; g++) {
            view.showGameNumber(g);
            roundResults.add(gameRunner.playGame());
        }

        view.showFinalScores(players);
        GameLogger.sessionEnd();
        persistSession(sessionStart, Instant.now(), players, roundResults);
    }

    static void persistSession(Instant startedAt, Instant endedAt, ArrayList<Player> players, ArrayList<RoundResult> rounds) {
        ArrayList<GamePersistenceService.PlayerScoreData> playerScores = new ArrayList<GamePersistenceService.PlayerScoreData>();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            playerScores.add(new GamePersistenceService.PlayerScoreData(player.name, player.score));
        }

        ArrayList<GamePersistenceService.RoundData> roundData = new ArrayList<GamePersistenceService.RoundData>();
        for (int i = 0; i < rounds.size(); i++) {
            RoundResult round = rounds.get(i);
            roundData.add(new GamePersistenceService.RoundData(round.winnerName, round.winnerPoints, round.endedAt));
        }

        EntityManagerFactory entityManagerFactory = PersistenceConfig.createEntityManagerFactory();
        try {
            new GamePersistenceService(entityManagerFactory).saveSession(startedAt, endedAt, playerScores, roundData);
        } finally {
            entityManagerFactory.close();
        }
    }

    static void runReports(boolean recentGames, boolean playerWins, boolean highScores, int reportLimit) {
        EntityManagerFactory entityManagerFactory = PersistenceConfig.createEntityManagerFactory();
        try {
            if (recentGames) {
                ReportCommands.showRecentGames(entityManagerFactory, reportLimit);
            }
            if (playerWins) {
                ReportCommands.showPlayerWins(entityManagerFactory);
            }
            if (highScores) {
                ReportCommands.showHighScores(entityManagerFactory, reportLimit);
            }
        } finally {
            entityManagerFactory.close();
        }
    }

    static void printHelp() {
        System.out.println("Usage: java -jar uno-cli.jar [options]");
        System.out.println("  --bots N            number of bot players");
        System.out.println("  --games N           number of rounds to play");
        System.out.println("  --human             include a human player");
        System.out.println("  --quiet             reduce console output");
        System.out.println("  --seed N            random seed");
        System.out.println("  --self-test         run characterization checks");
        System.out.println("  --recent-games      list recent persisted games");
        System.out.println("  --player-wins       show round win counts by player");
        System.out.println("  --high-scores       show highest session scores");
        System.out.println("  --report-limit N    limit for report output (default 10)");
    }

    static ArrayList<Player> setupPlayers(int bots, boolean human) {
        ArrayList<Player> players = new ArrayList<Player>();
        if (human) {
            players.add(new Player("You", true));
        }
        for (int i = 1; i <= bots; i++) {
            players.add(new Player("Bot" + i, false));
        }
        return players;
    }

    static void selfTest() {
        CharacterizationTests.run();
    }
}
