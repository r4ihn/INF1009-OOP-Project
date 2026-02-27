package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Circle;
import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.Raindrop;
import io.github.lab2coursework.lwjgl3.entities.Triangle;
import io.github.lab2coursework.lwjgl3.graphics.TextureObject;

public class RaindropCollisionRule implements CollisionRule {

    @Override
    public boolean matches(Entity a, Entity b) {
        if (a == null || b == null) return false;

        boolean aDrop = a instanceof Raindrop;
        boolean bDrop = b instanceof Raindrop;

        // exactly one must be a raindrop
        if (aDrop == bDrop) return false;

        Entity other = aDrop ? b : a;
        return (other instanceof Circle) || (other instanceof Triangle);
    }

    @Override
    public void resolve(Entity a, Entity b) {
        Raindrop drop = (a instanceof Raindrop) ? (Raindrop) a : (Raindrop) b;
        Entity other = (drop == a) ? b : a;

        boolean hit = false;

        if (other instanceof Circle) {
            hit = rectHitsCircle(drop, (Circle) other);
        } else if (other instanceof Triangle) {
            hit = rectHitsTriangle(drop, (Triangle) other);
        }

        if (hit) despawn(drop);
    }

    // droplet is a sprite => treat as rectangle
    private boolean rectHitsCircle(TextureObject r, Circle c) {
        float rx = r.getX();
        float ry = r.getY();
        float rw = r.getTexture().getWidth();
        float rh = r.getTexture().getHeight();

        float cx = c.getX();
        float cy = c.getY();
        float cr = c.getRadius();

        float closestX = clamp(cx, rx, rx + rw);
        float closestY = clamp(cy, ry, ry + rh);

        float dx = cx - closestX;
        float dy = cy - closestY;

        return (dx * dx + dy * dy) < (cr * cr);
    }

    // your Triangle collision model uses a 100x100 footprint (same as your other rules)
    private boolean rectHitsTriangle(TextureObject r, Triangle t) {
        float rx = r.getX();
        float ry = r.getY();
        float rw = r.getTexture().getWidth();
        float rh = r.getTexture().getHeight();

        float tx = t.getX();
        float ty = t.getY();
        float tw = 100f;
        float th = 100f;

        return rx < tx + tw && rx + rw > tx && ry < ty + th && ry + rh > ty;
    }

    // engine-safe "disappear": move far away + stop moving
    private void despawn(Entity e) {
        e.setX(-10000f);
        e.setY(-10000f);
        e.setSpeed(0f);
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
