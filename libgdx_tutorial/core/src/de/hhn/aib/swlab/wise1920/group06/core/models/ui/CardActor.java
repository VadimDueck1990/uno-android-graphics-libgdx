package de.hhn.aib.swlab.wise1920.group06.core.models.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.hhn.aib.swlab.wise1920.group06.core.models.Card;

public class CardActor extends Actor{
    TextureRegion textureRegion;
    final float width; // actual card width
    final float height;
    Card card;

    public CardActor(Card card){
        super();
        this.card = card;
        textureRegion = new TextureRegion();
        width = Gdx.graphics.getWidth() / 7.66667f; // actual card width
        height = width * 1.44845f;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
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
