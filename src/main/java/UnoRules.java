import java.util.ArrayList;

public final class UnoRules {
  static final int MISSED_UNO_PENALTY_CARDS = 2;

  private UnoRules() {}

  static void markPendingUnoCall(Player player) {
    player.pendingUnoCall = true;
  }

  static void resolveUnoCall(Player player) {
    player.pendingUnoCall = false;
    player.calledUno = true;
  }

  static void applyPendingPenalties(
      ArrayList<Player> players, DrawPile pile, ConsoleView view) {
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      if (player.pendingUnoCall) {
        for (int j = 0; j < MISSED_UNO_PENALTY_CARDS; j++) {
          player.hand.add(pile.draw());
        }
        view.showMissedUnoPenalty(player.name);
        player.pendingUnoCall = false;
        player.calledUno = false;
      }
    }
  }

  static boolean hasLegalPlay(ArrayList<String> hand, String upCard, String calledColor) {
    for (int i = 0; i < hand.size(); i++) {
      if (CardRules.isLegal(hand.get(i), upCard, calledColor)) {
        return true;
      }
    }
    return false;
  }

  static String findChampion(ArrayList<Player> players, int targetScore) {
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).score >= targetScore) {
        return players.get(i).name;
      }
    }
    return null;
  }
}
