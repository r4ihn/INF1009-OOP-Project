package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

/**
 * Strategy: pendulum physics for the block hanging from the crane.
 *
 * Uses simplified angular pendulum: θ'' = -(g/L) * sin(θ)
 * Block position is computed from crane hook + rope vector, and swing also
 * reacts to horizontal crane-anchor movement so the bob lags naturally.
 */
public class RopeSwingMovement extends Movement {

    private static final float GRAVITY      = 9.8f;
    private static final float DAMPING      = 0.995f; // slight energy loss per frame
    private static final float ANCHOR_COUPLING = 2.2f;

    private final float ropeLength;  // pixels
    private float angle;             // radians from vertical, + = right
    private float angularVelocity;   // radians/second

    // Anchor point: updated each frame from the crane arm
    private float anchorX;
    private float anchorY;
    // Previous frame anchor position used to estimate anchor horizontal velocity.
    private float previousAnchorX;
    private boolean hasPreviousAnchor;

    /**
     * Creates a pendulum movement controller for one hanging block.
     *
     * @param ropeLength rope length in pixels
     * @param initialAngle initial swing angle in radians from vertical
     * @param anchorX initial hook x-coordinate
     * @param anchorY initial hook y-coordinate
     */
    public RopeSwingMovement(float ropeLength, float initialAngle, float anchorX, float anchorY) {
        this.ropeLength      = ropeLength;
        this.angle           = initialAngle;
        this.angularVelocity = 0f;
        this.anchorX         = anchorX;
        this.anchorY         = anchorY;
        this.previousAnchorX = anchorX;
        this.hasPreviousAnchor = false;
    }

    /** Call every frame BEFORE update() to keep the anchor tracking the crane. */
    public void setAnchor(float x, float y) {
        this.anchorX = x;
        this.anchorY = y;
    }

    /**
     * Integrates pendulum state for one frame and writes the resulting position
     * to the attached entity.
     */
    @Override
    public void update(Entity entity, float deltaTime) {
        if (hasPreviousAnchor && deltaTime > 0f) {
            float anchorVelocityX = (anchorX - previousAnchorX) / deltaTime;
            // Anchor moving left should push the bob to the right and vice versa.
            angularVelocity += -(anchorVelocityX / ropeLength) * ANCHOR_COUPLING * deltaTime;
        }
        previousAnchorX = anchorX;
        hasPreviousAnchor = true;

        // Angular acceleration: -(g/L)*sin(θ), scaled to screen pixels
        float angularAccel = -(GRAVITY / ropeLength) * (float) Math.sin(angle) * 60f;
        angularVelocity = (angularVelocity + angularAccel * deltaTime) * DAMPING;
        angle += angularVelocity * deltaTime;

        // Convert polar to Cartesian
        float bobX = anchorX + (float) Math.sin(angle) * ropeLength;
        float bobY = anchorY - (float) Math.cos(angle) * ropeLength;

        if (entity instanceof LetterBlock) {
            LetterBlock block = (LetterBlock) entity;
            // Rope end attaches to block top-centre, not bottom-left.
            entity.setX(bobX - block.getWidth() / 2f);
            entity.setY(bobY - block.getHeight());
        } else {
            entity.setX(bobX);
            entity.setY(bobY);
        }
    }

    /** Returns current pendulum angle (radians from vertical). */
    public float getAngle()         { return angle; }
    /** Returns configured rope length (pixels). */
    public float getRopeLength()    { return ropeLength; }
    /** Returns current anchor x-coordinate. */
    public float getAnchorX()       { return anchorX; }
    /** Returns current anchor y-coordinate. */
    public float getAnchorY()       { return anchorY; }
}
