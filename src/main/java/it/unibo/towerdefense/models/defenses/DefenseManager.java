package it.unibo.towerdefense.models.defenses;

import it.unibo.towerdefense.commons.dtos.DefenseDescription;
import it.unibo.towerdefense.commons.engine.LogicalPosition;
import it.unibo.towerdefense.controllers.SerializableModel;

import java.util.Map;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;


/**
 * Interface for the menager of the game defenses.
 */
public interface DefenseManager extends SerializableModel {

    /**returns description for build at given position.
     * @param at position to check
     * @return DefenseDescription for given pos.
    */
    DefenseDescription getDescriptionFor(LogicalPosition at);
    /**
     * Builds a tower of the given type.
     * @param choice index of buildable.
     * @param position the position of the defense.
     * @throws IOException
     */
    void buildDefense(int choice, LogicalPosition position) throws IOException;

    /**
     * Removes a defense.
     * @param position where the defense is located at.
     * @return the money you get from disassembling the build.
     * @throws IllegalArgumentException if there is no Defense at given position.
     */
    int disassembleDefense(LogicalPosition position);

    /**
     * Gets the possible buildable defenses on given position.
     * @param position the position where to build.
     * @return the possibles types of buildable defenses and their mapped cost.
     */
    List<DefenseDescription> getBuildables(LogicalPosition position) throws IOException;

    /**
     * makes the current ready defenses attack the available enemies.
     * @param availableTargets a list of the targets position and health.
     * @return the targets index and the amount of damage to deal (key=index and damage=value).
     */
    Map<Integer, Integer> attackEnemies(List<Pair<LogicalPosition, Integer>> availableTargets);
}