package de.hhn.aib.swlab.wise1920.group06.exercise3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;


public class MyUnoGame extends ApplicationAdapter {
	private Stage stage;
	private List<Image> ownHand;
	private List<Image> leftHand;
	private List<Image> topHand;
	private List<Image> rightHand;

	
	@Override
	public void create () {
		// create a stage and set it to full screen
		stage = new Stage(new ScreenViewport());

		// dummy numbers to test hand generation
		int ownNumber = 3;
		int leftNumber = 4;
		int topNumber = 5;
		int rightNumber = 5;

		  = new ArrayList<>();
		leftHand = new ArrayList<>();
		topHand = new ArrayList<>();
		rightHand = new ArrayList<>();

		final float WIDTH_HEIGHT_RATIO = 1.44845f; // the width to height ratio
		final float WIDTH_SCREEN_RATIO = 7.66667f; //  the card to screen ratio
		final float OTHERS_SCREEN_RATIO = 11.0f; // the card to screen ratio of the other hands
		final float CARD_ROTATION = -7.0f;

		// own card size
		final float ownCardWidth = Gdx.graphics.getWidth() / WIDTH_SCREEN_RATIO; // actual card width
		final float ownCardHeight = ownCardWidth * WIDTH_HEIGHT_RATIO;
		// all other card sizes
		final float foreignCardWidth = Gdx.graphics.getWidth() / OTHERS_SCREEN_RATIO; // actual card width
		final float foreignCardHeight = foreignCardWidth * WIDTH_HEIGHT_RATIO;

		// fill all hands with dummy cards
		createOwnHand(ownNumber, ownCardWidth, ownCardHeight);
		createotherHands(leftNumber, foreignCardWidth, foreignCardHeight, leftHand);
		createotherHands(topNumber, foreignCardWidth, foreignCardHeight, topHand);
		createotherHands(rightNumber, foreignCardWidth, foreignCardHeight, rightHand);


		// load table texture and create an Image
		Texture tableTexture = new Texture(Gdx.files.internal("Table_1.png"));
		Image table = new Image(tableTexture);
		table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		table.setPosition(0,0);
		stage.addActor(table);

		// same for card
		/*Texture otherTexture = new Texture(Gdx.files.internal("Deck.png"));
		Image otherCard = new Image(otherTexture);
		otherCard.setSize(foreignCardWidth, foreignCardHeight);
		otherCard.setPosition(Gdx.graphics.getWidth() - ownCardWidth*2, Gdx.graphics.getHeight()-foreignCardHeight/3 * 2);
		stage.addActor(otherCard);*/

		placeOwnCards(Gdx.graphics.getWidth(), ownCardWidth, ownCardHeight, CARD_ROTATION);
		placeTopCards(Gdx.graphics.getWidth(), foreignCardWidth, foreignCardHeight, CARD_ROTATION, topHand);
		placeLeftCards(Gdx.graphics.getHeight(), foreignCardWidth, foreignCardHeight, CARD_ROTATION, leftHand);

		// same for card
		Texture wildTexture = new Texture(Gdx.files.internal("Wild_Draw.png"));
		Image wildCard = new Image(wildTexture);
		wildCard.setSize(ownCardWidth, ownCardHeight);
		wildCard.setPosition(Gdx.graphics.getWidth()/2, ownCardWidth * 4.0f);
		stage.addActor(wildCard);
//
//		// same for card
//		Texture numberTexture = new Texture(Gdx.files.internal("Red_2.png"));
//		Image numberCard = new Image(numberTexture);
//		numberCard.setSize(ownCardWidth, ownCardHeight);
//		numberCard.setPosition(ownCardWidth + ownCardWidth*2/3, -ownCardHeight/3);
//		stage.addActor(numberCard);
	}

	@Override
	public void render () {
		// render the stage
		Gdx.gl.glClearColor(1, 1,1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	public void createOwnHand(int numberOfCards, float cardWidth, float cardHeight) {
		for (int i = 0; i < numberOfCards; i++) {
			// same for card
			Texture texture = new Texture(Gdx.files.internal("Red_2.png"));
			Image image = new Image(texture);
			image.setSize(cardWidth, cardHeight);
			ownHand.add(image);
		}
	}

	public void createotherHands(int numberOfCards, float cardWidth, float cardHeight, List<Image> hand) {
		for (int i = 0; i < numberOfCards; i++) {
			// same for card
			Texture texture = new Texture(Gdx.files.internal("Deck.png"));
			Image image = new Image(texture);
			image.setSize(cardWidth, cardHeight);
			hand.add(image);
		}
	}

	// place the hand of the own player
	public void placeOwnCards(float screenWidth, float cardWidth, float cardHeight, float rot) {
	    // define the constants
        final float rotation = rot;   // rotation in degrees
        final float rotationOffset = MathUtils.sinDeg(rotation) * cardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * cardWidth;
		if(ownHand.size() > 8) {
		    offset = (screenWidth - cardWidth *3.0f) / ((float)ownHand.size() - 1.0f);
        }
		final float offsetGlobal = (((float)ownHand.size() * offset + offset) / 2.0f) - rotationOffset;
        float xPosCard;

        Gdx.app.log("Handwidth: ", String.valueOf(screenWidth));
        Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
        // calculate the x-pos of every card
		for(int i = ownHand.size(); i > 0; i--) {
			float index = (float)i;
			xPosCard = (screenWidth / 2.0f) + ((offset * index - cardWidth / 2.0f)) - offsetGlobal;

			ownHand.get(ownHand.size()-i).setPosition(xPosCard, -cardHeight/3.0f);
            ownHand.get(i-1).rotateBy(rotation);
			stage.addActor(ownHand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeTopCards(float screenWidth, float cardWidth, float cardHeight, float rot, List<Image> hand) {
		// define the constants
		final float rotation = rot + 180.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation) * cardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * cardWidth;
		if(hand.size() > 8) {
			offset = (screenWidth - cardWidth *6.0f) / ((float)hand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)hand.size() * offset + offset) / 2.0f) - rotationOffset;
		float xPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(screenWidth));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = hand.size(); i > 0; i--) {
			float index = (float)i;
			xPosCard = (screenWidth / 2.0f) + ((offset * index + cardWidth / 2.0f)) - offsetGlobal;

			hand.get(hand.size()-i).setPosition(xPosCard, Gdx.graphics.getHeight()+ cardHeight * 1.0f/3.0f);
			hand.get(i-1).rotateBy(rotation);
			stage.addActor(hand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeLeftCards(float screenHeight, float cardWidth, float cardHeight, float rot, List<Image> hand) {
		// define the constants
		final float rotation = rot + 270.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation) * cardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * cardWidth;
		if(hand.size() > 8) {
			offset = (screenHeight - cardWidth *6.0f) / ((float)hand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)hand.size() * offset + offset) / 2.0f) - rotationOffset;
		float yPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(screenHeight));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = hand.size(); i > 0; i--) {
			float index = (float)i;
			yPosCard = (screenHeight / 2.0f) + ((offset * index - cardWidth / 2.0f)) - offsetGlobal;

			hand.get(hand.size()-i).setPosition( -cardHeight/3.0f, yPosCard);
			hand.get(i-1).rotateBy(rotation);
			stage.addActor(hand.get(i - 1));
		}
	}
}
