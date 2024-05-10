package it.unibo.towerdefense.controllers.mediator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import it.unibo.towerdefense.controllers.Controller;
import it.unibo.towerdefense.controllers.SerializableController;
import it.unibo.towerdefense.controllers.defenses.DefensesController;
import it.unibo.towerdefense.controllers.defenses.DefensesControllerImpl;
import it.unibo.towerdefense.controllers.enemies.EnemyController;
import it.unibo.towerdefense.controllers.enemies.EnemyControllerImpl;
import it.unibo.towerdefense.controllers.game.GameController;
import it.unibo.towerdefense.controllers.game.GameControllerImpl;
import it.unibo.towerdefense.controllers.map.MapController;
import it.unibo.towerdefense.controllers.map.MapControllerImpl;
import it.unibo.towerdefense.models.engine.Size;
import it.unibo.towerdefense.models.game.GameDTO;
import it.unibo.towerdefense.models.savingloader.SavingLoaderImpl;
import it.unibo.towerdefense.models.savingloader.saving.Saving;
import it.unibo.towerdefense.models.savingloader.saving.SavingFieldsEnum;
import it.unibo.towerdefense.models.savingloader.saving.SavingImpl;
import it.unibo.towerdefense.utils.images.ImageLoader;
import it.unibo.towerdefense.views.graphics.GameRenderer;

/**
 * Class that implements the ControllerMediator interface.
 */
public class ControllerMediatorImpl implements ControllerMediator {

    private final GameRenderer gameRenderer;
    private final GameController gameController;
    private final MapController mapController;
    private final DefensesController defensesController;
    private final EnemyController enemiesController;
    private List<Controller> controllers;
    private Map<SavingFieldsEnum, SerializableController> serializableControllers;

    /**
     * Initializes the different controllers and binds itself to them.
     * @param playerName the player name
     * @param mapSize the size of the map
     * @param renderer the game renderer
     */
    public ControllerMediatorImpl(
        final String playerName,
        final Size mapSize,
        final GameRenderer renderer
    ) {
        this.gameRenderer = renderer;
        // initialize controllers
        this.gameController = new GameControllerImpl(playerName);
        this.mapController = new MapControllerImpl(mapSize, this);
        this.defensesController = new DefensesControllerImpl(this);
        this.enemiesController = new EnemyControllerImpl(this);
        // add controllers to the list
        this.addControllersToLists();
    }

    /**
     * Constructor from Saving.
     * Initializes the controllers based on the state provided by the Saving.
     * @param saving the saving object
     * @param renderer the game renderer
     */
    public ControllerMediatorImpl(final Saving saving, final GameRenderer renderer) {
        this.gameRenderer = renderer;
        // initialize controllers
        this.gameController = new GameControllerImpl(
            GameDTO.fromJson(saving.getGameJson())
        );
        this.mapController = new MapControllerImpl(saving.getMapJson(), this);
        this.defensesController = null; //new DefensesControllerImpl(saving, this);
        this.enemiesController = new EnemyControllerImpl(this);
        // add controllers to the list
        this.addControllersToLists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // calls update on each controller
        this.controllers.forEach(Controller::update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        // calls render on each controller
        this.controllers.forEach(
            controller -> controller.render(this.gameRenderer)
        );
        // force repaint
        this.gameRenderer.renderCanvas();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        // create saving instance
        final var json = this.toJSON();
        final SavingImpl saving = new SavingImpl(json);
        // write saving
        try {
            final SavingLoaderImpl savingLoader = new SavingLoaderImpl(
                this.gameController.getPlayerName()
            );

            if(!savingLoader.writeSaving(saving)) {
                // TODO handle error
            }
        } catch (final IOException e) {
            // TODO handle error
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageLoader getImageLoader() {
        return this.gameRenderer.getImageLoader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameController getGameController() {
        return this.gameController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapController getMapController() {
        return this.mapController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefensesController getDefensesController() {
        return this.defensesController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnemyController getEnemyController() {
        return this.enemiesController;
    }

    private Map<SavingFieldsEnum, String> toJSON() {
        return this.serializableControllers.entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Entry::getKey,
                    entry -> entry.getValue().toJSON()
                )
            );
    }

    private void addControllersToLists() {
        // add controllers to the list
        this.controllers = List.of(
            this.gameController,
            this.mapController,
            this.defensesController,
            this.enemiesController
        );
        // save serializable controllers
        this.serializableControllers = Map.of(
            SavingFieldsEnum.GAME, gameController,
            SavingFieldsEnum.MAP, mapController,
            SavingFieldsEnum.DEFENSES, defensesController
        );
    }
}
