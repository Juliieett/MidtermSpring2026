import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CharacterizationTests {
    static void run() {
        int passed = 0;
        passed += check(CardRules.color("R5").equals("R"), "color R5");
        passed += check(CardRules.rank("G+2").equals("DRAW_TWO"), "rank +2");
        passed += check(CardRules.rank("YS").equals("SKIP"), "rank skip");
        passed += check(CardRules.rank("BR").equals("REVERSE"), "rank reverse");
        passed += check(CardRules.rank("W").equals("WILD"), "rank wild");
        passed += check(CardRules.rank("W4").equals("WILD_DRAW_FOUR"), "rank wild draw four");

        passed += check(ScoreCalculator.points("R7") == 7, "number points");
        passed += check(ScoreCalculator.points("YS") == 20, "skip points");
        passed += check(ScoreCalculator.points("BR") == 20, "reverse points");
        passed += check(ScoreCalculator.points("G+2") == 20, "draw two points");
        passed += check(ScoreCalculator.points("W") == 50, "wild points");
        passed += check(ScoreCalculator.points("W4") == 50, "wild draw four points");

        ArrayList<ArrayList<String>> scoreHands = new ArrayList<ArrayList<String>>();
        scoreHands.add(new ArrayList<String>());
        scoreHands.add(new ArrayList<String>());
        scoreHands.add(new ArrayList<String>());
        scoreHands.get(0).add("W4");
        scoreHands.get(1).add("R5");
        scoreHands.get(1).add("B9");
        scoreHands.get(2).add("GS");
        scoreHands.get(2).add("W");
        passed += check(ScoreCalculator.scoreRemainingHands(scoreHands, 0) == 84, "winner scores other hands");

        passed += check(CardRules.isLegal("R2", "R9", ""), "same color");
        passed += check(CardRules.isLegal("G9", "R9", ""), "same number");
        passed += check(CardRules.isLegal("GS", "RS", ""), "same skip action");
        passed += check(CardRules.isLegal("G+2", "R+2", ""), "same draw two action");
        passed += check(CardRules.isLegal("GR", "RR", ""), "same reverse action");
        passed += check(CardRules.isLegal("W", "R9", ""), "wild is always legal");
        passed += check(CardRules.isLegal("W4", "R9", ""), "wild draw four is always legal");
        passed += check(CardRules.isLegal("B3", "W", "B"), "called color");
        passed += check(!CardRules.isLegal("B3", "R9", ""), "illegal mismatch");

        ArrayList<String> h = new ArrayList<String>();
        h.add("B3");
        h.add("R4");
        h.add("W");
        GameRunner runner = newRunner();
        runner.upCard = "R9";
        runner.calledColor = "";
        passed += check(runner.chooseBotCard(h) == 1, "bot normal before wild");

        ArrayList<String> hDrawTwo = new ArrayList<String>();
        hDrawTwo.add("RS");
        hDrawTwo.add("R+2");
        hDrawTwo.add("R3");
        runner.upCard = "R9";
        runner.calledColor = "";
        passed += check(runner.chooseBotCard(hDrawTwo) == 1, "bot prefers draw two");

        ArrayList<String> hSkip = new ArrayList<String>();
        hSkip.add("R3");
        hSkip.add("RS");
        runner.upCard = "R9";
        runner.calledColor = "";
        passed += check(runner.chooseBotCard(hSkip) == 1, "bot prefers skip before number");

        ArrayList<String> h2 = new ArrayList<String>();
        h2.add("B1");
        h2.add("B2");
        h2.add("R3");
        passed += check(runner.chooseBotColor(h2).equals("B"), "bot color");

        ArrayList<String> inputHand = new ArrayList<String>();
        inputHand.add("B3");
        inputHand.add("R4");
        HumanMove drawMove = HumanMoveParser.parse("draw", inputHand);
        HumanMove indexMove = HumanMoveParser.parse("1", inputHand);
        HumanMove codeMove = HumanMoveParser.parse("R4", inputHand);
        HumanMove missingMove = HumanMoveParser.parse("99", inputHand);
        passed += check(drawMove.type.equals(HumanMove.DRAW), "parse draw command");
        passed += check(indexMove.type.equals(HumanMove.INDEX) && indexMove.index == 1, "parse index command");
        passed += check(codeMove.type.equals(HumanMove.CARD_CODE) && codeMove.index == 1, "parse card code command");
        passed += check(missingMove.type.equals(HumanMove.NOT_FOUND), "parse missing command");

        runner = newRunner();
        runner.deck.clear();
        runner.discard.clear();
        runner.discard.add("R1");
        passed += check(runner.draw().equals("R1"), "draw reshuffles discard");
        passed += check(runner.discard.size() == 0, "discard empty after reshuffle");

        runner.deck.clear();
        runner.discard.clear();
        passed += check(runner.draw().equals("W"), "draw fallback wild");

        runner = newRunner();
        runner.currentPlayer = 0;
        runner.direction = 1;
        runner.next();
        passed += check(runner.currentPlayer == 1, "next clockwise");
        runner.currentPlayer = 0;
        runner.direction = -1;
        runner.next();
        passed += check(runner.currentPlayer == 2, "next wraps counterclockwise");

        runner.currentPlayer = 0;
        runner.direction = 1;
        runner.applyCardEffect("RS");
        passed += check(runner.currentPlayer == 2, "skip advances past next player");

        runner.currentPlayer = 0;
        runner.direction = 1;
        runner.applyCardEffect("RR");
        passed += check(runner.direction == -1 && runner.currentPlayer == 2, "reverse flips direction");

        runner = newRunner();
        runner.deck.clear();
        runner.discard.clear();
        runner.deck.add("R1");
        runner.deck.add("R2");
        runner.currentPlayer = 0;
        runner.direction = 1;
        runner.applyCardEffect("R+2");
        passed += check(runner.players.get(1).hand.size() == 2 && runner.currentPlayer == 2, "draw two hits next player");

        runner = newRunner();
        runner.deck.clear();
        runner.discard.clear();
        runner.deck.add("R1");
        runner.deck.add("R2");
        runner.deck.add("R3");
        runner.deck.add("R4");
        runner.currentPlayer = 0;
        runner.direction = 1;
        runner.applyCardEffect("W4");
        passed += check(runner.players.get(1).hand.size() == 4 && runner.currentPlayer == 2, "wild draw four hits next player");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    private static int check(boolean condition, String name) {
        if (condition) {
            return 1;
        }
        fail(name);
        return 0;
    }

    private static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }

    private static GameRunner newRunner() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("A", false));
        players.add(new Player("B", false));
        players.add(new Player("C", false));
        ConsoleView view = new ConsoleView(true);
        ConsoleInput input = new ConsoleInput(new Scanner(System.in), view);
        return new GameRunner(players, new Random(1), input, view);
    }
}
