package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
 * Design patterns:
 *   - Template Method  : AbstractScreen.render() → update() → draw()
 *   - Factory Method   : LetterBlockFactory
 *   - Strategy         : CraneMovement, FallMovement, RopeSwingMovement (via MovementManager)
 *   - Strategy         : BlockLandingRule, GarbageCollectionRule (CollisionRule)
 *   - Dependency Inj.  : WordBank injected — no singletons
 */
public class WordGameScreen extends AbstractScreen {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int   SW          = 1280;
    private static final int   SH          = 720;
    private static final float GROUND_Y    = 60f;
    private static final float CRANE_Y     = SH - 160f;
    private static final float ROPE_LEN    = 110f;
    private static final float CRANE_MIN_X = 100f;
    private static final float CRANE_MAX_X = SW - 220f;
    private static final float BIN_X       = SW - 140f;
    private static final float BIN_Y       = GROUND_Y;

    // ── Game-state objects ────────────────────────────────────────────────────
    private final WordGameState          state;
    private final LetterBlockFactory     blockFactory;
    private final BlockLandingRule       landingRule;
    private final GarbageCollectionRule  garbageRule;

    // Live entity references (crane and bin also registered in EntityManager)
    private CraneArm    crane;
    private GarbageCan  bin;
    private LetterBlock hangingBlock;   // block currently on the rope
    private LetterBlock fallingBlock;   // block after SPACE is pressed

    // Stacked (landed) blocks — kept separately for label drawing
    private final List<LetterBlock> stackedBlocks = new ArrayList<>();

    // ── Managers ──────────────────────────────────────────────────────────────
    private final EntityManager   entityManager;
    private final MovementManager movementManager;  // FIX: now actually used
    private final ShapeRenderer   shapeRenderer;

    // ── State flags ───────────────────────────────────────────────────────────
    private boolean blockReleased;
    private boolean awaitingNext;
    private float   awaitTimer;

    // ─────────────────────────────────────────────────────────────────────────
    public WordGameScreen(ScreenManager screenManager, WordBank wordBank) {
        super(screenManager);

        state        = new WordGameState(wordBank);
        blockFactory = new LetterBlockFactory(state);

        // FIX: create EntityManager first, then wire MovementManager to its list
        entityManager   = new EntityManager();
        movementManager = new MovementManager(entityManager.getEntities());
        shapeRenderer   = new ShapeRenderer();

        // Bin is a static entity — register it once
        bin         = new GarbageCan(BIN_X, BIN_Y);
        landingRule = new BlockLandingRule(state);
        garbageRule = new GarbageCollectionRule(bin, state);

        // FIX: add bin to EntityManager so it is drawn by the manager
        entityManager.addEntities(bin);
    }

    // ── AbstractScreen lifecycle ──────────────────────────────────────────────

    @Override
    public void show() {
        super.show();
        spawnCrane();
        spawnNextHangingBlock();
    }

    @Override
    protected void update(float delta) {
        // Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.push(new PauseScreen(screenManager));
            return;
        }

        bin.setHighlighted(Gdx.input.isKeyPressed(Input.Keys.G));

        // Brief delay before spawning next block
        if (awaitingNext) {
            awaitTimer -= delta;
            if (awaitTimer <= 0f) {
                awaitingNext = false;
                handlePostBlockAction();
            }
            return;
        }

        // Discard hanging block (G key)
        if (!blockReleased && hangingBlock != null
            && Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            discardHangingBlock();
            return;
        }

        // Drop block (SPACE)
        if (!blockReleased && hangingBlock != null
            && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            releaseBlock();
        }

        // FIX: update the rope anchor BEFORE MovementManager runs,
        //      so the hanging block tracks the crane correctly each frame
        if (!blockReleased && hangingBlock != null) {
            RopeSwingMovement swing =
                (RopeSwingMovement) hangingBlock.getMovementStrategy();
            if (swing != null) {
                swing.setAnchor(crane.getHookX(), crane.getHookY());
            }
        }

        // FIX: single MovementManager call replaces all manual strategy.update() calls
        //      for crane (registered in EntityManager) and falling block (registered on release)
        movementManager.updateMovement(delta);

        // Hanging block is NOT in EntityManager, so update its swing manually
        if (!blockReleased && hangingBlock != null) {
            RopeSwingMovement swing =
                (RopeSwingMovement) hangingBlock.getMovementStrategy();
            if (swing != null) swing.update(hangingBlock, delta);
        }

        // Check landing for the falling block
        if (blockReleased && fallingBlock != null) {
            if (landingRule.matches(fallingBlock, null)) {
                landingRule.resolve(fallingBlock, null);

                // Remove from EntityManager's live list and move to stacked list
                entityManager.getEntities().remove(fallingBlock);
                stackedBlocks.add(fallingBlock);

                fallingBlock  = null;
                blockReleased = false;
                scheduleNextBlock(0.4f);
                return;
            }
        }

        // Game-over / level-complete checks
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

        // FIX: EntityManager draws crane and bin (registered entities)
        entityManager.draw(null, shapeRenderer);

        // Hanging/falling/stacked blocks drawn separately (lifecycle-managed manually)
        if (hangingBlock != null) hangingBlock.draw(shapeRenderer);
        if (fallingBlock != null) fallingBlock.draw(shapeRenderer);
        for (LetterBlock b : stackedBlocks) b.draw(shapeRenderer);

        shapeRenderer.end();

        // 3. SpriteBatch pass (text labels)
        batch.begin();
        drawHUD();
        drawWordDisplay();
        if (hangingBlock != null) hangingBlock.drawLabel(batch, font);
        if (fallingBlock != null) fallingBlock.drawLabel(batch, font);
        for (LetterBlock b : stackedBlocks) b.drawLabel(batch, font);
        drawBinLabel();
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void spawnCrane() {
        crane = new CraneArm(SW / 2f - 60f, CRANE_Y);
        crane.setMovementStrategy(new CraneMovement(CRANE_MIN_X, CRANE_MAX_X));
        // FIX: register crane with EntityManager so MovementManager updates it
        entityManager.addEntities(crane);
    }

    private void spawnNextHangingBlock() {
        hangingBlock = blockFactory.createHangingBlock();
        hangingBlock.setMovementStrategy(
            new RopeSwingMovement(ROPE_LEN, 0.35f, crane.getHookX(), crane.getHookY())
        );
        blockReleased = false;
        fallingBlock  = null;
        // NOTE: hanging block is NOT added to EntityManager because its
        // lifecycle (anchor tracking, swap to falling) is managed manually.
        // Its movement is updated via the direct swing.update() call above.
    }

    private void releaseBlock() {
        fallingBlock = hangingBlock;
        fallingBlock.setMovementStrategy(new FallMovement());
        hangingBlock  = null;
        blockReleased = true;
        // FIX: add falling block to EntityManager so MovementManager applies FallMovement
        entityManager.addEntities(fallingBlock);
    }

    private void discardHangingBlock() {
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
        // Remove all LetterBlock entities from EntityManager (landed/falling ones)
        entityManager.getEntities().removeIf(e -> e instanceof LetterBlock);
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
        float hx = crane.getHookX(), hy = crane.getHookY();
        float bx = hangingBlock.getX() + hangingBlock.getWidth()  / 2f;
        float by = hangingBlock.getY() + hangingBlock.getHeight();
        int segs = 6;
        for (int i = 0; i < segs; i++) {
            float t0 = (float) i       / segs;
            float t1 = (float)(i + 1)  / segs;
            shapeRenderer.rectLine(
                hx + (bx - hx) * t0, hy + (by - hy) * t0,
                hx + (bx - hx) * t1, hy + (by - hy) * t1,
                3f
            );
        }
    }

    private void drawHUD() {
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: "    + state.getLevel(),        20, SH - 20);
        font.draw(batch, "Category: " + state.getCategoryName(), 20, SH - 45);

        StringBuilder hearts = new StringBuilder("Lives: ");
        for (int i = 0; i < WordGameState.MAX_LIVES; i++) {
            hearts.append(i < state.getLives() ? "♥ " : "♡ ");
        }
        font.draw(batch, hearts.toString(), 20, SH - 70);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "SPACE = drop block   G = discard block   ESC = pause", 20, 30);
    }

    private void drawWordDisplay() {
        String display = state.getDisplayWord();
        font.getData().setScale(2.5f);
        font.setColor(Color.CYAN);
        GlyphLayout layout = new GlyphLayout(font, display);
        font.draw(batch, display, (SW - layout.width) / 2f, SH - 20);
        font.getData().setScale(1f);
    }

    private void drawBinLabel() {
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "BIN", BIN_X + 18f, BIN_Y - 6f);
    }
}
