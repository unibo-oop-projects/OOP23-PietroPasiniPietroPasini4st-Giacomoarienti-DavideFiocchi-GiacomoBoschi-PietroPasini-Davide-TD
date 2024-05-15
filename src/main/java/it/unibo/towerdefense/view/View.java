package it.unibo.towerdefense.view;

import java.util.stream.Stream;

import it.unibo.towerdefense.commons.dtos.enemies.EnemyInfo;
import it.unibo.towerdefense.commons.engine.Size;
import it.unibo.towerdefense.controller.gamelauncher.GameLauncherController;
import it.unibo.towerdefense.controller.menu.MenuController;
import it.unibo.towerdefense.controller.savings.SavingsController;
import it.unibo.towerdefense.view.graphics.GameRenderer;

public interface View {

    void displayLauncher(GameLauncherController controller);

    void displayGame(Size size);

    void displayStartMenu(MenuController menuController);

    void displaySavings(SavingsController savingsController);

    void close();

    void createGameRenderer(Size size);

    GameRenderer getGameRenderer();

    void renderEnemies(GameRenderer gameRenderer, Stream<EnemyInfo> enemies);
}