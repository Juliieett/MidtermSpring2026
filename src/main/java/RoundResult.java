import java.time.Instant;

public class RoundResult {
    final String winnerName;
    final int winnerPoints;
    final Instant endedAt;

    RoundResult(String winnerName, int winnerPoints, Instant endedAt) {
        this.winnerName = winnerName;
        this.winnerPoints = winnerPoints;
        this.endedAt = endedAt;
    }
}
