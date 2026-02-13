package io.github.lab2coursework.lwjgl3;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class EntityManager {
    private List<Entity> entities;

    public EntityManager() {
        this.entities = new java.util.ArrayList<>();
    }

    public void addEntity(Entity entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }

    public void movement() {
        for (Entity entity : entities) {
            entity.movement();
        }
    }

    public void drawShapes(ShapeRenderer shape) {
        for (Entity entity : entities) {
            entity.draw(shape);
        }
    }

    public void drawSprites(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.draw(batch);
        }
    }

    public void update() {
        for (Entity entity : entities) {
            entity.update();
        }
    }


}
