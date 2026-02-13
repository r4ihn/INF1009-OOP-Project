package io.github.lab2coursework.lwjgl3;

public abstract class MovementManager{
    public abstract void update(Entity entity);

    protected void moveRight(Entity entity) {
        entity.setX(entity.getX() + entity.getSpeed());
    }

    protected void moveLeft(Entity entity) {
        entity.setX(entity.getX() - entity.getSpeed());
    }

    protected void moveUp(Entity entity) {
        entity.setY(entity.getY() + entity.getSpeed());
    }

    protected void moveDown(Entity entity) {
        entity.setY(entity.getY() - entity.getSpeed());
    }

    protected void stop(Entity entity) {

    }
}

