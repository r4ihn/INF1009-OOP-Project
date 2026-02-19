package io.github.lab2coursework.lwjgl3;

public abstract class Movement {

    public abstract void update(Entity entity, float deltaTime);

    // Protected movement methods
    protected void RightMovement(Entity entity, float deltaTime) {
        entity.setX(entity.getX() + (entity.getSpeed() * deltaTime));
    }

    protected void LeftMovement(Entity entity, float deltaTime) {

        entity.setX(entity.getX() - (entity.getSpeed() * deltaTime));
    }

    protected void UpMovement(Entity entity, float deltaTime) {

        entity.setY(entity.getY() + (entity.getSpeed() * deltaTime));
    }

    protected void DownMovement(Entity entity, float deltaTime) {

        entity.setY(entity.getY() - (entity.getSpeed() * deltaTime));
    }

    protected void stop(Entity entity) {
        // Optional stop logic
    }
}
