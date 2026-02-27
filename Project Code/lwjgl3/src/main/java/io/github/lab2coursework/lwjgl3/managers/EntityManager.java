package io.github.lab2coursework.lwjgl3.managers;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.entities.Entity;

public class EntityManager {
    private List<Entity> entityList = new ArrayList<>();

    public void addEntities(Entity entity) {
        if (entity != null){
            entityList.add(entity);
        }
    }

    public List<Entity> getEntities() {
        return entityList;
    }

    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        for (Entity entity : entityList) {
            // Only draw sprites if a batch is provided
            if (batch != null && batch.isDrawing()) {
                entity.draw(batch);
            }
            // Only draw shapes if a renderer is provided
            if (shape != null && shape.isDrawing()) {
                entity.draw(shape);
            }
        }
    }

    public void update() {
        for (Entity entity : entityList) {
            entity.update(); // Call individual entity logic if defined
        }
    }
}
