package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

public class BlockLandingRule implements CollisionRule {

    private static final float GROUND_Y = 60f;
    private static final float BLOCK_H = 60f;

    // How far the next block can be from its tower anchor before the whole tower falls.
    private static final float MAX_HORIZONTAL_ERROR = 70f;

    // Small offsets will shake, then stabilise.
    private static final float SWAY_TRIGGER_ERROR = 8f;
    private static final float SWAY_DURATION = 0.30f;
    private static final float SWAY_FREQUENCY = 28f;
    private static final float MAX_SWAY_AMPLITUDE = 18f;

    private final WordGameState state;
    private final float stackBaseY;

    private final int[] stackCountPerWord = {0, 0, 0};
    private final float[] stackXPerWord = {-1f, -1f, -1f};

    private boolean towerCollapsed = false;
    private boolean towerStabilized = false;

    private boolean settling = false;
    private int settlingWordIndex = -1;
    private float settlingTimer = 0f;
    private boolean collapseAfterSway = false;
    private float swayAmplitude = 0f;

    public BlockLandingRule(WordGameState state) {
        this.state = state;
        this.stackBaseY = GROUND_Y;
    }

    @Override
    public boolean matches(Entity a, Entity b) {
        if (!(a instanceof LetterBlock)) {
            return false;
        }

        LetterBlock falling = (LetterBlock) a;
        if (falling.isLanded() || falling.isDiscarded()) {
            return false;
        }

        int matchedWordIdx = state.peekMatchingWordIndex(falling.getLetter());

        // Ground collision always exists as a fallback.
        if (b == null) {
            return falling.getY() <= GROUND_Y;
        }

        if (!(b instanceof LetterBlock)) {
            return false;
        }

        LetterBlock stacked = (LetterBlock) b;
        if (!stacked.isLanded()) {
            return false;
        }

        // Wrong letters should not stack on existing towers.
        if (matchedWordIdx < 0) {
            return false;
        }

        if (stacked.getWordIndex() != matchedWordIdx) {
            return false;
        }

        if (!isTopBlock(stacked, matchedWordIdx)) {
            return false;
        }

        return isTouchingTop(falling, stacked);
    }

    @Override
    public void resolve(Entity a, Entity b) {
        LetterBlock block = (LetterBlock) a;

        int matchedWordIdx = state.placeNextLetter(block.getLetter());
        if (matchedWordIdx < 0) {
            block.setDiscarded(true);
            block.setMovementStrategy(null);
            return;
        }

        if (stackXPerWord[matchedWordIdx] < 0f) {
            if (b instanceof LetterBlock) {
                stackXPerWord[matchedWordIdx] = ((LetterBlock) b).getX();
            } else {
                // First correct block for this word becomes the tower anchor.
                stackXPerWord[matchedWordIdx] = block.getX();
            }
        }

        float targetX = stackXPerWord[matchedWordIdx];
        float horizontalError = block.getX() - targetX;
        float landY = stackBaseY + stackCountPerWord[matchedWordIdx] * BLOCK_H;

        // Snap into the tower column, but the whole tower can visibly sway in render.
        block.setX(targetX);
        block.setY(landY);
        block.setMovementStrategy(null);
        block.setLanded(true);
        block.setWordIndex(matchedWordIdx);
        stackCountPerWord[matchedWordIdx]++;

        if (Math.abs(horizontalError) >= SWAY_TRIGGER_ERROR) {
            settling = true;
            settlingWordIndex = matchedWordIdx;
            settlingTimer = SWAY_DURATION;
            collapseAfterSway = Math.abs(horizontalError) > MAX_HORIZONTAL_ERROR;
            swayAmplitude = Math.min(Math.abs(horizontalError) * 0.35f, MAX_SWAY_AMPLITUDE);
        } else {
            towerStabilized = true;
        }
    }

    public void update(float delta) {
        if (!settling) return;

        settlingTimer -= delta;
        if (settlingTimer > 0f) return;

        settling = false;

        if (collapseAfterSway) {
            triggerTowerCollapse();
        } else {
            towerStabilized = true;
        }

        collapseAfterSway = false;
        swayAmplitude = 0f;
        settlingWordIndex = -1;
        settlingTimer = 0f;
    }

    private void triggerTowerCollapse() {
        towerCollapsed = true;

        // The letter was already accepted into WordGameState, so revert the whole attempt.
        state.loseLife();
        resetStack();
    }

    private boolean isTopBlock(LetterBlock block, int wordIdx) {
        if (stackCountPerWord[wordIdx] <= 0) {
            return false;
        }

        float expectedTopY = stackBaseY + (stackCountPerWord[wordIdx] - 1) * BLOCK_H;
        return Math.abs(block.getY() - expectedTopY) < 0.5f;
    }

    private boolean isTouchingTop(LetterBlock falling, LetterBlock stacked) {
        float fallingLeft = falling.getX();
        float fallingRight = falling.getRight();
        float stackedLeft = stacked.getX();
        float stackedRight = stacked.getRight();

        boolean overlapsX = fallingRight > stackedLeft && fallingLeft < stackedRight;
        boolean reachedTop = falling.getY() <= stacked.getTop();

        return overlapsX && reachedTop;
    }

    public boolean isSettling() {
        return settling;
    }

    public float getTowerSwayOffset(int wordIdx) {
        if (!settling || wordIdx != settlingWordIndex) {
            return 0f;
        }

        float progress = 1f - (settlingTimer / SWAY_DURATION);
        float damping = 1f - progress;
        return (float) Math.sin(progress * SWAY_FREQUENCY) * swayAmplitude * damping;
    }

    public boolean consumeTowerCollapsed() {
        boolean result = towerCollapsed;
        towerCollapsed = false;
        return result;
    }

    public boolean consumeTowerStabilized() {
        boolean result = towerStabilized;
        towerStabilized = false;
        return result;
    }

    public int getStackCount(int wordIdx) {
        if (wordIdx >= 0 && wordIdx < 3) {
            return stackCountPerWord[wordIdx];
        }
        return 0;
    }

    public int getTotalStackCount() {
        int total = 0;
        for (int count : stackCountPerWord) {
            total += count;
        }
        return total;
    }

    public void resetStack() {
        stackCountPerWord[0] = 0;
        stackCountPerWord[1] = 0;
        stackCountPerWord[2] = 0;

        stackXPerWord[0] = -1f;
        stackXPerWord[1] = -1f;
        stackXPerWord[2] = -1f;

        settling = false;
        settlingWordIndex = -1;
        settlingTimer = 0f;
        collapseAfterSway = false;
        swayAmplitude = 0f;
    }

    public boolean hasStackX(int wordIdx) {
        return wordIdx >= 0 && wordIdx < 3 && stackXPerWord[wordIdx] >= 0f;
    }

    public float getStackX(int wordIdx) {
        if (wordIdx >= 0 && wordIdx < 3) {
            return stackXPerWord[wordIdx];
        }
        return 0f;
    }
}
