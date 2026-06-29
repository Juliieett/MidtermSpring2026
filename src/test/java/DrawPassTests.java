import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrawPassTests {
    @Test
    void detectsLegalPlayInHand() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("R4");
        assertTrue(UnoRules.hasLegalPlay(hand, "R9", ""));
    }

    @Test
    void detectsNoLegalPlayInHand() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("G2");
        assertFalse(UnoRules.hasLegalPlay(hand, "R9", ""));
    }

    @Test
    void botMustDrawWhenNoLegalPlay() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("G2");
        assertEquals(-1, BotStrategy.chooseCard(hand, "R9", ""));
    }

    @Test
    void drawCommandRequestsDrawTurn() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("R5");
        HumanMove move = HumanMoveParser.parse("draw", hand);
        assertEquals(HumanMove.DRAW, move.type);
    }

    @Test
    void legalDrawnCardMayBePlayed() {
        String drawn = "R4";
        String upCard = "R9";
        assertTrue(CardRules.isLegal(drawn, upCard, ""));
        assertTrue(resolveAfterDraw(drawn, upCard, "", false, false) >= 0);
        assertTrue(resolveAfterDraw(drawn, upCard, "", true, true) >= 0);
    }

    @Test
    void illegalDrawnCardMeansPass() {
        String drawn = "B3";
        String upCard = "R9";
        assertFalse(CardRules.isLegal(drawn, upCard, ""));
        assertEquals(-1, resolveAfterDraw(drawn, upCard, "", false, false));
        assertEquals(-1, resolveAfterDraw(drawn, upCard, "", true, false));
    }

    @Test
    void humanMayPassEvenWhenDrawnCardIsLegal() {
        String drawn = "R2";
        String upCard = "R9";
        assertTrue(CardRules.isLegal(drawn, upCard, ""));
        assertEquals(-1, resolveAfterDraw(drawn, upCard, "", true, false));
    }

    /** Mirrors GameRunner draw-then-maybe-play resolution for bots and humans. */
    private static int resolveAfterDraw(
            String drawn, String upCard, String calledColor, boolean human, boolean humanPlaysDrawn) {
        if (!CardRules.isLegal(drawn, upCard, calledColor)) {
            return -1;
        }
        if (!human) {
            return 0;
        }
        return humanPlaysDrawn ? 0 : -1;
    }
}
