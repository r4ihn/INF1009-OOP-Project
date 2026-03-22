package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

/**
 * Strategy: pendulum physics for the block hanging from the crane.
 *
 * Uses an inertia-driven pendulum with moving pivot:
 * θ'' = -(g/L)sin(θ) - (x¨pivot/L)cos(θ) - cθ'
 * Block position is then computed from crane hook + rope vector.
 */
public class RopeSwingMovement extends Movement {

    private static final float GRAVITY_PX      = 1200f; // pixels/s^2
    // Higher values make rope react more strongly to crane movement (arcade feel).
    private static final float SWING_SENSITIVITY = 3.0f;
    // Slightly lower drag keeps movement lively while still settling for stacking.
    private static final float ANGULAR_DRAG    = 1.45f;  // viscous damping coefficient
    // Higher filter gain means quicker response to input direction changes.
    private static final float ANCHOR_RESPONSE_GAIN = 20f;
    private static final float MAX_ANCHOR_ACCEL = 3200f; // clamp to avoid jitter spikes

    private final float ropeLength;  // pixels
    private float angle;             // radians from vertical, + = right
    private float angularVelocity;   // radians/second

    // Anchor point: updated each frame from the crane arm
    private float anchorX;
    private float anchorY;
    private float prevAnchorX;
    private float prevAnchorVelocityX;
    private float anchorAccelX;
    private boolean anchorInitialized;

    public RopeSwingMovement(float ropeLength, float initialAngle, float anchorX, float anchorY) {
        this.ropeLength      = ropeLength;
        this.angle           = initialAngle;
        this.angularVelocity = 0f;
        this.anchorX         = anchorX;
        this.anchorY         = anchorY;
        this.prevAnchorX     = anchorX;
        this.prevAnchorVelocityX = 0f;
        this.anchorAccelX = 0f;
        this.anchorInitialized = false;
    }

    /** Call every frame BEFORE update() to keep the anchor tracking the crane. */
    public void setAnchor(float x, float y) {
        this.anchorX = x;
        this.anchorY = y;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        if (deltaTime <= 0f) return;

        if (!anchorInitialized) {
            prevAnchorX = anchorX;
            prevAnchorVelocityX = 0f;
            anchorAccelX = 0f;
            anchorInitialized = true;
        }

        // Derive pivot kinematics from anchor motion each frame.
        float anchorVelocityX = (anchorX - prevAnchorX) / deltaTime;
        float rawAnchorAccelX = (anchorVelocityX - prevAnchorVelocityX) / deltaTime;
        rawAnchorAccelX = Math.max(-MAX_ANCHOR_ACCEL, Math.min(MAX_ANCHOR_ACCEL, rawAnchorAccelX));

        // Low-pass filter keeps inertia stable and avoids one-frame jitter kicks.
        float filterStrength = Math.min(1f, ANCHOR_RESPONSE_GAIN * deltaTime);
        anchorAccelX += (rawAnchorAccelX - anchorAccelX) * filterStrength;

        prevAnchorX = anchorX;
        prevAnchorVelocityX = anchorVelocityX;

        // Driven pendulum: gravity + anchor acceleration forcing + drag.
        float gravityTerm = -(GRAVITY_PX / ropeLength) * (float) Math.sin(angle);
        float anchorDriveTerm = -((anchorAccelX * SWING_SENSITIVITY) / ropeLength) * (float) Math.cos(angle);
        float dragTerm = -ANGULAR_DRAG * angularVelocity;
        float angularAccel = gravityTerm + anchorDriveTerm + dragTerm;

        angularVelocity += angularAccel * deltaTime;
        angle += angularVelocity * deltaTime;

        // Convert polar to Cartesian from hook (rope end is at block's top-centre).
        float ropeEndX = anchorX + (float) Math.sin(angle) * ropeLength;
        float ropeEndY = anchorY - (float) Math.cos(angle) * ropeLength;

        if (entity instanceof LetterBlock) {
            LetterBlock block = (LetterBlock) entity;
            entity.setX(ropeEndX - block.getWidth() / 2f);
            entity.setY(ropeEndY - block.getHeight());
        } else {
            entity.setX(ropeEndX);
            entity.setY(ropeEndY);
        }
    }

    public float getAngle()         { return angle; }
    public float getRopeLength()    { return ropeLength; }
    public float getAnchorX()       { return anchorX; }
    public float getAnchorY()       { return anchorY; }
}
