package it.unibo.towerdefense.models.game;

import it.unibo.towerdefense.models.JsonSerializable;

public interface GameDTO extends JsonSerializable {

    /**
     * Getter for the lives.
     * @return the amount of lives of the player
     */
    int getLives();

    /**
     * Getter for the money.
     * @return the amount of money the player has
     */
    int getMoney();

    /**
     * Getter for the wave.
     * @return the wave number
     */
    int getWave();

    /**
     * Returns the GameDTO object from JSON string.
     * @param jsonData the JSON representation
     * @return the GameDTO object
     */
    static GameDTO fromJson(final String jsonData) {
        return GameDTOImpl.fromJson(jsonData);
    }
}