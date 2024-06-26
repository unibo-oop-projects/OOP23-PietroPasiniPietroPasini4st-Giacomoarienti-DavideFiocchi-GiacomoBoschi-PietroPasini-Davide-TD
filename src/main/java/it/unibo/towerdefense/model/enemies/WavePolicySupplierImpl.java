package it.unibo.towerdefense.model.enemies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unibo.towerdefense.commons.dtos.enemies.EnemyType.EnemyArchetype;
import it.unibo.towerdefense.commons.dtos.enemies.EnemyType.EnemyLevel;
import it.unibo.towerdefense.commons.exceptions.ConfigurationLoadingException;
import it.unibo.towerdefense.commons.dtos.enemies.EnemyType;

/**
 * {@inheritDoc}.
 */
final class WavePolicySupplierImpl implements WavePolicySupplier {

    /**
     * This data is stored in separate maps so that different
     * criteria can be specified independently.
     */
    private final SortedMap<Integer, Predicate<EnemyType>> predicates;
    private final SortedMap<Integer, Integer> powerIncrements;
    private final SortedMap<Integer, Integer> rates;
    private final Map<Integer, Long> memo;

    /**
     * The constructor for the class.
     *
     * @param configFile the file from which to read the configuration.
     */
    WavePolicySupplierImpl(final String configFile) {
        final Triple<SortedMap<Integer, Predicate<EnemyType>>,
               SortedMap<Integer, Integer>,
               SortedMap<Integer, Integer>> configValues = loadConfig(
                configFile);

        checkConstraints(configValues);

        this.predicates = configValues.getLeft();
        this.powerIncrements = configValues.getMiddle();
        this.rates = configValues.getRight();
        this.memo = new HashMap<>();
    }

    /**
     * Method which separates the loading logic from the rest of the class.
     *
     * Changes to configuration file format only affect this part of the class.
     *
     * @param configString String containing configuration values.
     * @return a Triple containing the three sorted maps which represent the
     *         information stored in the file.
     */
    private Triple<SortedMap<Integer, Predicate<EnemyType>>, SortedMap<Integer, Integer>, SortedMap<Integer, Integer>> loadConfig(
            final String configString) {

        final SortedMap<Integer, Predicate<EnemyType>> types = new TreeMap<>();
        final SortedMap<Integer, Integer> powerIncrements = new TreeMap<>();
        final SortedMap<Integer, Integer> rates = new TreeMap<>();
        try {
            final JSONArray config = new JSONArray(configString);
            config.forEach(
                    (Object o) -> {
                        assert o instanceof JSONObject;
                        final JSONObject wave = (JSONObject) o;
                        final int waveNumber = wave.getInt("wave");
                        Optional.ofNullable(wave.optIntegerObject("power_increment", null))
                                .ifPresent((Integer i) -> powerIncrements.put(waveNumber, i));
                        Optional.ofNullable(wave.optIntegerObject("rate", null))
                                .ifPresent((Integer i) -> rates.put(waveNumber, i));
                        Optional.ofNullable(wave.optJSONArray("types", null)).ifPresent(
                                (JSONArray a) -> {
                                    types.put(waveNumber,
                                            translate(IntStream.range(0, a.length()).mapToObj(i -> a.getString(i))
                                                    .toList()));
                                });
                    });
            return Triple.of(types, powerIncrements, rates);
        } catch (JSONException e) {
            throw new ConfigurationLoadingException("Failed to load waves configuration from given String", e);
        }
    }

    /**
     * Method which incapsulates all the constraints a given configuration has to
     * respect.
     *
     * Will throw a RuntimeException if constraints are not respected.
     *
     * @param values the triple containing the configuration to check.
     */
    private void checkConstraints(
            final Triple<SortedMap<Integer, Predicate<EnemyType>>,
                         SortedMap<Integer, Integer>,
                         SortedMap<Integer, Integer>> values) {

        final SortedMap<Integer, Predicate<EnemyType>> types = values.getLeft();
        final SortedMap<Integer, Integer> powerIncrements = values.getMiddle();
        final SortedMap<Integer, Integer> rates = values.getRight();

        try {
            /*
             * Wave 1 is mandatory.
             */
            if (!(types.containsKey(1) && powerIncrements.containsKey(1) && rates.containsKey(1))) {
                throw new ConfigurationLoadingException("Wave 1 missing an element.");
            }

            /*
             * Configurations for waves < 0 are not permitted.
             */
            if (!types.headMap(1).isEmpty() || !powerIncrements.headMap(1).isEmpty() || !rates.headMap(1).isEmpty()) {
                throw new ConfigurationLoadingException("Wave number can't be < 1");
            }

            /*
             * Wave 1 must have some available enemy type.
             */
            if (!EnemyType.getEnemyTypes().stream().anyMatch(types.get(1))) {
                throw new ConfigurationLoadingException("Wave 1 has no available enemy type.");
            }

            /*
             * Power increments can't be < 0.
             */
            if (powerIncrements.values().stream().anyMatch(i -> i < 0)) {
                throw new ConfigurationLoadingException("Power increments can't be < 0.");
            }

            /*
             * Cant have rate <= 0.
             */
            if (rates.values().stream().anyMatch(i -> i < 0)) {
                throw new ConfigurationLoadingException("Power increments can't be < 0.");
            }
        } catch (ConfigurationLoadingException e) {
            throw new ConfigurationLoadingException("Values contained in configuration file for wave policies are not permitted.",
                    e);
        }
    }

    /**
     * Returns a predicate which tests true for any of the types which textual
     * representation e.g "IA" for level I type A are contained in list.
     *
     * @param list the list containing String representation of accepted types
     * @return the corresponding predicate
     */
    private static Predicate<EnemyType> translate(final List<String> list) {
        Predicate<EnemyType> ret = et -> false;
        for (final String type : list) {
            final EnemyLevel l = EnemyLevel.valueOf(type.substring(0, type.length() - 1));
            final EnemyArchetype t = EnemyArchetype.valueOf(type.substring(type.length() - 1));
            ret = ret.or(
                    et -> et.level().equals(l) && et.type().equals(t));
        }
        return ret;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Predicate<EnemyType> getPredicate(final Integer wave) {
        check(wave);
        return predicates.headMap(wave + 1).values().stream().reduce(et -> false, (p1, p2) -> p1.or(p2));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Long getPower(final Integer wave) {
        check(wave);
        final int lastIncrementWave = powerIncrements.headMap(wave + 1).lastKey();
        /*
         * we only memorize the powers for each wave in which changes the increase rate
         * and calculate the rest
         */
        return getPowerUpTo(lastIncrementWave)
                + ((long) powerIncrements.get(lastIncrementWave) * (wave - lastIncrementWave + 1));
    }

    /**
     * A memoized recursive function for calculating the power the wave before the
     * one given should have.
     *
     * @param wave the wave of which to compute the power
     * @return the computed power
     */
    private long getPowerUpTo(final int wave) {
        if (!memo.containsKey(wave)) {
            long power;
            if (wave == 1) {
                power = 0L;
            } else {
                final int previous = powerIncrements.headMap(wave).lastKey();
                power = getPowerUpTo(previous) + (powerIncrements.get(previous) * (wave - previous));
            }
            memo.put(wave, power);
        }
        return memo.get(wave);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Integer getCyclesPerSpawn(final Integer wave) {
        check(wave);
        return rates.get(rates.headMap(wave + 1).lastKey());
    }

    /**
     * Checks for the correctness of wave parameter.
     *
     * @param wave the integer to check, must be > 1
     */
    private void check(final Integer wave) {
        if (wave < 1) {
            throw new IllegalArgumentException("Wave numbers < 1 are not allowed.");
        }
    }
}
