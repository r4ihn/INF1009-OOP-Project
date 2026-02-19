package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Circle extends Entity {
    private float radius;

    public void Circlet(){ }

    public Circle(float x, float y, float radius, Color color, float speed) {
        super(x, y, color, speed);
        this.radius = radius;
    }

    public float getRadius() { return radius; }

    public void setRadius(float radius){ this.radius = radius; }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(this.getColor());
        shape.circle(getX(), getY(), this.radius);
    }

    @Override
    public void movement() {
        // Down Key (Move Down)
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)) {
            this.setY(this.getY() - this.getSpeed());
        }

        // Up key (Move UP)
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)) {
            this.setY(this.getY() + this.getSpeed());
        }
    }

    @Override
    public void update(){
        this.movement();
    }
}

