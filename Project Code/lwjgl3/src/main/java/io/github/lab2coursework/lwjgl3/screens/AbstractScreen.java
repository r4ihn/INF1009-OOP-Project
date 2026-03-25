package io.github.lab2coursework.lwjgl3.screens;

// Import statements
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Base class for all screens using a shared camera/viewport setup.
 * Subclasses implement update logic and draw logic separately.
 */
public abstract class AbstractScreen extends ScreenAdapter {
    protected static final float WORLD_WIDTH = 1280f;
    protected static final float WORLD_HEIGHT = 720f;

    protected final ScreenManager screenManager;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected OrthographicCamera camera;
    protected Viewport viewport;

    private boolean disposed;

    public AbstractScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Default font

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.viewport.apply(true);
        this.camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        this.camera.update();
    }

    @Override
    public void render(float delta) {
        // Template flow: update first, then draw only if this screen is still active.
        update(delta);
        // If update() switched screens, this screen may have been disposed.
        if (disposed || screenManager.peak() != this) {
            return;
        }

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        draw(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    protected abstract void update(float delta);

    @Override
    public void dispose() {
        disposed = true;
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
