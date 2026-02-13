package io.github.lab2coursework.lwjgl3.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.movement.iMoveable;

public abstract class Entity implements iMoveable {
    // Private attributes for Encapsulation
    private float x, y, speed;
    private Color color;

    public Entity() {}

    public Entity(float x, float y, Color color, float speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
    }

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

    @Override
    public void movement(){
        this.y -= this.speed;
        if(this.y < 0){
            this.y = 400;
        }
    }

    public abstract void update();
}
