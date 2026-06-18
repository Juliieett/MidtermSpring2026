import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleInput {
    private final Scanner scanner;
    private final ConsoleView view;

    ConsoleInput(Scanner scanner, ConsoleView view) {
        this.scanner = scanner;
        this.view = view;
    }

    int askHumanMove(ArrayList<String> hand, String upCard, String calledColor) {
        while (true) {
            view.promptChooseCard();
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
                view.showCardNotLegal();
            } else {
                view.showCardNotFound();
            }
        }
    }

    boolean shouldPlayDrawnCard(String card) {
        view.promptPlayDrawnCard(card);
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    String askColor() {
        while (true) {
            view.promptColor();
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
            view.showBadColor();
        }
    }
}
