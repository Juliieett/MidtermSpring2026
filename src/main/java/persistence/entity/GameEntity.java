package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at", nullable = false)
    private Instant endedAt;

    @Column(name = "rounds_played", nullable = false)
    private int roundsPlayed;

    @Column(name = "winner_name", nullable = false, length = 100)
    private String winnerName;

    @OneToMany(mappedBy = "game")
    private List<RoundEntity> rounds = new ArrayList<RoundEntity>();

    @OneToMany(mappedBy = "game")
    private List<ScoreEntity> scores = new ArrayList<ScoreEntity>();

    protected GameEntity() {
    }

    public GameEntity(Instant startedAt, Instant endedAt, int roundsPlayed, String winnerName) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.roundsPlayed = roundsPlayed;
        this.winnerName = winnerName;
    }

    public Long getId() {
        return id;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public String getWinnerName() {
        return winnerName;
    }
}
