package io.github.lab2coursework.lwjgl3;

import java.util.List;

public class MovementManager {

    // As per UML: - entities: List<Entity>
    private List<Entity> entities;

    public MovementManager(List<Entity> entities) {
        this.entities = entities;
    }

    public void updateMovement() {
        if (entities == null) return;

        for (Entity e : entities) {
            // 1. Grab the assigned strategy (PlayerMovement or AiMovement)
            Movement strategy = e.getMovementStrategy();

            // 2. If the entity has a strategy assigned, execute the movement math
            if (strategy != null) {
                strategy.update(e);
            }
        }
    }
}
