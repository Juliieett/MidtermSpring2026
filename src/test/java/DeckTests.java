import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTests {
    @Test
    void classicDeckHas108Cards() {
        assertEquals(108, DeckFactory.deckSize());
    }
}
