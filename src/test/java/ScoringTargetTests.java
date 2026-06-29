import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ScoringTargetTests {
    @Test
    void scoresRemainingHands() {
        ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();
        hands.add(new ArrayList<String>());
        hands.add(new ArrayList<String>());
        hands.get(1).add("R5");
        hands.get(1).add("YS");
        assertEquals(25, ScoreCalculator.scoreRemainingHands(hands, 0));
    }

    @Test
    void findsChampionAtTargetScore() {
        ArrayList<Player> players = new ArrayList<Player>();
        Player leader = new Player("Bot1", false);
        leader.score = 500;
        players.add(leader);
        players.add(new Player("Bot2", false));
        assertEquals("Bot1", UnoRules.findChampion(players, 500));
    }

    @Test
    void returnsNullWhenNoChampionYet() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("Bot1", false));
        players.add(new Player("Bot2", false));
        assertNull(UnoRules.findChampion(players, 500));
    }
}
