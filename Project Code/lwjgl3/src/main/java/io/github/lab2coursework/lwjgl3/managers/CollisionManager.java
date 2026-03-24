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
        this.rules = new ArrayList<>();
    }

    public CollisionManager(List<CollisionRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    // Add a new collision rule
    public void addRule(CollisionRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    // Apply all matching rules to a pair of entities
    public void applyTo(Entity first, Entity second) {
        for (CollisionRule rule : rules) {
            if (rule.matches(first, second)) {
                rule.resolve(first, second);
            }
        }
    }

    // Apply boundary handling to one entity
    public void applyBounds(Entity entity, float worldW, float worldH) {
        CollisionRule boundsRule = new KeepInBoundsRule(worldW, worldH);
        if (boundsRule.matches(entity, null)) {
            boundsRule.resolve(entity, null);
        }
    }

    // Apply bounds first, then pairwise collision rules
    public void applyAll(List<Entity> entities, float worldW, float worldH) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        for (Entity entity : entities) {
            applyBounds(entity, worldW, worldH);
        }

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                applyTo(entities.get(i), entities.get(j));
            }
        }
    }
}
