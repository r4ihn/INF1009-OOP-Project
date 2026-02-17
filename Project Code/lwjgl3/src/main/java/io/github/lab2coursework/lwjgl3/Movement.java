package io.github.lab2coursework.lwjgl3;

public abstract class Movement {

    public abstract void update(Entity entity);

    // Protected movement methods
    protected void RightMovement(Entity entity) {
        entity.setX(entity.getX() + entity.getSpeed());
    }

    protected void LeftMovement(Entity entity) {
        entity.setX(entity.getX() - entity.getSpeed());
    }

    protected void UpMovement(Entity entity) {
        entity.setY(entity.getY() + entity.getSpeed());
    }

    protected void DownMovement(Entity entity) {
        entity.setY(entity.getY() - entity.getSpeed());
    }

    protected void stop(Entity entity) {
        // Optional stop logic
    }
}
