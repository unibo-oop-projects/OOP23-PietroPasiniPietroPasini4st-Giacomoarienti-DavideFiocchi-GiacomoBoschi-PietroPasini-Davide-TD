package it.unibo.towerdefense.controllers.gameMap;

import java.util.NoSuchElementException;
import java.util.Optional;

import it.unibo.towerdefense.models.engine.Position;
import it.unibo.towerdefense.models.engine.Size;
import it.unibo.towerdefense.models.map.BuildableCell;
import it.unibo.towerdefense.models.map.GameMap;
import it.unibo.towerdefense.models.map.GameMapImpl;
import it.unibo.towerdefense.models.map.PathCell;

/**
 * Class to interact with map methods.
 */
public class MapControllerImpl implements MapController {

    private final GameMap map;
    private BuildableCell selected = null;

    /**
     *Constructor from size of map in two unit of measure.
     * @param sizeInPixel pixel sizes of part of screen occupied by map.
     * @param sizeInCell size of map in terms of game
     */
    public MapControllerImpl(final Size sizeInPixel, final Size sizeInCell) {
        this.map = new GameMapImpl(sizeInPixel, sizeInCell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathCell getSpawnCell() {
        return map.getSpawnCell();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PathCell> getNext(final PathCell current) {
        try {
            return Optional.of(map.getNext(current));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(final Position position) {
        if (selected.contains(position)) {
            selected = null;
        } else {
            var c = map.getCellAt(position);
                if (c instanceof BuildableCell) {
                    selected = (BuildableCell) c;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BuildableCell> getSelected() {
        return selected == null ? Optional.empty() : Optional.of(selected);
    }

}
