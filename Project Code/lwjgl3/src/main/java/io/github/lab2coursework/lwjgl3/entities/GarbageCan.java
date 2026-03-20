package io.github.lab2coursework.lwjgl3.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The trash bin the player throws unwanted blocks into.
 * Drawn as a simple trapezoid (lid + body) using ShapeRenderer.
 * Highlights in red when the player is holding G (discard key).
 */
public class GarbageCan extends Entity {

    private static final float W = 80f;
    private static final float H = 90f;

    private boolean highlighted;

    public GarbageCan(float x, float y) {
        super(x, y, Color.GRAY, 0f);
    }

    @Override
    public void update() { /* static entity */ }

    @Override
    public void draw(ShapeRenderer shape) {
        if (shape == null) return;

        Color bodyColor = highlighted ? Color.RED : Color.GRAY;
        Color lidColor  = highlighted ? new Color(0.8f, 0.1f, 0.1f, 1f) : Color.DARK_GRAY;

        float x = getX();
        float y = getY();

        // Body (slightly narrower at top for a bin look)
        shape.setColor(bodyColor);
        shape.rect(x, y, W, H - 12f);

        // Lid
        shape.setColor(lidColor);
        shape.rect(x - 4f, y + H - 12f, W + 8f, 12f);

        // Stripes on body
        shape.setColor(Color.DARK_GRAY);
        shape.rect(x + W * 0.25f, y + 4f, 6f, H - 20f);
        shape.rect(x + W * 0.55f, y + 4f, 6f, H - 20f);
    }

    /** Hit-box check: does the given block overlap the bin opening? */
    public boolean overlaps(LetterBlock block) {
        float bx = block.getX(), by = block.getY();
        float br = bx + block.getWidth(), bt = by + block.getHeight();
        float cx = getX(), cy = getY(), cr = cx + W, ct = cy + H;
        return bx < cr && br > cx && by < ct && bt > cy;
    }

    public void setHighlighted(boolean h) { this.highlighted = h; }
    public float getWidth()  { return W; }
    public float getHeight() { return H; }
}
