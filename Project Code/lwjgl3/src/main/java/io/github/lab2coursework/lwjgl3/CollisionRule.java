package io.github.lab2coursework.lwjgl3;

public interface CollisionRule {
    boolean matches(Entity a, Entity b);

    void resolve(Entity a, Entity b);
}
