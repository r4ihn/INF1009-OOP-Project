package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Circle;
import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.Triangle;

public class PhysicsCollisionRule implements CollisionRule {

    // Checks if entities collide
    @Override
    public boolean matches(Entity a, Entity b) {
        return (a instanceof Circle && b instanceof Triangle) || (a instanceof Triangle && b instanceof Circle);
    }

    // Calculates the outcome of entity movement when collision occurs
    @Override
    public void resolve(Entity a, Entity b) {
        Circle c;
        Triangle t;

        if (a instanceof Circle) {
            c = (Circle) a;
            t = (Triangle) b;
        } else {
            c = (Circle) b;
            t = (Triangle) a;
        }

        float left = t.getX();
        float right = t.getX() + 100f;
        float bottom = t.getY();
        float top = t.getY() + 100f;

        float cx = c.getX();
        float cy = c.getY();
        float r = c.getRadius();

        float closestX = clamp(cx, left, right);
        float closestY = clamp(cy, bottom, top);

        float dx = cx - closestX;
        float dy = cy - closestY;

        float dist2 = dx * dx + dy * dy;
        float r2 = r * r;

        // Circle gets pushed by triangle
        if (dist2 < r2) {
            float dist = (float) Math.sqrt(dist2);

            if (dist == 0f) {
                c.setY(c.getY() + r);
                return;
            }

            float penetration = r - dist;
            float nx = dx / dist;
            float ny = dy / dist;

            c.setX(c.getX() + nx * penetration);
            c.setY(c.getY() + ny * penetration);
        }
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
