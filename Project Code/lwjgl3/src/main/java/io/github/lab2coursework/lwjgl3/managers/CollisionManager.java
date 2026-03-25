package io.github.lab2coursework.lwjgl3.managers;

import io.github.lab2coursework.lwjgl3.collision.CollisionRule;
import io.github.lab2coursework.lwjgl3.collision.KeepInBoundsRule;
import io.github.lab2coursework.lwjgl3.entities.Entity;

import java.util.ArrayList;
import java.util.List;

// Coordinates collision rules without storing collision logic itself
public class CollisionManager {

    private final List<CollisionRule> rules;

    public CollisionManager() {
        this(new ArrayList<>());
    }

    public CollisionManager(List<CollisionRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public void addRule(CollisionRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    public void applyTo(Entity first, Entity second) {
        for (CollisionRule rule : rules) {
            if (rule.matches(first, second)) {
                rule.resolve(first, second);
            }
        }
    }

    public void applyBounds(Entity entity, CollisionRule boundsRule) {
        if (boundsRule.matches(entity, null)) {
            boundsRule.resolve(entity, null);
        }
    }

    public void applyAll(List<Entity> entities, float worldW, float worldH) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        CollisionRule boundsRule = new KeepInBoundsRule(worldW, worldH);
        for (Entity entity : entities) {
            applyBounds(entity, boundsRule);
        }

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                applyTo(entities.get(i), entities.get(j));
            }
        }
    }
}
