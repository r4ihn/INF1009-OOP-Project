package io.github.lab2coursework.lwjgl3.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.movement.Movement;

public abstract class Entity {
    // Private attributes for Encapsulation
    private float x, y, speed;
    private Color color;

    private Movement movementStrategy;

    public Entity() {}

    public Entity(float x, float y, Color color, float speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
    }

    // Allow the Manager to get and set the strategy
    public Movement getMovementStrategy() { return movementStrategy; }
    public void setMovementStrategy(Movement movementStrategy) { this.movementStrategy = movementStrategy; }

    // Standard getters and setters
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getSpeed() { return speed;}
    public void setSpeed(float speed) { this.speed = speed;}

    public void draw(ShapeRenderer shape){ };
    public void draw(SpriteBatch batch){ };

    public abstract void update();
}
