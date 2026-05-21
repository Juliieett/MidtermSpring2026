import java.util.ArrayList;

public class ConsoleView {
    private final boolean quiet;

    ConsoleView(boolean quiet) {
        this.quiet = quiet;
    }

    void showGameNumber(int gameNumber) {
        if (!quiet) {
            System.out.println("\n=== Game " + gameNumber + " ===");
        }
    }

    void showTurn(String upCard, String calledColor, Player player) {
        if (!quiet) {
            System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
            System.out.println(player.name + " hand: " + formatHand(player.hand));
        }
    }

    void showDraw(String playerName, String card) {
        if (!quiet) {
            System.out.println(playerName + " draws " + card);
        }
    }

    void showInvalidIndexPenalty(String playerName) {
        if (!quiet) {
            System.out.println(playerName + " selected an invalid index and draws a penalty card.");
        }
    }

    void showIllegalCardPenalty(String playerName, String card) {
        if (!quiet) {
            System.out.println(playerName + " tried illegal card " + card + " and draws a penalty card.");
        }
    }

    void showPlay(String playerName, String card) {
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

    void showWin(String playerName, int points) {
        if (!quiet) {
            System.out.println(playerName + " wins and scores " + points);
        }
    }

    void showDrawCards(String playerName, int count) {
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
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    void showFinalScores(ArrayList<Player> players) {
        System.out.println("\nFinal scores:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).name + ": " + players.get(i).score);
        }
    }

    void promptChooseCard() {
        System.out.print("Choose card index/code or draw: ");
    }

    void showCardNotLegal() {
        System.out.println("That card is not legal.");
    }

    void showCardNotFound() {
        System.out.println("Card not found.");
    }

    void promptPlayDrawnCard(String card) {
        System.out.print("Play drawn card " + card + "? y/n: ");
    }

    void promptColor() {
        System.out.print("Call color R/Y/G/B: ");
    }

    void showBadColor() {
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
