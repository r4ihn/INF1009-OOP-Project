package io.github.lab2coursework.lwjgl3;

import java.util.ArrayList;

public class MovementManager {

    private ArrayList<Entity> entities;

    public void rightMovement(Entity entity) {
        entity.setX(entity.getX() - entity.getSpeed());
    }

    public void leftMovement(Entity entity) {
        entity.setX(entity.getX() + entity.getSpeed());
    }

    public void upMovement(Entity entity) {
        entity.setY(entity.getY() + entity.getSpeed());
    }

    public void downMovement(Entity entity) {
        entity.setY(entity.getY() - entity.getSpeed());
    }

    public void stopMovement(Entity entity) {
        // Movement management logic to be implemented
    }

    public void setSpeed(Entity entity, float speed) {
        entity.setSpeed(speed);
    }
}
