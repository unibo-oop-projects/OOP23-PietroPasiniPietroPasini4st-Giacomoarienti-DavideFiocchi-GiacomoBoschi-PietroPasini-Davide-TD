package it.unibo.towerdefense.controllers.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import it.unibo.towerdefense.commons.LogicalPosition;
import it.unibo.towerdefense.controllers.defenses.DefenseType;
import it.unibo.towerdefense.controllers.defenses.DefensesController;
import it.unibo.towerdefense.controllers.game.GameController;
import it.unibo.towerdefense.models.engine.Position;
import it.unibo.towerdefense.models.engine.PositionImpl;
import it.unibo.towerdefense.models.engine.Size;
import it.unibo.towerdefense.models.map.BuildableCell;
import it.unibo.towerdefense.models.map.Cell;
import it.unibo.towerdefense.models.map.Direction;
import it.unibo.towerdefense.models.map.GameMap;
import it.unibo.towerdefense.models.map.GameMapImpl;
import it.unibo.towerdefense.models.map.PathCell;
import it.unibo.towerdefense.views.graphics.GameRenderer;

/**
 * Class to interact with map methods.
 */
public class MapControllerImpl implements MapController {

    private final GameMap map;
    private final GameController gameController;
    private final DefensesController defensesController;
    private BuildableCell selected = null;
    private List<Entry<DefenseType, Integer>> options;

    /**
     *Constructor from size of map in two unit of measure.
     * @param size size of map in terms of game cells.
     * @param defensesController the defenses controller.
     * @param gameController the game controller.
     */
    public MapControllerImpl(final Size size, final DefensesController defensesController, final GameController gameController) {
        try {
            this.map = new GameMapImpl(size);
        } catch (Exception e) {
            throw e;
        }
        this.gameController = gameController;
        this.defensesController = defensesController;
    }

    /**
     *Constructor from size of map in two unit of measure.
     * @param jsondata JSON representation of GameMap Object.
     * @param defensesController the defenses controller.
     * @param gameController the game controller.
     */
    public MapControllerImpl(
        final String jsondata,
        final DefensesController defensesController,
        final GameController gameController
    ) {
        this.map = GameMapImpl.fromJson(jsondata);
        this.gameController = gameController;
        this.defensesController = defensesController;
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
    public LogicalPosition getSpawnPosition() {
        return map.getSpawnCell().inSideMidpoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogicalPosition getEndPosition() {
        return map.getEndCell().inSideMidpoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(final Position position) {
        Cell c = map.getCellAt(position);
        if (c.equals(selected)) {
            selected = null;
        } else {
            if (c instanceof BuildableCell && ((BuildableCell) c).isBuildable()) {
                selected = (BuildableCell) c;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Position> getSelected() {
        return selected == null ? Optional.empty() : Optional.of(new PositionImpl(selected.getX(), selected.getY()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LogicalPosition> getNextPosition(final LogicalPosition pos, final int distanceToMove) {

        if (map.getEndCell().contains(pos)) {
            return Optional.empty();
        }
        Position cellPos = new PositionImpl(pos.getCellX(), pos.getCellY());
        Cell cell = map.getCellAt(cellPos);
        if (cell == null || !(cell instanceof PathCell)) {
            throw new IllegalArgumentException("position must belong to a PathCell");
        }
        LogicalPosition tempPos = pos;
        PathCell pCell = (PathCell) cell;
        Direction dir = pCell.getInDirection();
        int remaningDistance = distanceToMove;

        for (int i = 2; i > 0; i--) {

            int positionInCell = realModule(
                pos.getX() * dir.orizontal() + pos.getY() * dir.vertical(),
                LogicalPosition.SCALING_FACTOR / 2 * i
            );
            int factor = LogicalPosition.SCALING_FACTOR / i;

            if (positionInCell < factor) {
                int distanceToTravel = factor - positionInCell;
                if (remaningDistance < distanceToTravel) {
                    tempPos = addDistance(tempPos, dir, remaningDistance);
                    if (map.getEndCell().contains(tempPos)) {
                        return Optional.empty();
                    }
                    return Optional.of(tempPos);
                }
                remaningDistance -=  distanceToTravel;
                tempPos = addDistance(tempPos, dir, distanceToTravel);
            }
            dir = pCell.getOutDirection();
        }
        return getNextPosition(tempPos, remaningDistance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(final int optionNumber) {
        if (selected == null || options.isEmpty() || optionNumber > options.size() - 1) {
            throw new IllegalStateException("ERROR, can't build!");
        }
        var choice = options.get(optionNumber);
        if (choice.getKey() == DefenseType.NOTOWER) {
            defensesController.disassembleDefense(selected.getCenter());
            gameController.addMoney(choice.getValue());
            return;
        }
        if (!gameController.purchase(choice.getValue())) {
            throw new IllegalArgumentException("Not enought money!");
        }
        defensesController.buildDefense(choice.getKey(), selected.getCenter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Integer>> getBuildingOptions() {
        options = requestBuildinOption();
        return options.stream().map(e -> Pair.of(e.getKey().toString(), e.getValue())).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMapJSON() {
        return map.toJSON();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(final GameRenderer renderer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    private  List<Map.Entry<DefenseType, Integer>> requestBuildinOption() {
        if (selected == null) {
            return List.of();
        }
        return defensesController.getBuildables(selected.getCenter()).entrySet().stream().toList();
    }

    /**
     * Do module operation as it should work, also if operand are negatives.
     * @param a dividend
     * @param b divisor
     * @return rest of integer division
     */
    private static int realModule(final int a, final int b) {
        return (a % b + b) % b;
    }

    private static LogicalPosition addDistance(final LogicalPosition pos, final Direction dir, final int distance) {
        return new LogicalPosition(pos.getX() + distance * dir.orizontal(), pos.getY() + distance * dir.vertical());
    }
}
