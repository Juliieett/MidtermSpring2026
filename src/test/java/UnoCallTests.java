import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnoCallTests {
    @Test
    void resolvingUnoCallClearsPendingState() {
        Player player = new Player("You", true);
        player.hand.add("R5");
        UnoRules.markPendingUnoCall(player);
        UnoRules.resolveUnoCall(player);
        assertFalse(player.pendingUnoCall);
        assertTrue(player.calledUno);
    }

    @Test
    void missedUnoCallDrawsPenaltyCards() {
        ArrayList<Player> players = new ArrayList<Player>();
        Player player = new Player("You", true);
        player.hand.add("R5");
        players.add(player);
        GameState state = new GameState(players);
        DrawPile pile = new DrawPile(state.deck, state.discard, new Random(1));
        state.deck.add("B2");
        state.deck.add("G3");
        UnoRules.markPendingUnoCall(player);
        UnoRules.applyPendingPenalties(players, pile, new ConsoleView(true));
        assertEquals(3, player.hand.size());
        assertFalse(player.pendingUnoCall);
    }
}
