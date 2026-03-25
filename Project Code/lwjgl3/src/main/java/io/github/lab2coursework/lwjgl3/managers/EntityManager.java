package io.github.lab2coursework.lwjgl3.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private final List<Entity> entityList = new ArrayList<>();

    public void addEntity(Entity entity) {
        if (entity != null) {
            entityList.add(entity);
        }
    }

    public void addEntities(Entity entity) {
        addEntity(entity);
    }

    public List<Entity> getEntities() {
        return entityList;
    }

    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        for (Entity entity : entityList) {
            if (batch != null && batch.isDrawing()) {
                entity.draw(batch);
            }
            if (shape != null && shape.isDrawing()) {
                entity.draw(shape);
            }
        }
    }

    public void update() {
        for (Entity entity : entityList) {
            entity.update();
        }
    }

    public void removeEntities(Entity entity) {
        if (entity != null){
            entityList.remove(entity);
        }
    }
}
