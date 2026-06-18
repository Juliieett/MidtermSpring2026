import java.util.ArrayList;

public class GameState {
    final ArrayList<Player> players;
    final ArrayList<String> deck;
    final ArrayList<String> discard;
    int currentPlayer;
    int direction;
    String upCard;
    String calledColor;

    GameState(ArrayList<Player> players) {
        this.players = players;
        this.deck = new ArrayList<String>();
        this.discard = new ArrayList<String>();
        this.currentPlayer = 0;
        this.direction = 1;
        this.upCard = "";
        this.calledColor = "";
    }

    Player current() {
        return players.get(currentPlayer);
    }

    void next() {
        currentPlayer += direction;
        if (currentPlayer >= players.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = players.size() - 1;
        }
    }

    int playerCount() {
        return players.size();
    }
}
