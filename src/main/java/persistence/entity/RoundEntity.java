package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rounds")
public class RoundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "winner_name", length = 100)
    private String winnerName;

    @Column(name = "winner_points", nullable = false)
    private int winnerPoints;

    @Column(name = "ended_at", nullable = false)
    private Instant endedAt;

    protected RoundEntity() {
    }

    public RoundEntity(GameEntity game, int roundNumber, String winnerName, int winnerPoints, Instant endedAt) {
        this.game = game;
        this.roundNumber = roundNumber;
        this.winnerName = winnerName;
        this.winnerPoints = winnerPoints;
        this.endedAt = endedAt;
    }

    public Long getId() {
        return id;
    }

    public GameEntity getGame() {
        return game;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public int getWinnerPoints() {
        return winnerPoints;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}
