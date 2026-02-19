package io.github.lab2coursework.lwjgl3.screens;

// Import statements
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;

public abstract class AbstractScreen extends ScreenAdapter {
    protected final ScreenManager screenManager;
    protected SpriteBatch batch;
    protected BitmapFont font;

    public AbstractScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Default font
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    protected abstract void update(float delta);

    @Override
    public void dispose() {
        // Check if not null to prevent crashing during rapid screen switching
        if (batch != null){
            batch.dispose();
        }
        if (font != null){
            font.dispose();
        }
    }

    protected abstract void draw(float delta);
}
