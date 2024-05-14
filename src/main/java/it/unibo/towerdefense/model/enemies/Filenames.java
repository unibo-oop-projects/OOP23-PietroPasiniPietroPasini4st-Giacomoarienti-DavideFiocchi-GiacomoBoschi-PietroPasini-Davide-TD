package it.unibo.towerdefense.model.enemies;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class containing the names of configuration files used by the classes which
 * need them.
 * Stored this way to access them from test classes.
 */
abstract class Filenames {
    private static final String ROOT = "it/unibo/towerdefense/models/enemies/";
    private static final String WAVECONF = "waves.json";
    private static final String TYPESCONF = "types.json";

    /**
     * Returns the path of the waves config file.
     *
     * @return the path of the waves config file.
     */
    static Path wavesConfig() {
        try {
            return Paths.get(ClassLoader.getSystemResource(ROOT + WAVECONF).toURI());
        } catch (Throwable t) {
            throw new RuntimeException("Could not find file " + ROOT + WAVECONF);
        }
    }

    /**
     * Returns the path of the types config file.
     *
     * @return the path of the types config file.
     */
    static Path typesConfig() {
        try {
            return Paths.get(ClassLoader.getSystemResource(ROOT + TYPESCONF).toURI());
        } catch (Throwable t) {
            throw new RuntimeException("Could not find file " + ROOT + TYPESCONF);
        }
    }
}