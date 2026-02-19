package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;

public interface CollisionRule {
    // Decides the entities affected
    boolean matches(Entity a, Entity b);

    // Decides what to do when collision occurs
    void resolve(Entity a, Entity b);
}
