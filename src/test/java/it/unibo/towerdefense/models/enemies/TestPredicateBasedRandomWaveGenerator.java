package it.unibo.towerdefense.models.enemies;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.towerdefense.controllers.enemies.EnemyArchetype;
import it.unibo.towerdefense.controllers.enemies.EnemyLevel;
import it.unibo.towerdefense.controllers.enemies.EnemyType;

public class TestPredicateBasedRandomWaveGenerator {

    private final static String ROOT = "it/unibo/towerdefense/models/enemies/Test_";
    private PredicateBasedRandomWaveGenerator rwg;
    private WavePolicySupplierImpl wps;
    private ConfigurableEnemyCatalogue catalogue;

    @BeforeEach
    void init(){
        wps = new WavePolicySupplierImpl(ROOT + "waves.json");
        catalogue = new ConfigurableEnemyCatalogue(ROOT + "types.json");
        rwg = new PredicateBasedRandomWaveGenerator(wps, catalogue);
    }

    @Test
    void testWaves(){
        for(int i = 1; i < 10000; i++){
            testWave(i);
        }
    }

    private void testWave(int wave) {

        record QuickEnemyType(EnemyLevel level, EnemyArchetype type) implements EnemyType{};

        int length = wps.getLength(wave);
        int rate = wps.getCyclesPerSpawn(wave);
        Wave generated = rwg.apply(wave);
        for(int i = 0; i < length * rate - (rate - 1); i++){
            Assertions.assertTrue(generated.hasNext(), () -> "Didn't have next");
            if(i % rate == 0){
                Optional<RichEnemyType> current = generated.next();
                Assertions.assertTrue(current.isPresent(), () -> "Was not present");
                Assertions.assertTrue(wps.getPredicate(wave).test(new QuickEnemyType(current.get().level(), current.get().type())), () -> "Was not right type");
            }else{
                Assertions.assertTrue(generated.next().isEmpty(), () -> "Was present");
            }
        }
    }
}
