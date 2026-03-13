package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: pendulum physics for the block hanging from the crane.
 *
 * Uses simplified angular pendulum: θ'' = -(g/L) * sin(θ)
 * Block position is then computed from crane hook + rope vector.
 */
public class RopeSwingMovement extends Movement {

    private static final float GRAVITY      = 9.8f;
    private static final float DAMPING      = 0.995f; // slight energy loss per frame

    private final float ropeLength;  // pixels
    private float angle;             // radians from vertical, + = right
    private float angularVelocity;   // radians/second

    // Anchor point: updated each frame from the crane arm
    private float anchorX;
    private float anchorY;

    public RopeSwingMovement(float ropeLength, float initialAngle, float anchorX, float anchorY) {
        this.ropeLength      = ropeLength;
        this.angle           = initialAngle;
        this.angularVelocity = 0f;
        this.anchorX         = anchorX;
        this.anchorY         = anchorY;
    }

    /** Call every frame BEFORE update() to keep the anchor tracking the crane. */
    public void setAnchor(float x, float y) {
        this.anchorX = x;
        this.anchorY = y;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        // Angular acceleration: -(g/L)*sin(θ), scaled to screen pixels
        float angularAccel = -(GRAVITY / ropeLength) * (float) Math.sin(angle) * 60f;
        angularVelocity = (angularVelocity + angularAccel * deltaTime) * DAMPING;
        angle += angularVelocity * deltaTime;

        // Convert polar to Cartesian
        entity.setX(anchorX + (float) Math.sin(angle) * ropeLength);
        entity.setY(anchorY - (float) Math.cos(angle) * ropeLength);
    }

    public float getAngle()         { return angle; }
    public float getRopeLength()    { return ropeLength; }
    public float getAnchorX()       { return anchorX; }
    public float getAnchorY()       { return anchorY; }
}
