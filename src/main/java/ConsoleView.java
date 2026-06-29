import java.util.ArrayList;

public class ConsoleView {
    private final boolean quiet;

    ConsoleView(boolean quiet) {
        this.quiet = quiet;
    }

    void showGameNumber(int gameNumber) {
        GameLogger.gameStart(gameNumber);
        if (!quiet) {
            System.out.println("\n=== Game " + gameNumber + " ===");
        }
    }

    void showTurn(String upCard, String calledColor, Player player) {
        GameLogger.playerTurn(player.name, upCard, calledColor);
        if (!quiet) {
            System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
            System.out.println(player.name + " hand: " + formatHand(player.hand));
        }
    }

    void showDraw(String playerName, String card) {
        GameLogger.cardDrawn(playerName, card);
        if (!quiet) {
            System.out.println(playerName + " draws " + card);
        }
    }

    void showInvalidIndexPenalty(String playerName) {
        GameLogger.invalidInput(playerName, "invalid index");
        if (!quiet) {
            System.out.println(playerName + " selected an invalid index and draws a penalty card.");
        }
    }

    void showIllegalCardPenalty(String playerName, String card) {
        GameLogger.invalidInput(playerName, "illegal card " + card);
        if (!quiet) {
            System.out.println(playerName + " tried illegal card " + card + " and draws a penalty card.");
        }
    }

    void showPlay(String playerName, String card) {
        GameLogger.cardPlayed(playerName, card);
        if (!quiet) {
            System.out.println(playerName + " plays " + card);
        }
    }

    void showColorCall(String playerName, String color) {
        if (!quiet) {
            System.out.println(playerName + " calls " + color);
        }
    }

    void showUno(String playerName) {
        if (!quiet) {
            System.out.println(playerName + " says UNO!");
        }
    }

    void showUnoCallAcknowledged() {
        if (!quiet) {
            System.out.println("UNO call noted.");
        }
    }

    void showMissedUnoPenalty(String playerName) {
        GameLogger.invalidInput(playerName, "missed uno call");
        if (!quiet) {
            System.out.println(playerName + " missed UNO and draws two penalty cards.");
        }
    }

    void showRoundScores(ArrayList<Player> players) {
        if (!quiet) {
            System.out.println("Round scores:");
            for (int i = 0; i < players.size(); i++) {
                System.out.println("  " + players.get(i).name + ": " + players.get(i).score);
            }
        }
    }

    void showChampion(String playerName, int targetScore) {
        GameLogger.gameEnd("target reached by " + playerName);
        System.out.println("\n" + playerName + " wins the match with " + targetScore + "+ points!");
    }

    void showWin(String playerName, int points) {
        GameLogger.roundEnd(playerName, points);
        if (!quiet) {
            System.out.println(playerName + " wins and scores " + points);
        }
    }

    void showDrawCards(String playerName, int count) {
        GameLogger.cardsDrawn(playerName, count);
        if (!quiet) {
            if (count == 2) {
                System.out.println(playerName + " draws two.");
            } else if (count == 4) {
                System.out.println(playerName + " draws four.");
            } else {
                System.out.println(playerName + " draws " + count + " cards.");
            }
        }
    }

    void showSafetyLimit() {
        GameLogger.gameEnd("safety limit reached");
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    void showFinalScores(ArrayList<Player> players) {
        GameLogger.gameEnd("session complete");
        System.out.println("\nFinal scores:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).name + ": " + players.get(i).score);
        }
    }

    void promptChooseCard() {
        System.out.print("Choose card index/code, draw, or uno: ");
    }

    void showCardNotLegal() {
        GameLogger.invalidInput("human", "card not legal");
        System.out.println("That card is not legal.");
    }

    void showCardNotFound() {
        GameLogger.invalidInput("human", "card not found");
        System.out.println("Card not found.");
    }

    void promptPlayDrawnCard(String card) {
        System.out.print("Play drawn card " + card + "? y/n: ");
    }

    void promptCallUno() {
        System.out.print("Call UNO now? type uno or y: ");
    }

    void promptColor() {
        System.out.print("Call color R/Y/G/B: ");
    }

    void showBadColor() {
        GameLogger.invalidInput("human", "bad color");
        System.out.println("Bad color.");
    }

    private String formatHand(ArrayList<String> cards) {
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
