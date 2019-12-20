package de.hhn.aib.swlab.wise1920.group06.exercise3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.hhn.aib.swlab.wise1920.group06.exercise3.screens.PlayScreen;
import sun.rmi.runtime.Log;


public class MyUnoGame extends Game {
    public SpriteBatch batch;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

    @Override
    public void render () {
        super.render();
    }
}
