import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WildTests {
    @Test
    void wildIsAlwaysLegal() {
        assertTrue(CardRules.isLegal("W", "R9", ""));
    }

    @Test
    void wildDrawFourIsAlwaysLegal() {
        assertTrue(CardRules.isLegal("W4", "R9", ""));
    }

    @Test
    void calledColorAffectsLegalPlay() {
        assertTrue(CardRules.isLegal("B3", "W", "B"));
    }
}
