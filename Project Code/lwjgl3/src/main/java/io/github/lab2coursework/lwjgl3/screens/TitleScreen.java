package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class TitleScreen extends AbstractScreen {

    private final Texture titleBackground;
    private final Texture whitePixel; // Drawing solid rectangles behind the words
    private final GlyphLayout layout = new  GlyphLayout();

    // Constant variables
    private static final Color textColour = new Color(Color.WHITE);
    private static final Color blockColour = new Color(Color.DARK_GRAY);

    public TitleScreen(ScreenManager screenManager) {
        super(screenManager);
        titleBackground = new Texture("titleBackground.jpg");

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    // Helper function to draw label using GlyphLayout for better centering
    private void drawLabelWithBlock(String text, float centerX, float y) {
        layout.setText(font, text);

        // Add in the gap between the words and the rectangle itself
        float padX = 18f;
        float padY = 10f;

        // Set the size of the rectangles
        float rectW = layout.width + padX * 2f;
        float rectH = layout.height + padY * 2f;
        float rectX = centerX - rectW / 2f;
        float rectY = y - layout.height - padY;

        // Draw background block
        Color old = batch.getColor();
        batch.setColor(TitleScreen.blockColour);
        batch.draw(whitePixel, rectX, rectY, rectW, rectH);
        batch.setColor(old);

        // Draw text on top
        font.setColor(TitleScreen.textColour);
        font.draw(batch, text, centerX - layout.width / 2f, y);
    }


    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            screenManager.push(new WordGameScreen(screenManager, new WordBank())); // word game
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background screen first
        batch.setColor(Color.WHITE);
        batch.draw(titleBackground, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        float centreX = WORLD_WIDTH / 2f; // Centre of the screen
        font.getData().setScale(1f);

        // Drawing the labellings on screen with blocks to make it more readable
        drawLabelWithBlock(" P4-5 OOP Game Simulation ", centreX, 600);
        drawLabelWithBlock(">>  Press W to play the word game <<", centreX, 200);
        drawLabelWithBlock(">> Press ESC to Exit <<", centreX, 100);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        titleBackground.dispose();
        whitePixel.dispose();
    }
}
