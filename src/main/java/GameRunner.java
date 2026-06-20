import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class GameRunner {
    final GameState state;
    final DrawPile pile;
    final CardEffectEngine effects;
    final Random random;
    final ConsoleInput input;
    final ConsoleView view;

    GameRunner(ArrayList<Player> players, Random random, ConsoleInput input, ConsoleView view) {
        this.state = new GameState(players);
        this.random = random;
        this.pile = new DrawPile(state.deck, state.discard, random);
        this.effects = new CardEffectEngine();
        this.input = input;
        this.view = view;
    }

    RoundResult playGame() {
        state.deck.clear();
        state.deck.addAll(DeckFactory.createShuffledDeck(random));
        state.discard.clear();
        for (int i = 0; i < state.players.size(); i++) {
            state.players.get(i).hand.clear();
        }
        for (int i = 0; i < state.players.size(); i++) {
            for (int j = 0; j < 7; j++) {
                state.players.get(i).hand.add(pile.draw());
            }
        }
        state.upCard = pile.draw();
        while (state.upCard.startsWith("W")) {
            state.discard.add(state.upCard);
            state.upCard = pile.draw();
        }
        state.calledColor = "";
        state.direction = 1;
        state.currentPlayer = random.nextInt(state.players.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            Player player = state.current();
            String name = player.name;
            ArrayList<String> hand = player.hand;

            view.showTurn(state.upCard, state.calledColor, player);

            int chosen = -1;
            if (player.human) {
                chosen = input.askHumanMove(hand, state.upCard, state.calledColor);
            } else {
                chosen = chooseBotCard(hand);
            }

            if (chosen == -1) {
                String drawn = pile.draw();
                hand.add(drawn);
                view.showDraw(name, drawn);
                if (CardRules.isLegal(drawn, state.upCard, state.calledColor)) {
                    if (!player.human) {
                        chosen = hand.size() - 1;
                    } else {
                        if (input.shouldPlayDrawnCard(drawn)) {
                            chosen = hand.size() - 1;
                        }
                    }
                }
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    view.showInvalidIndexPenalty(name);
                    hand.add(pile.draw());
                    state.next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = CardRules.isLegal(card, state.upCard, state.calledColor);

                if (!ok) {
                    view.showIllegalCardPenalty(name, card);
                    hand.add(pile.draw());
                    state.next();
                    continue;
                }

                hand.remove(chosen);
                state.discard.add(state.upCard);
                state.upCard = card;
                state.calledColor = "";
                view.showPlay(name, card);

                if (card.equals("W") || card.equals("W4")) {
                    if (player.human) {
                        state.calledColor = input.askColor();
                    } else {
                        state.calledColor = chooseBotColor(hand);
                    }
                    view.showColorCall(name, state.calledColor);
                }

                if (hand.size() == 1) {
                    view.showUno(name);
                }

                if (hand.size() == 0) {
                    int points = ScoreCalculator.scoreRemainingPlayers(state.players, state.currentPlayer);
                    player.score += points;
                    view.showWin(name, points);
                    return new RoundResult(name, points, Instant.now());
                }

                effects.apply(card, state, pile, view);
            } else {
                state.next();
            }
        }
        view.showSafetyLimit();
        return new RoundResult(null, 0, Instant.now());
    }

    int chooseBotCard(ArrayList<String> hand) {
        return BotStrategy.chooseCard(hand, state.upCard, state.calledColor);
    }

    String chooseBotColor(ArrayList<String> hand) {
        return BotStrategy.chooseColor(hand);
    }
}
