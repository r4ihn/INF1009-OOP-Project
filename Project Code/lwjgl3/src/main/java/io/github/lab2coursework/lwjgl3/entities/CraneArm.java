package io.github.lab2coursework.lwjgl3.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The horizontal crane arm that slides left and right at the top of the screen.
 * The hanging block is attached at getHookX() / getHookY().
 */
public class CraneArm extends Entity {

    private static final float ARM_W  = 120f;
    private static final float ARM_H  =  20f;
    private static final float MAST_W =  16f;
    private static final float MAST_H = 100f;

    // Hook is at the right end of the arm
    public float getHookX() { return getX() + ARM_W; }
    public float getHookY() { return getY(); }

    public CraneArm(float x, float y) {
        super(x, y, Color.ORANGE, 90f); // speed = px/s left-right travel
    }

    @Override
    public void update() { /* movement handled by CraneMovement strategy */ }

    @Override
    public void draw(ShapeRenderer shape) {
        if (shape == null) return;

        float x = getX();
        float y = getY();

        // Vertical mast
        shape.setColor(new Color(0.5f, 0.35f, 0.1f, 1f));
        shape.rect(x - MAST_W / 2f, y, MAST_W, MAST_H);

        // Horizontal arm
        shape.setColor(Color.ORANGE);
        shape.rect(x, y + MAST_H - ARM_H, ARM_W, ARM_H);

        // Hook tip
        shape.setColor(Color.DARK_GRAY);
        shape.rect(getHookX() - 6f, y + MAST_H - ARM_H - 14f, 12f, 14f);
    }

    public float getArmWidth()  { return ARM_W; }
    public float getMastHeight(){ return MAST_H; }
}
