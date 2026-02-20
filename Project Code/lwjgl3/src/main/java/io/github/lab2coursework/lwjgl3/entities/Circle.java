package io.github.lab2coursework.lwjgl3.entities;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Circle extends Entity {
    private float radius;

    public Circle(){
        super();
    }

    public Circle(float x, float y, float radius, Color color, float speed) {
        super(x, y, color, speed);
        this.radius = radius;
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius){ this.radius = radius; }

    @Override
    public void draw(ShapeRenderer shape) {
        if(shape != null){
            shape.setColor(this.getColor());
            shape.circle(getX(), getY(), this.radius);
        }
    }

    @Override
    public void update(){
    }
}

