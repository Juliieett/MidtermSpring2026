import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class GameRunner {
    final ArrayList<Player> players;
    final Random random;
    final Scanner scanner;
    final boolean quiet;
    final ArrayList<String> deck;
    final ArrayList<String> discard;
    int currentPlayer;
    int direction;
    String upCard;
    String calledColor;

    GameRunner(ArrayList<Player> players, Random random, Scanner scanner, boolean quiet) {
        this.players = players;
        this.random = random;
        this.scanner = scanner;
        this.quiet = quiet;
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

            if (!quiet) {
                System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
                System.out.println(name + " hand: " + join(hand));
            }

            int chosen = -1;
            if (player.human) {
                chosen = askHuman(hand);
            } else {
                chosen = chooseBotCard(hand);
            }

            if (chosen == -1) {
                String drawn = draw();
                hand.add(drawn);
                if (!quiet) {
                    System.out.println(name + " draws " + drawn);
                }
                if (CardRules.isLegal(drawn, upCard, calledColor)) {
                    if (!player.human) {
                        chosen = hand.size() - 1;
                    } else {
                        System.out.print("Play drawn card " + drawn + "? y/n: ");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                            chosen = hand.size() - 1;
                        }
                    }
                }
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    if (!quiet) {
                        System.out.println(name + " selected an invalid index and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = CardRules.isLegal(card, upCard, calledColor);

                if (!ok) {
                    if (!quiet) {
                        System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                hand.remove(chosen);
                discard.add(upCard);
                upCard = card;
                calledColor = "";
                if (!quiet) {
                    System.out.println(name + " plays " + card);
                }

                if (card.equals("W") || card.equals("W4")) {
                    if (player.human) {
                        calledColor = askColor();
                    } else {
                        calledColor = chooseBotColor(hand);
                    }
                    if (!quiet) {
                        System.out.println(name + " calls " + calledColor);
                    }
                }

                if (hand.size() == 1 && !quiet) {
                    System.out.println(name + " says UNO!");
                }

                if (hand.size() == 0) {
                    int points = ScoreCalculator.scoreRemainingPlayers(players, currentPlayer);
                    player.score += points;
                    if (!quiet) {
                        System.out.println(name + " wins and scores " + points);
                    }
                    return;
                }

                applyCardEffect(card);
            } else {
                next();
            }
        }
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
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
            if (!quiet) {
                System.out.println(players.get(currentPlayer).name + " draws two.");
            }
            next();
        } else if (CardRules.rank(card).equals("WILD_DRAW_FOUR")) {
            next();
            drawCardsForCurrentPlayer(4);
            if (!quiet) {
                System.out.println(players.get(currentPlayer).name + " draws four.");
            }
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

    int askHuman(ArrayList<String> hand) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            HumanMove move = HumanMoveParser.parse(scanner.nextLine(), hand);
            if (move.type.equals(HumanMove.DRAW)) {
                return -1;
            }
            if (move.type.equals(HumanMove.INDEX)) {
                return move.index;
            }
            if (move.type.equals(HumanMove.CARD_CODE)) {
                if (CardRules.isLegal(hand.get(move.index), upCard, calledColor)) {
                    return move.index;
                }
                System.out.println("That card is not legal.");
            } else {
                System.out.println("Card not found.");
            }
        }
    }

    String askColor() {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R")) {
                return "R";
            }
            if (input.equals("Y")) {
                return "Y";
            }
            if (input.equals("G")) {
                return "G";
            }
            if (input.equals("B")) {
                return "B";
            }
            System.out.println("Bad color.");
        }
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

    String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }
}
