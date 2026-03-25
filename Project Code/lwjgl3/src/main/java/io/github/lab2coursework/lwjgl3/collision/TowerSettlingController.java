package io.github.lab2coursework.lwjgl3.collision;

public class TowerSettlingController {

    private static final float SWAY_DURATION = 0.30f;
    private static final float SWAY_FREQUENCY = 28f;
    private static final float MAX_SWAY_AMPLITUDE = 18f;

    private boolean settling = false;
    private int settlingWordIndex = -1;
    private float settlingTimer = 0f;
    private boolean collapseAfterSway = false;
    private float swayAmplitude = 0f;

    public void startSettlingForReset(int wordIdx, float error) {
        settling = true;
        settlingWordIndex = wordIdx;
        settlingTimer = 0f;
        collapseAfterSway = true;
        swayAmplitude = Math.min(error, MAX_SWAY_AMPLITUDE);
    }

    public void startSettlingForStabilize(int wordIdx) {
        settling = true;
        settlingWordIndex = wordIdx;
        settlingTimer = 0f;
        collapseAfterSway = false;
        swayAmplitude = MAX_SWAY_AMPLITUDE * 0.5f;
    }

    public void update(float delta) {
        if (!settling) return;

        settlingTimer += delta;

        if (settlingTimer >= SWAY_DURATION) {
            settling = false;
        }
    }

    public float getSwayOffset() {
        if (!settling) return 0f;

        return (float) Math.sin(settlingTimer * SWAY_FREQUENCY) * swayAmplitude;
    }

    public boolean isSettling() {
        return settling;
    }

    public boolean shouldCollapse() {
        return !settling && collapseAfterSway;
    }

    public int getSettlingWordIndex() {
        return settlingWordIndex;
    }

    public void reset() {
        settling = false;
        settlingWordIndex = -1;
        settlingTimer = 0f;
        collapseAfterSway = false;
        swayAmplitude = 0f;
    }
}
