package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import io.github.lab2coursework.lwjgl3.collision.BlockLandingRule;
import io.github.lab2coursework.lwjgl3.collision.GarbageCollectionRule;
import io.github.lab2coursework.lwjgl3.entities.CraneArm;
import io.github.lab2coursework.lwjgl3.entities.GarbageCan;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.managers.CollisionManager;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.movement.CraneMovement;
import io.github.lab2coursework.lwjgl3.movement.FallMovement;
import io.github.lab2coursework.lwjgl3.movement.RopeSwingMovement;
import io.github.lab2coursework.lwjgl3.wordgame.GameScore;
import io.github.lab2coursework.lwjgl3.wordgame.LetterBlockFactory;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

import java.util.ArrayList;
import java.util.List;


/**
 * Main gameplay screen for the word-building crane game.
 * Now supports:
 * - Keyboard-controlled crane (A/D or LEFT/RIGHT)
 * - 3 simultaneous target words
 * - Mouse-click garbage can for discarding
 * - Rule-based block stacking
 * - Combo points every 3 words
 * - Multiple difficulty levels
 */
public class WordGameScreen extends AbstractScreen {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int   SW          = 1280;
    private static final int   SH          = 720;
    private static final float GROUND_Y    = 60f;
    private static final float CRANE_Y     = SH - 160f;  // top of crane mast
    private static final float ROPE_LEN    = 110f;
    private static final float CRANE_MIN_X = 100f;
    private static final float CRANE_MAX_X = SW - 220f;
    private static final float BIN_X       = SW - 140f;
    private static final float BIN_Y       = GROUND_Y;

    // ── Game objects ─────────────────────────────────────────────────────────
    private final WordGameState      state;
    private final LetterBlockFactory blockFactory;
    private final BlockLandingRule   landingRule;
    private final GarbageCollectionRule garbageRule;

    private CraneArm    crane;
    private GarbageCan  bin;
    private LetterBlock hangingBlock;    // block currently on the rope
    private LetterBlock fallingBlock;    // block after SPACE is pressed (in free-fall)

    // All stacked (landed) blocks — for drawing only
    private final List<LetterBlock> stackedBlocks = new ArrayList<>();

    private final CollisionManager collisionManager;
    private final ShapeRenderer shapeRenderer;

    private final Texture heartFullTexture;
    private final Texture heartEmptyTexture;

    // ── State flags ───────────────────────────────────────────────────────────
    private boolean blockReleased;   // true while block is in free-fall
    private boolean awaitingNext;    // brief pause before spawning next block
    private float   awaitTimer;

    public WordGameScreen(ScreenManager screenManager, WordBank wordBank) {
        super(screenManager);
        this.state        = new WordGameState(wordBank);
        this.blockFactory = new LetterBlockFactory(state);

        // Entities
        bin           = new GarbageCan(BIN_X, BIN_Y);
        landingRule   = new BlockLandingRule(state);
        garbageRule   = new GarbageCollectionRule(bin, state);

        shapeRenderer = new ShapeRenderer();
        collisionManager = new CollisionManager();
        collisionManager.addRule(garbageRule);
        collisionManager.addRule(landingRule);

        heartFullTexture  = new Texture("heart_full.png");
        heartEmptyTexture = new Texture("heart_empty.png");
    }

    // ── AbstractScreen lifecycle ──────────────────────────────────────────────

    @Override
    public void show() {
        super.show(); // creates batch + font from AbstractScreen fix
        spawnCrane();
        spawnNextHangingBlock();
    }

    @Override
    protected void update(float delta) {
        // ── Pause ─────────────────────────────────────────────────────────────
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.push(new PauseScreen(screenManager));
            return;
        }

        // ── Tower sway / settle animation ─────────────────────────────────────
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

        // ── Awaiting next block spawn (brief delay after land/discard) ────────
        if (awaitingNext) {
            awaitTimer -= delta;
            if (awaitTimer <= 0f) {
                awaitingNext = false;
                handlePostBlockAction();
            }
            return;
        }

        // ── Mouse click on garbage can ────────────────────────────────────────
        if (!blockReleased && hangingBlock != null
            && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Get mouse coordinates
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();  // Flip Y axis

            // Check if click is within bin bounds
            float binX = bin.getX();
            float binY = bin.getY();
            float binW = bin.getWidth();
            float binH = bin.getHeight();

            if (mouseX >= binX && mouseX <= binX + binW &&
                mouseY >= binY && mouseY <= binY + binH) {
                discardHangingBlock();
                return;
            }
        }

        // ── Drop block (SPACE) ────────────────────────────────────────────────
        if (!blockReleased && hangingBlock != null
            && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            releaseBlock();
        }

        // ── Update crane movement (keyboard controlled) ────────────────────────
        if (crane != null) {
            crane.getMovementStrategy().update(crane, delta);
        }

        // ── Update rope / falling block ───────────────────────────────────────
        if (!blockReleased && hangingBlock != null) {
            RopeSwingMovement swing = (RopeSwingMovement) hangingBlock.getMovementStrategy();
            if (swing != null) {
                swing.setAnchor(crane.getHookX(), crane.getHookY());
                swing.update(hangingBlock, delta);
            }
        }

        if (blockReleased && fallingBlock != null) {
            FallMovement fall = (FallMovement) fallingBlock.getMovementStrategy();
            if (fall != null) {
                fall.update(fallingBlock, delta);
            }

            if (tryResolveFallingBlockCollision()) {
                return;
            }
        }

        // ── Check game-over / level-complete ──────────────────────────────────
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

    @Override
    protected void draw(float delta) {
        // 1. Clear
        Gdx.gl.glClearColor(0.12f, 0.16f, 0.22f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. Shapes pass
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGround();
        drawRope();
        if (crane != null)       crane.draw(shapeRenderer);
        if (hangingBlock != null) hangingBlock.draw(shapeRenderer);
        if (fallingBlock != null) fallingBlock.draw(shapeRenderer);

        // Draw stacked blocks with Smart Stacking visualization
        drawSmartStack(shapeRenderer);

        bin.draw(shapeRenderer);

        shapeRenderer.end();

        // 3. SpriteBatch pass (text labels and heart icons)
        batch.begin();

        drawHUD();
        drawWordDisplay();
        if (hangingBlock != null) hangingBlock.drawLabel(batch, font);
        if (fallingBlock != null) fallingBlock.drawLabel(batch, font);
        drawStackedBlockLabels();

        // Draw visual separators between word sections
        drawStackSeparators();

        drawBinLabel();

        // update() mouse click selection
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(mouse);
        float mouseX = mouse.x;
        float mouseY = mouse.y;
        
        // Draw lives as hearts
        float heartX = 80;
        float heartY = SH - 85;
        float heartSize = 25;
        float heartSpacing = 5;

        for (int i = 0; i < WordGameState.MAX_LIVES; i++) {
            if (i < state.getLives()) {
                batch.draw(heartFullTexture, heartX + i * (heartSize + heartSpacing), heartY, heartSize, heartSize);
            } else {
                batch.draw(heartEmptyTexture, heartX + i * (heartSize + heartSpacing), heartY, heartSize, heartSize);
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (heartFullTexture != null) heartFullTexture.dispose();
        if (heartEmptyTexture != null) heartEmptyTexture.dispose();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void spawnCrane() {
        crane = new CraneArm(SW / 2f - 60f, CRANE_Y);
        crane.setMovementStrategy(new CraneMovement(CRANE_MIN_X, CRANE_MAX_X));
    }

    private void spawnNextHangingBlock() {
        hangingBlock = blockFactory.createHangingBlock();
        hangingBlock.setMovementStrategy(
            new RopeSwingMovement(ROPE_LEN, 0f, crane.getHookX(), crane.getHookY())
        );
        blockReleased = false;
        fallingBlock  = null;
    }

    private void releaseBlock() {
        fallingBlock = hangingBlock;
        fallingBlock.setMovementStrategy(new FallMovement());
        hangingBlock  = null;
        blockReleased = true;
    }

    private void discardHangingBlock() {
        // Move the hanging block to the bin position and let CollisionManager apply rules.
        hangingBlock.setX(BIN_X + 10f);
        hangingBlock.setY(BIN_Y + 10f);
        collisionManager.applyTo(hangingBlock, null);
        hangingBlock = null;
        spawnNextHangingBlock();
    }

    private boolean tryResolveFallingBlockCollision() {
        if (fallingBlock == null) {
            return false;
        }

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
            // Improper stacking now resets only the matching word after the sway finishes.
            if (landingRule.isSettling()) {
                fallingBlock = null;
                blockReleased = false;
                return;
            }

            // Wrong letter: WordGameState already reset the whole attempt via loseLife().
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

        if (landingRule.isSettling()) {
            return;
        }

        scheduleNextBlock(0.25f);
    }


    private void removeBlocksForWord(int wordIndex) {
        if (wordIndex < 0) {
            return;
        }
        stackedBlocks.removeIf(block -> block.getWordIndex() == wordIndex);
    }

    private void scheduleNextBlock(float delay) {
        awaitingNext = true;
        awaitTimer   = delay;
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
        hangingBlock  = null;
        fallingBlock  = null;
        blockReleased = false;
    }

    // ── Draw helpers ──────────────────────────────────────────────────────────

    private void drawGround() {
        shapeRenderer.setColor(new Color(0.3f, 0.22f, 0.1f, 1f));
        shapeRenderer.rect(0, 0, SW, GROUND_Y);
        shapeRenderer.setColor(new Color(0.45f, 0.32f, 0.15f, 1f));
        shapeRenderer.rect(0, GROUND_Y - 8f, SW, 8f);
    }

    private void drawRope() {
        if (crane == null || hangingBlock == null || blockReleased) return;
        shapeRenderer.setColor(Color.TAN);
        // Draw 6 rope segments from hook to block centre
        float hx = crane.getHookX();
        float hy = crane.getHookY();
        float bx = hangingBlock.getX() + hangingBlock.getWidth()  / 2f;
        float by = hangingBlock.getY() + hangingBlock.getHeight();
        int segs = 6;
        for (int i = 0; i < segs; i++) {
            float t0 = (float) i       / segs;
            float t1 = (float)(i + 1)  / segs;
            float x0 = hx + (bx - hx) * t0;
            float y0 = hy + (by - hy) * t0;
            float x1 = hx + (bx - hx) * t1;
            float y1 = hy + (by - hy) * t1;
            shapeRenderer.rectLine(x0, y0, x1, y1, 3f);
        }
    }

    private void drawHUD() {
        // Left side: Level, Score, Combo (moved down to avoid overlap with hearts)
        font.getData().setScale(0.9f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: " + state.getLevel(), 20, SH - 120);
        font.draw(batch, "Score: " + state.getTotalScore(), 20, SH - 140);
        font.draw(batch, "Combo: " + state.getComboCount(), 20, SH - 160);

        // Controls reminder at bottom
        font.getData().setScale(0.7f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "A/D or LEFT/RIGHT = move crane   SPACE = drop block   CLICK BIN = discard   ESC = pause", 20, 30);
        font.getData().setScale(1f);
    }

    private void drawWordDisplay() {
        // Draw 3 target words at the top with clean layout
        List<String> displays = state.getDisplayWords();
        List<String> targetWords = state.getTargetWords();
        float topY = SH - 50;  // Higher up to avoid overlap
        float spacing = SW / 3.2f;
        float startX = 80;

        for (int i = 0; i < GameScore.TARGET_WORD_COUNT; i++) {
            String display = displays.get(i);
            String targetWord = targetWords.get(i);
            boolean completed = state.getGameScore().isWordCompleted(i);
            int points = state.getGameScore().getWordPoints(i);

            // Set color based on completion
            if (completed) {
                font.setColor(Color.LIME);
            } else {
                font.setColor(Color.WHITE);
            }

            // Draw target word (the goal) - larger and clear
            font.getData().setScale(1.8f);
            font.draw(batch, targetWord, startX + i * spacing, topY);

            // Draw progress below target - smaller
            font.getData().setScale(1.3f);
            font.setColor(Color.CYAN);
            font.draw(batch, display, startX + i * spacing, topY - 35);

            // Draw points - small and yellow
            font.getData().setScale(0.9f);
            font.setColor(Color.YELLOW);
            font.draw(batch, points + " pts", startX + i * spacing + 5, topY - 60);
        }

        font.getData().setScale(1f); // reset scale
    }

    private void drawBinLabel() {
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "CLICK TO\nDISCARD", BIN_X + 8f, BIN_Y + 30f);
    }

    private void drawSmartStack(ShapeRenderer shapeRenderer) {
        for (LetterBlock b : stackedBlocks) {
            drawStackedBlockShape(b, shapeRenderer);
        }
    }

    private void drawStackedBlockLabels() {
        for (LetterBlock b : stackedBlocks) {
            drawStackedBlockLabel(b);
        }
    }

    private void drawStackedBlockShape(LetterBlock block, ShapeRenderer shapeRenderer) {
        float originalX = block.getX();
        block.setX(originalX + landingRule.getTowerSwayOffset(block.getWordIndex()));
        block.draw(shapeRenderer);
        block.setX(originalX);
    }

    private void drawStackedBlockLabel(LetterBlock block) {
        float originalX = block.getX();
        block.setX(originalX + landingRule.getTowerSwayOffset(block.getWordIndex()));
        block.drawLabel(batch, font);
        block.setX(originalX);
    }

    private void drawStackSeparators() {
        font.getData().setScale(0.8f);
        font.setColor(Color.LIGHT_GRAY);

        for (int i = 0; i < GameScore.TARGET_WORD_COUNT; i++) {
            if (!landingRule.hasStackX(i)) {
                continue;
            }

            float x = landingRule.getStackX(i);
            font.draw(batch, "Word " + (i + 1), x - 4f, GROUND_Y + 22f);
        }

        font.getData().setScale(1f);
    }
}
