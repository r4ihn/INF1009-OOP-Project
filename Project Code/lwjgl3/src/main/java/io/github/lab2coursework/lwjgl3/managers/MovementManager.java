package io.github.lab2coursework.lwjgl3.managers;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.movement.Movement;

import java.util.List;

public class MovementManager {

    private final List<Entity> entities;

    public MovementManager(List<Entity> entities) {
        this.entities = entities;
    }

    public void updateMovement(float deltaTime) {
        if (entities == null) return;

        for (Entity e : entities) {
            // 1. Grab the assigned strategy (PlayerMovement or AiMovement)
            Movement strategy = e.getMovementStrategy();

            // 2. If the entity has a strategy assigned, execute the movement math
            if (strategy != null) {
                strategy.update(e, deltaTime);
            }
        }
    }
}
