package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

public class BlockLandingRule implements CollisionRule {

    private static final float GROUND_Y = 60f;
    private static final float BLOCK_H = 60f;

    // How much horizontal difference is allowed before the stack is considered unstable.
    private static final float MAX_STACK_OFFSET = 22f;

    private static final float SWAY_TRIGGER_ERROR = 8f;
    private static final float SWAY_DURATION = 0.30f;
    private static final float SWAY_FREQUENCY = 28f;
    private static final float MAX_SWAY_AMPLITUDE = 18f;

    private final WordGameState state;
    private final float stackBaseY;

    private final int[] stackCountPerWord = {0, 0, 0};
    private final float[] stackXPerWord = {-1f, -1f, -1f};

    private boolean towerStabilized = false;
    private boolean wordResetPending = false;
    private int resetWordIndex = -1;

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

        if (b == null) {
            return falling.getY() <= GROUND_Y;
        }

        if (!(b instanceof LetterBlock)) {
            return false;
        }

        LetterBlock stacked = (LetterBlock) b;
        if (!stacked.isLanded() || matchedWordIdx < 0) {
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

        float landY;
        float horizontalError;

        if (b instanceof LetterBlock) {
            LetterBlock stacked = (LetterBlock) b;
            landY = stacked.getTop();
            horizontalError = getCenterX(block) - getCenterX(stacked);
        } else {
            // First block for a word may start anywhere. Existing towers must not restart on the ground.
            if (stackCountPerWord[matchedWordIdx] > 0) {
                startSettlingForReset(matchedWordIdx, estimateGroundError(block, matchedWordIdx));
                block.setMovementStrategy(null);
                block.setDiscarded(true);
                return;
            }

            landY = stackBaseY;
            horizontalError = 0f;
            stackXPerWord[matchedWordIdx] = block.getX();
        }

        block.setY(landY);
        block.setMovementStrategy(null);
        block.setLanded(true);
        block.setWordIndex(matchedWordIdx);
        stackCountPerWord[matchedWordIdx]++;
        stackXPerWord[matchedWordIdx] = block.getX();

        float absError = Math.abs(horizontalError);
        if (absError > MAX_STACK_OFFSET) {
            startSettlingForReset(matchedWordIdx, absError);
        } else if (absError >= SWAY_TRIGGER_ERROR) {
            startSettlingForStabilize(matchedWordIdx, absError);
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
            state.getGameScore().applyTowerFallPenalty();
            resetWordStack(settlingWordIndex);
            state.resetWordProgress(settlingWordIndex);
            wordResetPending = true;
            resetWordIndex = settlingWordIndex;
        } else {
            towerStabilized = true;
        }

        collapseAfterSway = false;
        swayAmplitude = 0f;
        settlingWordIndex = -1;
        settlingTimer = 0f;
    }

    private void startSettlingForReset(int wordIdx, float absError) {
        settling = true;
        settlingWordIndex = wordIdx;
        settlingTimer = SWAY_DURATION;
        collapseAfterSway = true;
        swayAmplitude = Math.min(Math.max(absError * 0.35f, SWAY_TRIGGER_ERROR), MAX_SWAY_AMPLITUDE);
    }

    private void startSettlingForStabilize(int wordIdx, float absError) {
        settling = true;
        settlingWordIndex = wordIdx;
        settlingTimer = SWAY_DURATION;
        collapseAfterSway = false;
        swayAmplitude = Math.min(absError * 0.35f, MAX_SWAY_AMPLITUDE);
    }

    private void resetWordStack(int wordIdx) {
        if (wordIdx < 0 || wordIdx >= stackCountPerWord.length) {
            return;
        }
        stackCountPerWord[wordIdx] = 0;
        stackXPerWord[wordIdx] = -1f;
    }

    private float estimateGroundError(LetterBlock block, int wordIdx) {
        if (!hasStackX(wordIdx)) {
            return MAX_STACK_OFFSET + 1f;
        }
        return Math.abs(getCenterX(block) - (stackXPerWord[wordIdx] + block.getWidth() / 2f));
    }

    private float getCenterX(LetterBlock block) {
        return block.getX() + block.getWidth() / 2f;
    }

    private boolean isTopBlock(LetterBlock block, int wordIdx) {
        if (stackCountPerWord[wordIdx] <= 0) {
            return false;
        }

        float expectedTopY = stackBaseY + (stackCountPerWord[wordIdx] - 1) * BLOCK_H;
        return Math.abs(block.getY() - expectedTopY) < 0.5f;
    }

    private boolean isTouchingTop(LetterBlock falling, LetterBlock stacked) {
        boolean overlapsX = falling.getRight() > stacked.getX() && falling.getX() < stacked.getRight();
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

    public boolean consumeTowerStabilized() {
        boolean result = towerStabilized;
        towerStabilized = false;
        return result;
    }

    public boolean consumeWordReset() {
        boolean result = wordResetPending;
        wordResetPending = false;
        return result;
    }

    public int getResetWordIndex() {
        return resetWordIndex;
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
        for (int i = 0; i < stackCountPerWord.length; i++) {
            stackCountPerWord[i] = 0;
            stackXPerWord[i] = -1f;
        }

        settling = false;
        settlingWordIndex = -1;
        settlingTimer = 0f;
        collapseAfterSway = false;
        swayAmplitude = 0f;
        wordResetPending = false;
        resetWordIndex = -1;
        towerStabilized = false;
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
