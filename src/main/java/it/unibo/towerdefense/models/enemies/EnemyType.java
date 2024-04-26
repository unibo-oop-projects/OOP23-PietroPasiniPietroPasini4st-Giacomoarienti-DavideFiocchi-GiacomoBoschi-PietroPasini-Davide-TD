package it.unibo.towerdefense.models.enemies;

import it.unibo.towerdefense.controllers.enemies.EnemyArchetype;
import it.unibo.towerdefense.controllers.enemies.EnemyLevel;

/**
 * Interface which models an enemy in its abstract form.
 */
interface EnemyType {
    /**
     * Getter for the max HP.
     *
     * @return the initial HP for the EnemyType
     */
    int getMaxHP();

    /**
     * Getter for the speed.
     *
     * @return the speed for the EnemyType expressed in 1/SCALING_FACTOR cells per cycle
     */
    int getSpeed();

    /**
     * Returns the EnemyArchetype of the enemy.
     *
     * @return the EnemyArchetype of the enemy
     */
    EnemyArchetype getEnemyArchetype();

    /**
     * Returns the EnemyLevel of the enemy.
     *
     * @return the EnemyLevel of the enemy
     */
    EnemyLevel getEnemyLevel();
}
