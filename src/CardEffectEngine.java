public class CardEffectEngine {
    void apply(String card, GameState state, DrawPile pile, ConsoleView view) {
        String rank = CardRules.rank(card);
        if (rank.equals("SKIP")) {
            state.next();
            state.next();
        } else if (rank.equals("REVERSE")) {
            state.direction = state.direction * -1;
            if (state.playerCount() == 2) {
                state.next();
                state.next();
            } else {
                state.next();
            }
        } else if (rank.equals("DRAW_TWO")) {
            state.next();
            drawForCurrentPlayer(2, state, pile);
            view.showDrawCards(state.current().name, 2);
            state.next();
        } else if (rank.equals("WILD_DRAW_FOUR")) {
            state.next();
            drawForCurrentPlayer(4, state, pile);
            view.showDrawCards(state.current().name, 4);
            state.next();
        } else {
            state.next();
        }
    }

    void drawForCurrentPlayer(int count, GameState state, DrawPile pile) {
        for (int i = 0; i < count; i++) {
            state.current().hand.add(pile.draw());
        }
    }
}
