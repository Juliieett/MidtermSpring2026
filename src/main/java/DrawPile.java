import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DrawPile {
    final ArrayList<String> deck;
    final ArrayList<String> discard;
    final Random random;

    DrawPile(ArrayList<String> deck, ArrayList<String> discard, Random random) {
        this.deck = deck;
        this.discard = discard;
        this.random = random;
    }

    String draw() {
        if (deck.size() == 0) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.size() == 0) {
            return "W";
        }
        return deck.remove(0);
    }
}
