package lwjgl3.src.main.java.io.github.lab2coursework.lwjgl3.managers;

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
}
