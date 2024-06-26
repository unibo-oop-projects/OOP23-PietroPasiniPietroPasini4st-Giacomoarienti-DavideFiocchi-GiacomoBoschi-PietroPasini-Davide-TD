package it.unibo.towerdefense.model.map;

import it.unibo.towerdefense.commons.api.JsonSerializable;
import it.unibo.towerdefense.commons.engine.LogicalPosition;

/**
 * Interface that model a Cell, the square spatial unit of game.
 */
public interface Cell extends JsonSerializable {

    /**
     *I getter.
     * @return the horizontal index of the cell in the map.
     */
    int getX();

    /**
     *J getter.
     * @return the vertical index of the cell in the map.
     */
    int getY();

    /**
     * Returns true if this cell contains the specified position.
     * @param position whose presence in this cell is to be tested
     * @return true if this cell contains the specified position
     */
    boolean contains(LogicalPosition position);

    /**
     * Cell center position getter.
     * @return the LogicalPosition of centre of cell.
     */
    LogicalPosition getCenter();

}
