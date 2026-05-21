import java.util.ArrayList;

public class ScoreCalculator {
    static int scoreRemainingHands(ArrayList<ArrayList<String>> hands, int winnerIndex) {
        int points = 0;
        for (int i = 0; i < hands.size(); i++) {
            if (i != winnerIndex) {
                for (int j = 0; j < hands.get(i).size(); j++) {
                    points += points(hands.get(i).get(j));
                }
            }
        }
        return points;
    }

    static int scoreRemainingPlayers(ArrayList<Player> players, int winnerIndex) {
        int points = 0;
        for (int i = 0; i < players.size(); i++) {
            if (i != winnerIndex) {
                for (int j = 0; j < players.get(i).hand.size(); j++) {
                    points += points(players.get(i).hand.get(j));
                }
            }
        }
        return points;
    }

    static int points(String card) {
        String r = CardRules.rank(card);
        if (r.equals("NUMBER")) {
            return CardRules.number(card);
        }
        if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) {
            return 20;
        }
        if (r.equals("WILD") || r.equals("WILD_DRAW_FOUR")) {
            return 50;
        }
        return 0;
    }
}
