import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameRunner {
    final ArrayList<Player> players;
    final Random random;
    final ConsoleInput input;
    final ConsoleView view;
    final ArrayList<String> deck;
    final ArrayList<String> discard;
    int currentPlayer;
    int direction;
    String upCard;
    String calledColor;

    GameRunner(ArrayList<Player> players, Random random, ConsoleInput input, ConsoleView view) {
        this.players = players;
        this.random = random;
        this.input = input;
        this.view = view;
        this.deck = new ArrayList<String>();
        this.discard = new ArrayList<String>();
        this.currentPlayer = 0;
        this.direction = 1;
        this.upCard = "";
        this.calledColor = "";
    }

    void playGame() {
        deck.clear();
        deck.addAll(DeckFactory.createShuffledDeck(random));
        discard.clear();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).hand.clear();
        }
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < 7; j++) {
                players.get(i).hand.add(draw());
            }
        }
        upCard = draw();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }
        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(players.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            Player player = players.get(currentPlayer);
            String name = player.name;
            ArrayList<String> hand = player.hand;

            view.showTurn(upCard, calledColor, player);

            int chosen = -1;
            if (player.human) {
                chosen = input.askHumanMove(hand, upCard, calledColor);
            } else {
                chosen = chooseBotCard(hand);
            }

            if (chosen == -1) {
                String drawn = draw();
                hand.add(drawn);
                view.showDraw(name, drawn);
                if (CardRules.isLegal(drawn, upCard, calledColor)) {
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
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = CardRules.isLegal(card, upCard, calledColor);

                if (!ok) {
                    view.showIllegalCardPenalty(name, card);
                    hand.add(draw());
                    next();
                    continue;
                }

                hand.remove(chosen);
                discard.add(upCard);
                upCard = card;
                calledColor = "";
                view.showPlay(name, card);

                if (card.equals("W") || card.equals("W4")) {
                    if (player.human) {
                        calledColor = input.askColor();
                    } else {
                        calledColor = chooseBotColor(hand);
                    }
                    view.showColorCall(name, calledColor);
                }

                if (hand.size() == 1) {
                    view.showUno(name);
                }

                if (hand.size() == 0) {
                    int points = ScoreCalculator.scoreRemainingPlayers(players, currentPlayer);
                    player.score += points;
                    view.showWin(name, points);
                    return;
                }

                applyCardEffect(card);
            } else {
                next();
            }
        }
        view.showSafetyLimit();
    }

    void applyCardEffect(String card) {
        if (CardRules.rank(card).equals("SKIP")) {
            next();
            next();
        } else if (CardRules.rank(card).equals("REVERSE")) {
            direction = direction * -1;
            if (players.size() == 2) {
                next();
                next();
            } else {
                next();
            }
        } else if (CardRules.rank(card).equals("DRAW_TWO")) {
            next();
            drawCardsForCurrentPlayer(2);
            view.showDrawCards(players.get(currentPlayer).name, 2);
            next();
        } else if (CardRules.rank(card).equals("WILD_DRAW_FOUR")) {
            next();
            drawCardsForCurrentPlayer(4);
            view.showDrawCards(players.get(currentPlayer).name, 4);
            next();
        } else {
            next();
        }
    }

    void drawCardsForCurrentPlayer(int count) {
        for (int i = 0; i < count; i++) {
            players.get(currentPlayer).hand.add(draw());
        }
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

    int chooseBotCard(ArrayList<String> hand) {
        return BotStrategy.chooseCard(hand, upCard, calledColor);
    }

    String chooseBotColor(ArrayList<String> hand) {
        return BotStrategy.chooseColor(hand);
    }

    void next() {
        currentPlayer += direction;
        if (currentPlayer >= players.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = players.size() - 1;
        }
    }

}
