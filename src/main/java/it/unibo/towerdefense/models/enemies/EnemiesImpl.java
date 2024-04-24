package it.unibo.towerdefense.models.enemies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import it.unibo.towerdefense.commons.LogicalPosition;
import it.unibo.towerdefense.controllers.enemies.EnemyInfo;
import it.unibo.towerdefense.controllers.map.MapController;

/**
 * @inheritDoc .
 */
public class EnemiesImpl implements Enemies {

    private Set<Enemy> enemies;
    private final MapController map;

    /**
     * Constructor for the class.
     *
     * @param width  of the game area in cells
     * @param height of the game area in cells
     * @param map    handle to get next positions
     */
    public EnemiesImpl(final MapController map) {
        this.enemies = new HashSet<>();
        this.map = map;
    }

    /**
     * @inheritDoc .
     */
    @Override
    public void move() {
        for(Enemy e: enemies){
            Optional<LogicalPosition> next = map.getNextPosition(e.getPosition(), e.getSpeed());
            if(next.isEmpty()){
                this.signalDeath(e);
            }else{
                e.move(next.get());
            }
        }
    }

    /**
     * @inheritDoc .
     */
    @Override
    public void signalDeath(final Enemy which) {
        enemies.remove(which);
    }

    /**
     * @inheritDoc .
     */
    @Override
    public Set<Enemy> getEnemies() {
        return Set.copyOf(enemies);
    }

    /**
     * @inheritDoc .
     */
    @Override
    public Collection<EnemyInfo> getEnemiesInfo() {
        return enemies.stream().map(e -> e.info()).toList();
    }

    /**
     * @inheritDoc .
     */
    @Override
    public void add(Enemy e) {
        enemies.add(e);
    }

    /**
     * @inheritDoc .
     */
    @Override
    public boolean areDead(){
        return enemies.isEmpty();
    }

}
