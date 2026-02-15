package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import java.util.List;

public class CollisionManager {
    public void keepEntitiesInBounds(List<Entity> entities, float worldW, float worldH) {
        if (entities == null) return;

        for (Entity e : entities) {
            keepOneInBounds(e, worldW, worldH);
        }
    }

    private void keepOneInBounds(Entity e, float worldW, float worldH) {
        if (e == null) return;

        float minX = 0, minY = 0;
        float maxX = worldW, maxY = worldH;

        if(e instanceof Circle){
            Circle c = (Circle) e;
            float r = c.getRadius();
            minX = r;
            minY = r;
            maxX = worldW - r;
            maxY = worldH - r;
        } else if (e instanceof Triangle) {
            maxX = worldW - 100;
            maxY = worldH - 100;
        } else if (e instanceof TextureObject) {
            TextureObject t = (TextureObject) e;
            Texture tex = t.getTexture();
            if (tex != null){
                maxX = worldW - tex.getWidth();
                maxY = worldH - tex.getHeight();
            }
        }

        if (e.getX() < minX) e.setX(minX);
        if (e.getX() > maxX) e.setX(maxX);
        if (e.getY() < minY) e.setY(minY);
        if (e.getY() > maxY) e.setY(maxY);
    }

    public void resolveEntityCollisions(List<Entity> entities) {
        if (entities == null) return;

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity a = entities.get(i);
                Entity b = entities.get(j);

                if (a instanceof Circle && b instanceof Triangle) {
                    separateCircleFromTriangle((Circle) a, (Triangle) b);
                } else if (a instanceof Triangle && b instanceof Circle) {
                    separateCircleFromTriangle((Circle) b, (Triangle) a);
                }
            }
        }
    }

    private void separateCircleFromTriangle(Circle c, Triangle t) {
        float left   = t.getX();
        float right  = t.getX() + 100f;
        float bottom = t.getY();
        float top    = t.getY() + 100f;

        float cx = c.getX();
        float cy = c.getY();
        float r  = c.getRadius();

        float closestX = clamp(cx, left, right);
        float closestY = clamp(cy, bottom, top);

        float dx = cx - closestX;
        float dy = cy - closestY;

        float dist2 = dx * dx + dy * dy;
        float r2 = r * r;

        if (dist2 < r2) {
            float dist = (float)Math.sqrt(dist2);

            if (dist == 0f) {
                // push upwards by default
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
