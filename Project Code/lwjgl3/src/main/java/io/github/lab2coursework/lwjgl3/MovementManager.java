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
            // Tell the entity to process its movement strategy
            e.movement();
        }
    }
}
