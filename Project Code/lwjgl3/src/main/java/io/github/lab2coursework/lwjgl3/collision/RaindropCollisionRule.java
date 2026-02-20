package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Circle;
import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.Triangle;
import io.github.lab2coursework.lwjgl3.graphics.TextureObject;
import io.github.lab2coursework.lwjgl3.movement.AIMovement;

import java.util.concurrent.ThreadLocalRandom;

public class RaindropCollisionRule implements CollisionRule {

    private final float worldW;
    private final float worldH;

    public RaindropCollisionRule(float worldW, float worldH) {
        this.worldW = worldW;
        this.worldH = worldH;
    }

    @Override
    public boolean matches(Entity a, Entity b) {
        if (a == null || b == null) return false;
        return (isRaindrop(a) && isShape(b)) || (isRaindrop(b) && isShape(a));
    }

    @Override
    public void resolve(Entity a, Entity b) {
        Entity drop = isRaindrop(a) ? a : b;
        Entity shape = (drop == a) ? b : a;

        if (!areOverlapping(drop, shape)) return;

        float dropW = getDropWidth(drop);
        float safeMinX = 0f;
        float safeMaxX = Math.max(0f, worldW - dropW);

        float newX = rand(safeMinX, safeMaxX);

        drop.setX(newX);
        drop.setY(worldH);
    }

    private boolean isRaindrop(Entity e) {
        return (e instanceof TextureObject) && (e.getMovementStrategy() instanceof AIMovement);
    }

    private boolean isShape(Entity e) {
        return (e instanceof Circle) || (e instanceof Triangle);
    }

    private boolean areOverlapping(Entity drop, Entity shape) {
        float rx = drop.getX();
        float ry = drop.getY();
        float rw = getDropWidth(drop);
        float rh = getDropHeight(drop);

        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            return rectCircleOverlap(rx, ry, rw, rh, c.getX(), c.getY(), c.getRadius());
        }

        if (shape instanceof Triangle) {
            float tx = shape.getX();
            float ty = shape.getY();
            return rectRectOverlap(rx, ry, rw, rh, tx, ty, 100f, 100f);
        }

        return false;
    }

    private float getDropWidth(Entity drop) {
        if (drop instanceof TextureObject) {
            TextureObject t = (TextureObject) drop;
            if (t.getTexture() != null) return t.getTexture().getWidth();
        }
        return 32f; // fallback
    }

    private float getDropHeight(Entity drop) {
        if (drop instanceof TextureObject) {
            TextureObject t = (TextureObject) drop;
            if (t.getTexture() != null) return t.getTexture().getHeight();
        }
        return 32f; // fallback
    }

    private boolean rectRectOverlap(
        float ax, float ay, float aw, float ah,
        float bx, float by, float bw, float bh
    ) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    private boolean rectCircleOverlap(
        float rx, float ry, float rw, float rh,
        float cx, float cy, float cr
    ) {
        float closestX = clamp(cx, rx, rx + rw);
        float closestY = clamp(cy, ry, ry + rh);
        float dx = cx - closestX;
        float dy = cy - closestY;
        return (dx * dx + dy * dy) <= (cr * cr);
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    private float rand(float min, float max) {
        if (max <= min) return min;
        return (float) ThreadLocalRandom.current().nextDouble(min, max);
    }
}
