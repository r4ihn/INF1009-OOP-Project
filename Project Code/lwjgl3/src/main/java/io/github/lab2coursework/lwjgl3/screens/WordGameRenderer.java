package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import io.github.lab2coursework.lwjgl3.entities.CraneArm;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.GameScore;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

import java.util.List;

/**
 * Handles all visual output for the gameplay screen.
 * Uses separate passes for textures, shapes, and text.
 */
public class WordGameRenderer {
    private final WordGameController controller;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final Texture backgroundImage = new Texture("gameScreenImage.jpg");
    private final Texture heartFullTexture = new Texture("heart_full.png");
    private final Texture heartEmptyTexture = new Texture("heart_empty.png");

    public WordGameRenderer(WordGameController controller) {
        this.controller = controller;
    }

    public void draw(float worldW, float worldH, SpriteBatch batch, BitmapFont font, Matrix4 projection) {
        // Pass 1: clear and draw full-screen background texture.
        Gdx.gl.glClearColor(0.12f, 0.16f, 0.22f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundImage, 0, 0, worldW, worldH);
        batch.end();

        // Pass 2: draw geometric gameplay bodies.
        shapeRenderer.setProjectionMatrix(projection);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawGround();
        drawRope();
        drawBodies();
        shapeRenderer.end();

        // Pass 3: draw HUD and labels on top.
        batch.begin();
        drawHUD(batch, font);
        drawWordDisplay(batch, font);
        drawLabels(batch, font);
        drawHearts(batch);
        batch.end();
    }

    private void drawGround() {
        shapeRenderer.setColor(new Color(0.3f, 0.22f, 0.1f, 1f));
        shapeRenderer.rect(0, 0, WordGameController.SW, WordGameController.GROUND_Y);
        shapeRenderer.setColor(new Color(0.45f, 0.32f, 0.15f, 1f));
        shapeRenderer.rect(0, WordGameController.GROUND_Y - 8f, WordGameController.SW, 8f);
    }

    private void drawRope() {
        CraneArm crane = controller.getCrane();
        LetterBlock hanging = controller.getHangingBlock();
        if (crane == null || hanging == null || controller.isBlockReleased()) return;

        shapeRenderer.setColor(Color.TAN);
        float hx = crane.getHookX();
        float hy = crane.getHookY();
        float bx = hanging.getX() + hanging.getWidth() / 2f;
        float by = hanging.getY() + hanging.getHeight();
        int segs = 6;

        for (int i = 0; i < segs; i++) {
            float t0 = (float) i / segs;
            float t1 = (float) (i + 1) / segs;
            float x0 = hx + (bx - hx) * t0;
            float y0 = hy + (by - hy) * t0;
            float x1 = hx + (bx - hx) * t1;
            float y1 = hy + (by - hy) * t1;
            shapeRenderer.rectLine(x0, y0, x1, y1, 3f);
        }
    }

    private void drawBodies() {
        if (controller.getCrane() != null) controller.getCrane().draw(shapeRenderer);
        if (controller.getHangingBlock() != null) controller.getHangingBlock().draw(shapeRenderer);
        if (controller.getFallingBlock() != null) controller.getFallingBlock().draw(shapeRenderer);

        // Apply temporary sway offset while a tower is settling, then restore real X.
        for (LetterBlock b : controller.getStackedBlocks()) {
            float originalX = b.getX();
            b.setX(originalX + controller.getLandingRule().getTowerSwayOffset(b.getWordIndex()));
            b.draw(shapeRenderer);
            b.setX(originalX);
        }

        controller.getBin().draw(shapeRenderer);
    }

    private void drawHUD(SpriteBatch batch, BitmapFont font) {
        WordGameState state = controller.getState();
        font.getData().setScale(0.9f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: " + state.getLevel(), 20, WordGameController.SH - 120);
        font.draw(batch, "Score: " + state.getTotalScore(), 20, WordGameController.SH - 140);
        font.draw(batch, "Combo: " + state.getComboCount(), 20, WordGameController.SH - 160);
        font.getData().setScale(1f);
    }

    private void drawWordDisplay(SpriteBatch batch, BitmapFont font) {
        WordGameState state = controller.getState();
        List<String> displays = state.getDisplayWords();
        List<String> targetWords = state.getTargetWords();
        float topY = WordGameController.SH - 50;
        float spacing = WordGameController.SW / 3.2f;
        float startX = 80f;

        for (int i = 0; i < GameScore.TARGET_WORD_COUNT; i++) {
            boolean completed = state.getGameScore().isWordCompleted(i);
            int points = state.getGameScore().getWordPoints(i);

            font.setColor(completed ? Color.LIME : Color.WHITE);
            font.getData().setScale(1.8f);
            font.draw(batch, targetWords.get(i), startX + i * spacing, topY);

            font.setColor(Color.CYAN);
            font.getData().setScale(1.3f);
            font.draw(batch, displays.get(i), startX + i * spacing, topY - 35);

            font.setColor(Color.YELLOW);
            font.getData().setScale(0.9f);
            font.draw(batch, points + " pts", startX + i * spacing + 5, topY - 60);
        }
        font.getData().setScale(1f);
    }

    private void drawLabels(SpriteBatch batch, BitmapFont font) {
        if (controller.getHangingBlock() != null) controller.getHangingBlock().drawLabel(batch, font);
        if (controller.getFallingBlock() != null) controller.getFallingBlock().drawLabel(batch, font);

        // Mirror sway offset for text so labels stay aligned to moving blocks.
        for (LetterBlock b : controller.getStackedBlocks()) {
            float originalX = b.getX();
            b.setX(originalX + controller.getLandingRule().getTowerSwayOffset(b.getWordIndex()));
            b.drawLabel(batch, font);
            b.setX(originalX);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "CLICK TO\nDISCARD", WordGameController.BIN_X + 8f, WordGameController.BIN_Y + 30f);
    }

    private void drawHearts(SpriteBatch batch) {
        WordGameState state = controller.getState();
        float heartX = 50;
        float heartY = WordGameController.SH - 670;
        float size = 25;
        float spacing = 5;

        for (int i = 0; i < WordGameState.MAX_LIVES; i++) {
            Texture tex = i < state.getLives() ? heartFullTexture : heartEmptyTexture;
            batch.draw(tex, heartX + i * (size + spacing), heartY, size, size);
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
        backgroundImage.dispose();
        heartFullTexture.dispose();
        heartEmptyTexture.dispose();
    }
}
