package it.unibo.towerdefense;

import java.io.IOException;

import it.unibo.towerdefense.utils.file.FileUtils;
import it.unibo.towerdefense.views.window.Window;
import it.unibo.towerdefense.views.window.WindowImpl;
import it.unibo.towerdefense.controllers.game.GameController;
import it.unibo.towerdefense.controllers.game.GameControllerImpl;

/**
 * The main class for the Tower Defense game.
 * This class contains the entry point of the game and initializes the game components.
 */
public final class TowerDefense {

    /**
     * Zero-argument constructor.
     */
    private TowerDefense() {
    }

    /**
     * The main entry point of the Tower Defense application.
     * @param args the command line arguments
     * @throws IOException if the game folder cannot be created
     */
    public static void main(final String[] args) throws IOException {
        // create game folder
        FileUtils.createGameFolder();
        // instantiate the game window
        final Window window = new WindowImpl();
        // instantiate the game controller and run it
        final GameController gameController = new GameControllerImpl(window);
        gameController.run();
    }
}
