package it.unibo.towerdefense.commons.dtos.scoreboard;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.towerdefense.model.score.Score;
import it.unibo.towerdefense.model.scoreboard.Scoreboard;

/**
 * Class implementing the Data Transfer Object pattern for the Scoreboard.
 */
public class ScoreboardDTO {

    private final Set<Score> scores;

    /**
     * Constructor for ScoreboardDTO.
     * @param scoreboard the scoreboard
     */
    public ScoreboardDTO(final Scoreboard scoreboard) {
        this.scores = scoreboard.getScoreboard();
    }

    /**
     * Returns an unmodifiable Set of Scores.
     * @return the scores
     */
    public Set<Score> getScores() {
        return Collections.unmodifiableSet(this.scores);
    }

    /**
     * Returns the top ten scores.
     * @return the top ten scores
     */
    public Set<Score> getTopTenScores() {
        return this.scores.stream()
            .limit(10)
            .collect(Collectors.toSet());
    }
}
