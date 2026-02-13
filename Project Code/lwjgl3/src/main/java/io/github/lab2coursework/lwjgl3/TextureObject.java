package io.github.lab2coursework.lwjgl3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TextureObject extends Entity {
    private Texture tex;

    public TextureObject() {
    }

    public TextureObject(String path, float x, float y, float speed) {
        // Pass x, y, a default color (White), and speed to Entity
        super(x, y, Color.WHITE, speed);
        this.tex = new Texture(Gdx.files.internal(path));
    }

    public Texture getTexture() {
        return tex;
    }

    public void setTexture(Texture tex) {
        this.tex = tex;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(this.getTexture(), this.getX(), this.getY(),
            this.getTexture().getWidth(), this.getTexture().getHeight());
    }

    // No-op ShapeRenderer draw to satisfy abstract contract
    @Override
    public void draw(ShapeRenderer shape) { }

    @Override
    public void update(){
        this.movement();
    }
    
    public void userMovement(){

    }

    public  void AiMovement(){
        
    }
}