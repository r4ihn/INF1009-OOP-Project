package io.github.lab2coursework.lwjgl3.managers;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.entities.Entity;

public class EntityManager {
    private final List<Entity> entityList = new ArrayList<>();

    public void addEntities(Entity entity) {
        entityList.add(entity);
    }

    public List<Entity> getEntities() {
        return entityList;
    }

    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        for (Entity entity : entityList) {
            if (batch != null) entity.draw(batch);
            if (shape != null) entity.draw(shape);
        }
    }

    public void update(){
    }
}
