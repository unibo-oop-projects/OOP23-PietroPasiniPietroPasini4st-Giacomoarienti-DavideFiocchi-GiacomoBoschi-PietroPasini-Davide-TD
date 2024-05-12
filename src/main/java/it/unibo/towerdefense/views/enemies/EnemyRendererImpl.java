package it.unibo.towerdefense.views.enemies;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

import it.unibo.towerdefense.commons.dtos.enemies.EnemyInfo;
import it.unibo.towerdefense.commons.dtos.enemies.EnemyType;
import it.unibo.towerdefense.commons.graphics.GameRenderer;
import it.unibo.towerdefense.commons.graphics.ImageDrawable;
import it.unibo.towerdefense.utils.images.ImageLoader;

/**
 * {@InheritDoc}.
 */
public class EnemyRendererImpl implements EnemyRenderer {

    private final static String ROOT = "it/unibo/towerdefense/views/enemies/";
    private final static String EXTENSION = ".png";
    private Map<EnemyType, Double> sizes;
    private Map<EnemyType, List<BufferedImage>> images;

    /**
     * Loads all the images from disk and saves them in a structure for quick access.
     *
     * @param loader the ImageLoader to load the images.
     */
    public EnemyRendererImpl(final ImageLoader loader) {
        images = new HashMap<>();
        sizes = EnemyType.getEnemyTypes().stream().collect(Collectors.toMap(et -> et, et -> 1.0));
        EnemyType.getEnemyTypes().forEach(et -> {
            try {
                BufferedImage source = loader.loadImage(ROOT + et.toString() + EXTENSION, sizes.get(et));
                images.put(et, List.of(
                    source,
                    Scalr.rotate(source, Rotation.CW_90),
                    Scalr.rotate(source, Rotation.CW_180),
                    Scalr.rotate(source, Rotation.CW_270)
                ));
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize the image for type " + et.toString(), e);
            }
        });
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void render(GameRenderer gameRenderer, List<EnemyInfo> enemies) {
        gameRenderer.submitAllToCanvas(enemies.stream().map(e -> getDrawable(e)).toList());
    }

    /**
     * Returns the correct image to render for a given enemy.
     */
    private ImageDrawable getDrawable(EnemyInfo enemy) {
        return new ImageDrawable(images.get(enemy.type()).get(enemy.direction().ordinal()), enemy.pos());
    }
}
