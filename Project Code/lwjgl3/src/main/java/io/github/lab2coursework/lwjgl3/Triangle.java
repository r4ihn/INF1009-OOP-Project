package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Triangle extends Entity {

    public Triangle() { }

    public Triangle(float x, float y, Color color, float speed) {
        super(x, y, color, speed);
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(this.getColor());
        // Draw triangle based on position
        shape.triangle(getX(), getY(), getX() + 100, getY(), getX() + 50, getY() + 100);
    }

    // No-op SpriteBatch draw to satisfy abstract contract
    @Override
    public void draw(SpriteBatch batch) { }

    @Override
    public void movement() {
        // 1. Move Right (A Key)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.setX(this.getX() - this.getSpeed());
        }

        // 2. Move Left (D Key)
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.setX(this.getX() + this.getSpeed());
        }
    }

    @Override
    public void update() {
        movement();
    }
}