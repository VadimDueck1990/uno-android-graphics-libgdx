package de.hhn.aib.swlab.wise1920.group06.core.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import sun.rmi.runtime.Log;

public class CardUi {

    Image image;
    Card card;
    Texture texture;

    public CardUi(Card card){
        this.card = card;
        String filename = card.getCardResource();
        this.texture = new Texture(Gdx.files.internal(filename));
        this.image = new Image(texture);
        this.image.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(filename)))));
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
