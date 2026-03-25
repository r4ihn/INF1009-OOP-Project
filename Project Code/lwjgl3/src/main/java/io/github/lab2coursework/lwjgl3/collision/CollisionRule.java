package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;

// Common interface for all collision rules
public interface CollisionRule {

    // Check whether this rule applies
    boolean matches(Entity a, Entity b);

    // Apply the collision behaviour
    void resolve(Entity a, Entity b);
}
