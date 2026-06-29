import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegalPlayTests {
    @Test
    void allowsMatchByColor() {
        assertTrue(CardRules.isLegal("R2", "R9", ""));
    }

    @Test
    void allowsMatchByNumber() {
        assertTrue(CardRules.isLegal("G9", "R9", ""));
    }

    @Test
    void allowsMatchByActionType() {
        assertTrue(CardRules.isLegal("GS", "RS", ""));
    }

    @Test
    void rejectsIllegalCard() {
        assertFalse(CardRules.isLegal("B3", "R9", ""));
    }
}
