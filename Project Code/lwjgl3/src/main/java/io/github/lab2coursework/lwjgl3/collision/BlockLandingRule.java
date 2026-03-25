package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.GameScore;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

/** Handles landing, stacking, sway, and reset for letter blocks. */
public class BlockLandingRule implements CollisionRule {

    private static final float GROUND_Y = 60f;
    private static final float BLOCK_H = 60f;

    // Max horizontal error before the tower collapses
    private static final float MAX_STACK_OFFSET = 22f;

    // Error range that causes sway before stabilizing
    private static final float SWAY_TRIGGER_ERROR = 8f;
    private static final float SWAY_DURATION = 0.30f;
    private static final float SWAY_FREQUENCY = 28f;
    private static final float MAX_SWAY_AMPLITUDE = 18f;

    private final WordGameState state;
    private final float stackBaseY;

    // Track tower height and x-position for each word
    private final int[] stackCountPerWord = {0, 0, 0};
    private final float[] stackXPerWord = {-1f, -1f, -1f};

    private boolean towerStabilized = false;
    private boolean wordResetPending = false;
    private int resetWordIndex = -1;

    // Settling state for sway / collapse animation
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

        // Ground check
        if (b == null) {
            return falling.getY() <= GROUND_Y;
        }

        // Any landed letter block is solid now
        if (!(b instanceof LetterBlock)) {
            return false;
        }

        LetterBlock stacked = (LetterBlock) b;
        if (!stacked.isLanded()) {
            return false;
        }

        // If the falling block overlaps any landed block, that counts as collision.
        return isOverlapping(falling, stacked);
    }

    @Override
    public void resolve(Entity a, Entity b) {
        LetterBlock block = (LetterBlock) a;

        // Figure out which word this letter is currently valid for, if any
        int matchedWordIdx = state.peekMatchingWordIndex(block.getLetter());

        // ── Collision with another landed block ─────────────────────────────
        if (b instanceof LetterBlock) {
            LetterBlock stacked = (LetterBlock) b;

            boolean validTopLanding =
                matchedWordIdx >= 0
                    && stacked.getWordIndex() == matchedWordIdx
                    && isTopBlock(stacked, matchedWordIdx)
                    && isLandingOnTop(block, stacked);

            // Correct stack + top block + landing from above
            if (validTopLanding) {
                int placedWordIdx = state.placeNextLetter(block.getLetter());
                if (placedWordIdx < 0) {
                    block.setDiscarded(true);
                    block.setMovementStrategy(null);
                    return;
                }

                float landY = stacked.getTop();
                float horizontalError = getCenterX(block) - getCenterX(stacked);

                block.setY(landY);
                block.setMovementStrategy(null);
                block.setLanded(true);
                block.setWordIndex(placedWordIdx);

                stackCountPerWord[placedWordIdx]++;
                stackXPerWord[placedWordIdx] = block.getX();

                float absError = Math.abs(horizontalError);
                if (absError > MAX_STACK_OFFSET) {
                    startSettlingForReset(placedWordIdx, absError);
                } else if (absError >= SWAY_TRIGGER_ERROR) {
                    startSettlingForStabilize(placedWordIdx, absError);
                } else {
                    towerStabilized = true;
                }
                return;
            }

            // Wrong tower / side hit / body hit:
            // stop the block so it does not clip through, then treat it as a bad drop.
            block.setY(stacked.getTop());
            block.setMovementStrategy(null);
            block.setDiscarded(true);

            // Same word tower hit badly -> reset that same tower.
            if (matchedWordIdx >= 0 && stacked.getWordIndex() == matchedWordIdx) {
                startSettlingForReset(matchedWordIdx, MAX_STACK_OFFSET + 1f);
            } else {
                // Hit an unrelated tower:
                // lose 1 life, but only reset the tower that was actually hit.
                int hitWordIdx = stacked.getWordIndex();
                state.loseLifeOnly();
                startSettlingForReset(hitWordIdx, MAX_STACK_OFFSET + 1f);
            }
            return;
        }

        // ── Ground collision ────────────────────────────────────────────────
        if (matchedWordIdx < 0) {
            // Wrong letter dropped on ground
            state.loseLife();
            block.setDiscarded(true);
            block.setMovementStrategy(null);
            return;
        }

        // If this word already has a tower, the block should not be allowed to
        // "miss the stack and go to the ground".
        if (stackCountPerWord[matchedWordIdx] > 0) {
            block.setMovementStrategy(null);
            block.setDiscarded(true);
            startSettlingForReset(matchedWordIdx, estimateGroundError(block, matchedWordIdx));
            return;
        }

        // First block of the word lands on the ground
        int placedWordIdx = state.placeNextLetter(block.getLetter());
        if (placedWordIdx < 0) {
            block.setDiscarded(true);
            block.setMovementStrategy(null);
            return;
        }

        block.setY(stackBaseY);
        block.setMovementStrategy(null);
        block.setLanded(true);
        block.setWordIndex(placedWordIdx);

        stackCountPerWord[placedWordIdx]++;
        stackXPerWord[placedWordIdx] = block.getX();
        towerStabilized = true;
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

    // General AABB collision so landed blocks behave as solid objects
    private boolean isOverlapping(LetterBlock a, LetterBlock b) {
        return a.getX() < b.getRight()
            && a.getRight() > b.getX()
            && a.getY() < b.getTop()
            && a.getTop() > b.getY();
    }

    // Valid landing means the falling block is coming from above onto the top surface
    private boolean isLandingOnTop(LetterBlock falling, LetterBlock stacked) {
        boolean overlapsX = falling.getRight() > stacked.getX() && falling.getX() < stacked.getRight();
        boolean bottomReachedTop = falling.getY() <= stacked.getTop();
        boolean blockIsMostlyAbove = falling.getTop() >= stacked.getTop();
        return overlapsX && bottomReachedTop && blockIsMostlyAbove;
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

    public boolean consumeWordResetPending() {
        boolean result = wordResetPending;
        wordResetPending = false;
        return result;
    }

    public int consumeResetWordIndex() {
        int result = resetWordIndex;
        resetWordIndex = -1;
        return result;
    }

    public void resetStack() {
        for (int i = 0; i < GameScore.TARGET_WORD_COUNT; i++) {
            stackCountPerWord[i] = 0;
            stackXPerWord[i] = -1f;
        }
        towerStabilized = false;
        wordResetPending = false;
        resetWordIndex = -1;
        settling = false;
        settlingWordIndex = -1;
        settlingTimer = 0f;
        collapseAfterSway = false;
        swayAmplitude = 0f;
    }


    public boolean hasStackX(int wordIdx) {
        return wordIdx >= 0
            && wordIdx < stackXPerWord.length
            && stackXPerWord[wordIdx] >= 0f;
    }

    public float getStackX(int wordIdx) {
        if (!hasStackX(wordIdx)) {
            return -1f;
        }
        return stackXPerWord[wordIdx];
    }
}
