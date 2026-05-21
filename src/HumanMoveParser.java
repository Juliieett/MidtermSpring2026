import java.util.ArrayList;

public class HumanMoveParser {
    static HumanMove parse(String input, ArrayList<String> hand) {
        String normalized = input.trim().toUpperCase();
        if (normalized.equals("DRAW")) {
            return HumanMove.draw();
        }
        try {
            int index = Integer.parseInt(normalized);
            if (index >= 0 && index < hand.size()) {
                return HumanMove.index(index);
            }
        } catch (Exception ignored) {
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).equals(normalized)) {
                return HumanMove.cardCode(i);
            }
        }
        return HumanMove.notFound();
    }
}

class HumanMove {
    static final String DRAW = "DRAW";
    static final String INDEX = "INDEX";
    static final String CARD_CODE = "CARD_CODE";
    static final String NOT_FOUND = "NOT_FOUND";

    final String type;
    final int index;

    private HumanMove(String type, int index) {
        this.type = type;
        this.index = index;
    }

    static HumanMove draw() {
        return new HumanMove(DRAW, -1);
    }

    static HumanMove index(int index) {
        return new HumanMove(INDEX, index);
    }

    static HumanMove cardCode(int index) {
        return new HumanMove(CARD_CODE, index);
    }

    static HumanMove notFound() {
        return new HumanMove(NOT_FOUND, -1);
    }
}
