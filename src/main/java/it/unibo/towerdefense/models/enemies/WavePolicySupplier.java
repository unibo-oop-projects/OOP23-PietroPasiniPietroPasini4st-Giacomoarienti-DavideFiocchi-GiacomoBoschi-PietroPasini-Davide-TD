package it.unibo.towerdefense.models.enemies;

import java.util.function.Predicate;

/**
 * Determines how waves are created (what type and how many enemies they contain and spawn rate).
 */
public interface WavePolicySupplier {
    /**
     * Returns a predicate returning true for admitted Types.
     * @param wave the wave about which information is asked
     * @return the predicate
     */
    Predicate<EnemyType> getPredicate(final Integer wave);

    /**
     * Returns how many enemies the wave should contain.
     * @param wave the wave about which information is asked
     * @return the number of enemies
     */
    Integer getLength(final Integer wave);

    /**
     * Returns how many cycles pass between two spawns (counting the cycle in which the first enemy spawns).
     * @param wave the wave about which information is asked
     * @return the number of cycles
     */
    Integer getCyclesPerSpawn(final Integer wave);
}
