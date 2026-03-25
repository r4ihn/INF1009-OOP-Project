package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;

public class PauseScreen extends AbstractScreen {

    private final String[] menu = { "RESUME", "RESTART", "QUIT TO MENU" };
    private int selected = 0;

    private final Texture panelTexture; // setting background image
    private final Texture grayPixel;     // for dark overlay rectangle
    private final GlyphLayout layout = new GlyphLayout();

    public PauseScreen(ScreenManager screenManager) {
        super(screenManager);

        panelTexture = new Texture("pauseScreen.jpg");

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.DARK_GRAY);
        p.fill();
        grayPixel = new Texture(p);
        p.dispose();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selected = (selected - 1 + menu.length) % menu.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selected = (selected + 1) % menu.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            activateSelected();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.set(new TitleScreen(screenManager));
        }
    }

    private void activateSelected() {
        switch (selected) {
            case 0: // RESUME
                screenManager.pop();
                break;
            case 1: // RESTART (example: return to title or restart current game)
                screenManager.set(new TitleScreen(screenManager));
                break;
            case 2: // QUIT TO MENU
                screenManager.set(new TitleScreen(screenManager));
                break;
            default:
                break;
        }
    }

    @Override
    protected void draw(float delta) {
        // If ScreenManager only renders top screen, keep clear.
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float cx = WORLD_WIDTH / 2f;
        float cy = WORLD_HEIGHT / 2f;

        batch.begin();

        // Dim entire screen
        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(grayPixel, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Panel
        float panelW = WORLD_WIDTH;
        float panelH = WORLD_HEIGHT;
        float panelX = cx - panelW / 2f;
        float panelY = cy - panelH / 2f;

        batch.setColor(Color.WHITE);
        batch.draw(panelTexture, panelX, panelY, panelW, panelH);

        // Title
        font.getData().setScale(2.2f);
        font.setColor(new Color(0.95f, 0.93f, 0.75f, 1f));
        layout.setText(font, "PAUSED");
        font.draw(batch, "PAUSED", cx - layout.width / 2f, panelY + panelH - 70f);

        // Menu items
        font.getData().setScale(1.5f);
        float gap = 58f;

        // Vertical centre of the menu block
        float startY = cy + ((menu.length - 1) * gap) / 2f;

        for (int i = 0; i < menu.length; i++) {
            String text = menu[i];

            // Measure menu text only and not the selector
            layout.setText(font, text);
            float textX = cx - layout.width / 2f;
            float textY = startY - i * gap;

            if (i == selected) {
                font.setColor(new Color(0.95f, 0.93f, 0.75f, 1f));
                font.draw(batch, ">", textX - 24f, textY); // Selector will be drawn separately
            } else font.setColor(new Color(0.83f, 0.83f, 0.76f, 1f));

            font.draw(batch, text, textX, textY);
        }

        font.getData().setScale(1f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        panelTexture.dispose();
        grayPixel.dispose();
    }
}
