package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Contract for one collision behavior.
 * The manager asks each rule if it applies, then resolves matching ones.
 */
public interface CollisionRule {

    /** Returns true if this rule should handle the given pair. */
    boolean matches(Entity a, Entity b);

    /** Applies the collision response. */
    void resolve(Entity a, Entity b);
}
