package de.hhn.aib.swlab.wise1920.group06.core.models.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

// represents all flipped/hidden card
public class FlippedCardActor extends Actor {
    TextureRegion textureRegion;
    final float width; // actual card width
    final float height;

    public FlippedCardActor(){
        super();
        textureRegion = new TextureRegion();
        width = Gdx.graphics.getWidth() / 11.0f; // actual card width
        height = width * 1.44845f;
    }

    public void setTexture(Texture t) {
        textureRegion.setRegion(t);
        setSize(width, height);
    }

    public void act(float dt) { super.act(dt); }

    public void draw(Batch b, float parentAlpha) {
        super.draw(b, parentAlpha);
        b.draw(textureRegion, getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation());

    }
}
