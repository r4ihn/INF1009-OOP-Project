package io.github.lab2coursework.lwjgl3.screens;

// Import statements for LibGDX
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

// Import statements for external classes
import io.github.lab2coursework.lwjgl3.collision.BlockLandingRule;
import io.github.lab2coursework.lwjgl3.collision.GarbageCollectionRule;
import io.github.lab2coursework.lwjgl3.entities.CraneArm;
import io.github.lab2coursework.lwjgl3.entities.GarbageCan;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.managers.CollisionManager;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.movement.CraneMovement;
import io.github.lab2coursework.lwjgl3.movement.FallMovement;
import io.github.lab2coursework.lwjgl3.movement.Movement;
import io.github.lab2coursework.lwjgl3.movement.RopeSwingMovement;
import io.github.lab2coursework.lwjgl3.wordgame.LetterBlockFactory;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;
import io.github.lab2coursework.lwjgl3.movement.AnchoredMovement;

// Import statements for java utilities
import java.util.ArrayList;
import java.util.List;

public class WordGameController {
    public static final int SW = 1280;
    public static final int SH = 720;
    public static final float GROUND_Y = 60f;
    public static final float CRANE_Y = SH - 160f;
    public static final float ROPE_LEN = 110f;
    public static final float CRANE_MIN_X = 100f;
    public static final float CRANE_MAX_X = SW - 220f;
    public static final float BIN_X = SW - 140f;
    public static final float BIN_Y = GROUND_Y;

    private final ScreenManager screenManager;
    private final Viewport viewport;

    private final WordGameState state;
    private final LetterBlockFactory blockFactory;
    private final BlockLandingRule landingRule;
    private final CollisionManager collisionManager;

    private final GarbageCan bin;
    private final List<LetterBlock> stackedBlocks = new ArrayList<>();

    private CraneArm crane;
    private LetterBlock hangingBlock;
    private LetterBlock fallingBlock;

    private boolean blockReleased;
    private boolean awaitingNext;
    private float awaitTimer;

    public WordGameController(ScreenManager screenManager, Viewport viewport, WordBank wordBank) {
        this.screenManager = screenManager;
        this.viewport = viewport;

        this.state = new WordGameState(wordBank);
        this.blockFactory = new LetterBlockFactory(state);

        this.bin = new GarbageCan(BIN_X, BIN_Y);
        this.landingRule = new BlockLandingRule(state);
        GarbageCollectionRule garbageRule = new GarbageCollectionRule(bin, state);

        this.collisionManager = new CollisionManager();
        this.collisionManager.addRule(garbageRule);
        this.collisionManager.addRule(landingRule);
    }

    public void onShow() {
        spawnCrane();
        spawnNextHangingBlock();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.push(new PauseScreen(screenManager));
            return;
        }

        if (landingRule.isSettling()) {
            landingRule.update(delta);

            if (landingRule.consumeWordResetPending()) {
                removeBlocksForWord(landingRule.consumeResetWordIndex());
                fallingBlock = null;
                blockReleased = false;
                scheduleNextBlock(0.6f);
                return;
            }

            if (landingRule.consumeTowerStabilized()) {
                fallingBlock = null;
                blockReleased = false;
                scheduleNextBlock(0.25f);
                return;
            }
            return;
        }

        if (awaitingNext) {
            awaitTimer -= delta;
            if (awaitTimer <= 0f) {
                awaitingNext = false;
                handlePostBlockAction();
            }
            return;
        }

        if (!blockReleased && hangingBlock != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(mouse);

            float bx = bin.getX();
            float by = bin.getY();
            float bw = bin.getWidth();
            float bh = bin.getHeight();

            if (mouse.x >= bx && mouse.x <= bx + bw && mouse.y >= by && mouse.y <= by + bh) {
                discardHangingBlock();
                return;
            }
        }

        if (!blockReleased && hangingBlock != null && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            releaseBlock();
        }

        if (crane != null && crane.getMovementStrategy() != null) {
            crane.getMovementStrategy().update(crane, delta);
        }

        if (!blockReleased && hangingBlock != null) {
            Movement m = hangingBlock.getMovementStrategy();
            if (m instanceof RopeSwingMovement) {
                RopeSwingMovement swing = (RopeSwingMovement) m;
                swing.setAnchor(crane.getHookX(), crane.getHookY());
                swing.update(hangingBlock, delta);
            }
        }

        if (blockReleased && fallingBlock != null) {
            Movement m = fallingBlock.getMovementStrategy();
            if (m instanceof FallMovement) {
                FallMovement fall =  (FallMovement) m;
                fall.update(fallingBlock, delta);
            }
            if (tryResolveFallingBlockCollision()) return;
        }

        if (state.isGameOver()) {
            screenManager.set(new WordGameEndScreen(screenManager, state.getLevel() - 1));
            return;
        }

        if (state.isAllWordsComplete()) {
            state.advanceLevel();
            resetVisuals();
            spawnNextHangingBlock();
        }
    }

    private void spawnCrane() {
        crane = new CraneArm(SW / 2f - 60f, CRANE_Y);
        crane.setMovementStrategy(new CraneMovement(CRANE_MIN_X, CRANE_MAX_X));
    }

    private void spawnNextHangingBlock() {
        hangingBlock = blockFactory.createHangingBlock();
        hangingBlock.setMovementStrategy(new RopeSwingMovement(ROPE_LEN, 0f, crane.getHookX(), crane.getHookY()));
        blockReleased = false;
        fallingBlock = null;
    }

    private void releaseBlock() {
        fallingBlock = hangingBlock;
        fallingBlock.setMovementStrategy(new FallMovement());
        hangingBlock = null;
        blockReleased = true;
    }

    private void discardHangingBlock() {
        hangingBlock.setX(BIN_X + 10f);
        hangingBlock.setY(BIN_Y + 10f);
        collisionManager.applyTo(hangingBlock, null);
        hangingBlock = null;
        spawnNextHangingBlock();
    }

    private boolean tryResolveFallingBlockCollision() {
        if (fallingBlock == null) return false;

        for (int i = stackedBlocks.size() - 1; i >= 0; i--) {
            LetterBlock stacked = stackedBlocks.get(i);
            collisionManager.applyTo(fallingBlock, stacked);
            if (fallingBlock.isLanded() || fallingBlock.isDiscarded()) {
                resolveFallingBlockOutcome();
                return true;
            }
        }

        collisionManager.applyTo(fallingBlock, null);
        if (fallingBlock.isLanded() || fallingBlock.isDiscarded()) {
            resolveFallingBlockOutcome();
            return true;
        }

        return false;
    }

    private void resolveFallingBlockOutcome() {
        if (fallingBlock.isDiscarded()) {
            if (landingRule.isSettling()) {
                fallingBlock = null;
                blockReleased = false;
                return;
            }

            stackedBlocks.clear();
            landingRule.resetStack();
            fallingBlock = null;
            blockReleased = false;
            scheduleNextBlock(0.45f);
            return;
        }

        stackedBlocks.add(fallingBlock);
        fallingBlock = null;
        blockReleased = false;

        if (landingRule.isSettling()) return;
        scheduleNextBlock(0.25f);
    }

    private void removeBlocksForWord(int wordIndex) {
        if (wordIndex < 0) return;
        stackedBlocks.removeIf(block -> block.getWordIndex() == wordIndex);
    }

    private void scheduleNextBlock(float delay) {
        awaitingNext = true;
        awaitTimer = delay;
    }

    private void handlePostBlockAction() {
        if (state.isGameOver()) {
            screenManager.set(new WordGameEndScreen(screenManager, state.getLevel() - 1));
            return;
        }

        if (state.isAllWordsComplete()) {
            state.advanceLevel();
            resetVisuals();
        }

        spawnNextHangingBlock();
    }

    private void resetVisuals() {
        stackedBlocks.clear();
        landingRule.resetStack();
        hangingBlock = null;
        fallingBlock = null;
        blockReleased = false;
    }

    // getters for renderer
    public WordGameState getState() { return state; }
    public BlockLandingRule getLandingRule() { return landingRule; }
    public GarbageCan getBin() { return bin; }
    public CraneArm getCrane() { return crane; }
    public LetterBlock getHangingBlock() { return hangingBlock; }
    public LetterBlock getFallingBlock() { return fallingBlock; }
    public List<LetterBlock> getStackedBlocks() { return stackedBlocks; }
    public boolean isBlockReleased() { return blockReleased; }
}
