package io.github.lab2coursework.lwjgl3.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A building block that carries a single letter.
 * Drawn as a filled rectangle with the letter centred inside.
 */
public class LetterBlock extends Entity {

    private final char   letter;
    private final float  width;
    private final float  height;
    private       boolean landed;    // true once it has been placed on the stack
    private       boolean discarded; // true once thrown into the bin
    private       int     wordIndex = -1; // which word (0=CAT, 1=DOG, 2=FOX) this block belongs to

    public LetterBlock(char letter, float x, float y, float width, float height, Color color) {
        super(x, y, color, 0f);
        this.letter = letter;
        this.width  = width;
        this.height = height;
    }

    @Override
    public void update() { /* movement handled by MovementManager via strategy */ }

    // ── Drawing ───────────────────────────────────────────────────────────────

    @Override
    public void draw(ShapeRenderer shape) {
        if (shape == null) return;
        // Block body
        shape.setColor(getColor());
        shape.rect(getX(), getY(), width, height);
        // Dark border
        shape.setColor(Color.DARK_GRAY);
        shape.rect(getX(),          getY(),           width, 3f);   // bottom
        shape.rect(getX(),          getY() + height - 3f, width, 3f); // top
        shape.rect(getX(),          getY(),           3f, height);   // left
        shape.rect(getX() + width - 3f, getY(),       3f, height);   // right
    }

    /**
     * Draws the letter label. Call between batch.begin() / batch.end().
     * Requires the font to be passed in since Entity doesn't own one.
     */
    public void drawLabel(SpriteBatch batch, BitmapFont font) {
        if (batch == null || font == null) return;
        font.setColor(Color.BLACK);
        // Approximate centre; BitmapFont draws from baseline so offset upward
        font.draw(batch, String.valueOf(letter),
            getX() + width  / 2f - 6f,
            getY() + height / 2f + 8f);
    }

    // ── Getters / setters ─────────────────────────────────────────────────────

    public char    getLetter()   { return letter; }
    public float   getWidth()    { return width; }
    public float   getHeight()   { return height; }
    public boolean isLanded()    { return landed; }
    public boolean isDiscarded() { return discarded; }
    public int     getWordIndex() { return wordIndex; }

    public void setLanded(boolean v)    { this.landed    = v; }
    public void setDiscarded(boolean v) { this.discarded = v; }
    public void setWordIndex(int idx)   { this.wordIndex = idx; }

    /** AABB bounds helpers used by collision rules */
    public float getRight()  { return getX() + width; }
    public float getTop()    { return getY() + height; }
}
