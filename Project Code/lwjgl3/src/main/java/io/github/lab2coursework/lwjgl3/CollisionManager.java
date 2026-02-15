package io.github.lab2coursework.lwjgl3;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {

    private final List<CollisionRule> rules = new ArrayList<>();

    public CollisionManager() {
        rules.add(new PhysicsCollisionRule());
    }

    public void applyAll(List<Entity> entities, float worldW, float worldH) {
        if (entities == null) return;

        CollisionRule bounds = new KeepInBoundsRule(worldW, worldH);
        for (Entity e : entities) {
            if (bounds.matches(e, null)) bounds.resolve(e, null);
        }

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity a = entities.get(i);
                Entity b = entities.get(j);

                for (CollisionRule rule : rules) {
                    if (rule.matches(a, b)) {
                        rule.resolve(a, b);
                    }
                }
            }
        }
    }
}
