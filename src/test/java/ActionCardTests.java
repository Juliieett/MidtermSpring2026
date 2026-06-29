import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionCardTests {
    @Test
    void skipAdvancesPastNextPlayer() {
        GameState state = newState();
        CardEffectEngine engine = new CardEffectEngine();
        DrawPile pile = new DrawPile(state.deck, state.discard, new Random(1));
        state.currentPlayer = 0;
        state.direction = 1;
        engine.apply("RS", state, pile, new ConsoleView(true));
        assertEquals(2, state.currentPlayer);
    }

    @Test
    void reverseFlipsDirection() {
        GameState state = newState();
        CardEffectEngine engine = new CardEffectEngine();
        DrawPile pile = new DrawPile(state.deck, state.discard, new Random(1));
        state.currentPlayer = 0;
        state.direction = 1;
        engine.apply("RR", state, pile, new ConsoleView(true));
        assertEquals(-1, state.direction);
    }

    @Test
    void drawTwoHitsNextPlayer() {
        GameState state = newState();
        CardEffectEngine engine = new CardEffectEngine();
        DrawPile pile = new DrawPile(state.deck, state.discard, new Random(1));
        state.deck.add("R1");
        state.deck.add("R2");
        state.currentPlayer = 0;
        state.direction = 1;
        engine.apply("R+2", state, pile, new ConsoleView(true));
        assertEquals(2, state.players.get(1).hand.size());
        assertEquals(2, state.currentPlayer);
    }

    private static GameState newState() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("A", false));
        players.add(new Player("B", false));
        players.add(new Player("C", false));
        return new GameState(players);
    }
}
