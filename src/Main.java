import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        boolean quiet = false;
        long seed = System.currentTimeMillis();

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
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        Random random = new Random(seed);
        ArrayList<Player> players = setupPlayers(bots, human);

        if (players.size() < 2 || players.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        GameRunner gameRunner = new GameRunner(players, random, scanner, quiet);
        for (int g = 1; g <= games; g++) {
            if (!quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }
            gameRunner.playGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).name + ": " + players.get(i).score);
        }
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
