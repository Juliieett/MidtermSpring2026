import java.util.ArrayList;

public class Player {
    final String name;
    final boolean human;
    final ArrayList<String> hand;
    int score;

    Player(String name, boolean human) {
        this.name = name;
        this.human = human;
        this.hand = new ArrayList<String>();
        this.score = 0;
    }
}
