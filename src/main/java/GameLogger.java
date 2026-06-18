import java.util.logging.Level;
import java.util.logging.Logger;

public final class GameLogger {
    private static final Logger LOGGER = Logger.getLogger("uno.game");

    private GameLogger() {
    }

    static void sessionStart(int playerCount, int gameCount, long seed) {
        LOGGER.info("Session started: players=" + playerCount + " games=" + gameCount + " seed=" + seed);
    }

    static void gameStart(int gameNumber) {
        LOGGER.info("Game " + gameNumber + " started");
    }

    static void playerTurn(String playerName, String upCard, String calledColor) {
        String message = "Turn: player=" + playerName + " upCard=" + upCard;
        if (!calledColor.equals("")) {
            message += " calledColor=" + calledColor;
        }
        LOGGER.info(message);
    }

    static void cardDrawn(String playerName, String card) {
        LOGGER.info("Draw: player=" + playerName + " card=" + card);
    }

    static void cardsDrawn(String playerName, int count) {
        LOGGER.info("Draw: player=" + playerName + " count=" + count);
    }

    static void cardPlayed(String playerName, String card) {
        LOGGER.info("Play: player=" + playerName + " card=" + card);
    }

    static void invalidInput(String playerName, String reason) {
        LOGGER.warning("Invalid input: player=" + playerName + " reason=" + reason);
    }

    static void roundEnd(String playerName, int points) {
        LOGGER.info("Round ended: winner=" + playerName + " points=" + points);
    }

    static void gameEnd(String reason) {
        LOGGER.info("Game ended: " + reason);
    }

    static void sessionEnd() {
        LOGGER.info("Session ended");
    }

    static void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }
}
