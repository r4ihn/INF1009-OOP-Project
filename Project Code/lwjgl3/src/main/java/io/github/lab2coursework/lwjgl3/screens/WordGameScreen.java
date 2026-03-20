package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.collision.BlockLandingRule;
import io.github.lab2coursework.lwjgl3.collision.GarbageCollectionRule;
import io.github.lab2coursework.lwjgl3.entities.*;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.movement.CraneMovement;
import io.github.lab2coursework.lwjgl3.movement.FallMovement;
import io.github.lab2coursework.lwjgl3.movement.RopeSwingMovement;
import io.github.lab2coursework.lwjgl3.wordgame.LetterBlockFactory;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Main gameplay screen for the word-building crane game.
 *
 * Template Method pattern:
 *   render() → update() → draw()   (defined in AbstractScreen)
 *
 * Design patterns used here:
 *   - Factory Method  : LetterBlockFactory
 *   - Strategy        : CraneMovement, FallMovement, RopeSwingMovement
 *   - Strategy        : BlockLandingRule, GarbageCollectionRule (CollisionRule)
 *   - Dependency Injection : WordBank, WordGameState passed in — no singletons
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

    // Managers (local to this screen, consistent with existing code style)
    private final EntityManager  entityManager;
    private final MovementManager movementManager;
    private final ShapeRenderer  shapeRenderer;

    private Texture heartFullTexture;
    private Texture heartEmptyTexture;

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

        entityManager  = new EntityManager();
        shapeRenderer  = new ShapeRenderer();
        movementManager = new MovementManager(entityManager.getEntities());

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

        // ── Highlight bin when G held ─────────────────────────────────────────
        bin.setHighlighted(Gdx.input.isKeyPressed(Input.Keys.G));

        // ── Awaiting next block spawn (brief delay after land/discard) ────────
        if (awaitingNext) {
            awaitTimer -= delta;
            if (awaitTimer <= 0f) {
                awaitingNext = false;
                handlePostBlockAction();
            }
            return;
        }

        // ── Discard current hanging block (G key) ─────────────────────────────
        if (!blockReleased && hangingBlock != null
            && Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            discardHangingBlock();
            return;
        }

        // ── Drop block (SPACE) ────────────────────────────────────────────────
        if (!blockReleased && hangingBlock != null
            && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            releaseBlock();
        }

        // ── Update crane movement ─────────────────────────────────────────────
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

            // Check landing
            if (landingRule.matches(fallingBlock, null)) {
                landingRule.resolve(fallingBlock, null);
                stackedBlocks.add(fallingBlock);
                fallingBlock = null;
                blockReleased = false;
                scheduleNextBlock(0.4f);
                return;
            }
        }

        // ── Check game-over / level-complete ──────────────────────────────────
        if (state.isGameOver()) {
            screenManager.set(new WordGameEndScreen(screenManager, state.getLevel() - 1));
            return;
        }
        if (state.isWordComplete()) {
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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGround();
        drawRope();
        if (crane != null)       crane.draw(shapeRenderer);
        if (hangingBlock != null) hangingBlock.draw(shapeRenderer);
        if (fallingBlock != null) fallingBlock.draw(shapeRenderer);
        for (LetterBlock b : stackedBlocks) b.draw(shapeRenderer);
        bin.draw(shapeRenderer);

        shapeRenderer.end();

        // 3. SpriteBatch pass (text labels and heart icons)
        batch.begin();

        drawHUD();
        drawWordDisplay();
        if (hangingBlock != null) hangingBlock.drawLabel(batch, font);
        if (fallingBlock != null) fallingBlock.drawLabel(batch, font);
        for (LetterBlock b : stackedBlocks) b.drawLabel(batch, font);
        drawBinLabel();

        // Draw lives as hearts
        float heartX = 80;
        float heartY = SH - 85;
        float heartSize = 25; // Adjust size as needed
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
            new RopeSwingMovement(ROPE_LEN, 0.35f, crane.getHookX(), crane.getHookY())
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
        // Move the hanging block to the bin position and trigger garbage rule
        hangingBlock.setX(BIN_X + 10f);
        hangingBlock.setY(BIN_Y + 10f);
        garbageRule.resolve(hangingBlock, null);
        hangingBlock = null;
        scheduleNextBlock(0.3f);
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
        if (state.isWordComplete()) {
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
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: " + state.getLevel(), 20, SH - 20);
        font.draw(batch, "Category: " + state.getCategoryName(), 20, SH - 45);
        font.draw(batch, "Lives:", 20, SH - 70);

        // Controls reminder
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "SPACE = drop block   G = discard block   ESC = pause", 20, 30);
    }

    private void drawWordDisplay() {
        String display = state.getDisplayWord();

        font.getData().setScale(2.5f);
        font.setColor(Color.CYAN);

        GlyphLayout layout = new GlyphLayout(font, display);
        float textX = (SW - layout.width) / 2f;
        font.draw(batch, display, textX, SH - 20);

        font.getData().setScale(1f); // reset scale
    }

    private void drawBinLabel() {
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "BIN", BIN_X + 18f, BIN_Y - 6f);
    }
}
