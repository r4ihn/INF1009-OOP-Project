package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

/** Handles landing, stacking, sway, and reset for letter blocks. */
public class BlockLandingRule implements CollisionRule {

    private static final float GROUND_Y = 60f;
    private static final float BLOCK_H = 60f;

    // Max horizontal error before the tower collapses
    private static final float MAX_STACK_OFFSET = 22f;

    // Error range that causes sway before stabilizing
    private static final float SWAY_TRIGGER_ERROR = 8f;

    private final WordGameState state;
    private final float stackBaseY;

    // Track tower height and x-position for each word
    private final BlockStackTracker stackTracker;

    private final BlockLandingValidator landingValidator;
    private final BlockPlacementService placementService;

    // Settling state for sway / collapse animation
    private final TowerSettlingController settlingController;

    private boolean towerStabilized = false;
    private boolean wordResetPending = false;
    private int resetWordIndex = -1;

    public BlockLandingRule(WordGameState state) {
        this.state = state;
        this.stackBaseY = GROUND_Y;
        this.stackTracker = new BlockStackTracker(stackBaseY, BLOCK_H);
        this.landingValidator = new BlockLandingValidator();
        this.settlingController = new TowerSettlingController();
        this.placementService = new BlockPlacementService();
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
        return landingValidator.isOverlapping(falling, stacked);
    }

    private void handleStackCollision(LetterBlock block, LetterBlock stacked, int matchedWordIdx) {
        // A valid stack hit must match the same word tower and land on its top block.
        boolean validTopLanding =
            matchedWordIdx >= 0
                && stacked.getWordIndex() == matchedWordIdx
                && stackTracker.isTopBlock(stacked, matchedWordIdx)
                && landingValidator.isLandingOnTop(block, stacked);

        if (validTopLanding) {
            handleValidTopLanding(block, stacked, matchedWordIdx);
            return;
        }

        handleInvalidStackCollision(block, stacked, matchedWordIdx);
    }

    private void handleValidTopLanding(LetterBlock block, LetterBlock stacked, int matchedWordIdx) {
        // Score/state advance is decided first so visual placement reflects the accepted word.
        int placedWordIdx = state.placeNextLetter(block.getLetter(), matchedWordIdx);
        if (placedWordIdx < 0) {
            placementService.markDiscarded(block);
            return;
        }

        float horizontalError =
            landingValidator.getCenterX(block) - landingValidator.getCenterX(stacked);

        placementService.placeOnTopOfBlock(block, stacked, placedWordIdx);
        stackTracker.recordLanding(placedWordIdx, block);

        float absError = Math.abs(horizontalError);
        // Large error collapses tower, medium error triggers a temporary sway, small error is stable.
        if (absError > MAX_STACK_OFFSET) {
            settlingController.startSettlingForReset(placedWordIdx, absError);
        } else if (absError >= SWAY_TRIGGER_ERROR) {
            settlingController.startSettlingForStabilize(placedWordIdx);
        } else {
            towerStabilized = true;
        }
    }

    private void handleInvalidStackCollision(LetterBlock block, LetterBlock stacked, int matchedWordIdx) {
        placementService.stopAtTopAndDiscard(block, stacked);

        if (matchedWordIdx >= 0 && stacked.getWordIndex() == matchedWordIdx) {
            settlingController.startSettlingForReset(matchedWordIdx, MAX_STACK_OFFSET + 1f);
        } else {
            int hitWordIdx = stacked.getWordIndex();
            state.loseLifeOnly();
            settlingController.startSettlingForReset(hitWordIdx, MAX_STACK_OFFSET + 1f);
        }
    }

    private void handleGroundCollision(LetterBlock block, int matchedWordIdx) {
        // Wrong letter on ground is an immediate life loss and reset.
        if (matchedWordIdx < 0) {
            state.loseLife();
            placementService.markDiscarded(block);
            return;
        }

        if (stackTracker.hasStack(matchedWordIdx)) {
            placementService.markDiscarded(block);
            settlingController.startSettlingForReset(
                matchedWordIdx,
                stackTracker.estimateGroundError(block, matchedWordIdx)
            );
            return;
        }

        int placedWordIdx = state.placeNextLetter(block.getLetter());
        if (placedWordIdx < 0) {
            placementService.markDiscarded(block);
            return;
        }

        placementService.placeOnGround(block, stackBaseY, placedWordIdx);
        stackTracker.recordLanding(placedWordIdx, block);
        towerStabilized = true;
    }

    @Override
    public void resolve(Entity a, Entity b) {
        LetterBlock block = (LetterBlock) a;

        if (b instanceof LetterBlock) {
            LetterBlock stacked = (LetterBlock) b;
            int matchedWordIdx = state.peekMatchingWordIndex(block.getLetter(), stacked.getWordIndex());
            handleStackCollision(block, stacked, matchedWordIdx);
            return;
        }

        int matchedWordIdx = state.peekMatchingWordIndex(block.getLetter());
        handleGroundCollision(block, matchedWordIdx);
    }

    public void update(float delta) {
        // Preserve previous state so we can emit one-shot events when settling ends.
        boolean wasSettling = settlingController.isSettling();

        settlingController.update(delta);

        if (wasSettling && !settlingController.isSettling()) {
            int wordIdx = settlingController.getSettlingWordIndex();

            if (settlingController.shouldCollapse()) {
                // Collapse flow resets only the affected word tower.
                stackTracker.resetWordStack(wordIdx);
                state.resetWordProgress(wordIdx);
                wordResetPending = true;
                resetWordIndex = wordIdx;
            } else {
                towerStabilized = true;
            }

            settlingController.reset();
        }
    }

    public boolean isSettling() {
        return settlingController.isSettling();
    }

    public float getTowerSwayOffset(int wordIdx) {
        if (!settlingController.isSettling()
            || wordIdx != settlingController.getSettlingWordIndex()) {
            return 0f;
        }

        return settlingController.getSwayOffset();
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
        stackTracker.resetAll();
        towerStabilized = false;
        wordResetPending = false;
        resetWordIndex = -1;
        settlingController.reset();
    }

    public boolean hasStackX(int wordIdx) {
        return stackTracker.hasStackX(wordIdx);
    }

    public float getStackX(int wordIdx) {
        return stackTracker.getStackX(wordIdx);
    }
}
