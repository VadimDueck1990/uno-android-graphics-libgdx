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
	// dummy numbers to test hand generation
	int ownNumber;
	int leftNumber;
	int topNumber;
	int rightNumber;

	private float discardAngle; // rotation angle of the discard cards

	static final float WIDTH_HEIGHT_RATIO = 1.44845f; // the width to height ratio
	static final float WIDTH_SCREEN_RATIO = 7.66667f; //  the card to screen ratio
	static final float OTHERS_SCREEN_RATIO = 11.0f; // the card to screen ratio of the other hands
	static final float CARD_ROTATION = -7.0f;

	private Stage stage;
	private List<Image> ownHand;
	private List<Image> leftHand;
	private List<Image> topHand;
	private List<Image> rightHand;
	private List<Image> deck;
	private List<Image> discardStack;

	// own and others card size
	float ownCardWidth; // actual card width
	float ownCardHeight;
	float foreignCardWidth; // actual card width
	float foreignCardHeight;
	// positions of deck and discardstack
	Vector2 deckPosition;
	Vector2 discardStackPosition;

	
	@Override
	public void create () {
		// create a stage and set it to full screen
		stage = new Stage(new ScreenViewport());

		// initialize dummy numbers to test hand generation
		ownNumber = 7;
		leftNumber = 7;
		topNumber = 7;
		rightNumber = 7;

		discardAngle = 0.0f;

		ownHand  = new ArrayList<>();
		leftHand = new ArrayList<>();
		topHand = new ArrayList<>();
		rightHand = new ArrayList<>();

		// own and others card size
		ownCardWidth = Gdx.graphics.getWidth() / WIDTH_SCREEN_RATIO; // actual card width
		ownCardHeight = ownCardWidth * WIDTH_HEIGHT_RATIO;
		foreignCardWidth = Gdx.graphics.getWidth() / OTHERS_SCREEN_RATIO; // actual card width
		foreignCardHeight = foreignCardWidth * WIDTH_HEIGHT_RATIO;
		// positions of deck and discardstack
		deckPosition = new Vector2();
		discardStackPosition = new Vector2();
		deckPosition.x = Gdx.graphics.getWidth() / 3 - ownCardWidth / 2.0f;
		deckPosition.y = Gdx.graphics.getHeight() / 2 - ownCardHeight / 2.0f;
		discardStackPosition.x = Gdx.graphics.getWidth()/3.0f*2.0f - ownCardWidth / 2.0f;
		discardStackPosition.y = Gdx.graphics.getHeight() / 2 - ownCardHeight / 2.0f;

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

		// add Deck
		Texture deckTexture = new Texture(Gdx.files.internal("Deck.png"));
		Image deck = new Image(deckTexture);
		deck.setSize(ownCardWidth, ownCardHeight);
		deck.setOrigin(ownCardWidth/2, ownCardHeight/2);
		deck.setPosition(deckPosition.x, deckPosition.y);
		stage.addActor(deck);

		addCardToDiscardStack("Blue_0.png");
		addCardToDiscardStack("Green_5.png");
		addCardToDiscardStack("Red_5.png");
		//addCardToDiscardStack("Blue_Draw.png");
		//addCardToDiscardStack("Yellow_Draw.png");
		// addCardToDiscardStack("Blue_Skip.png");

		placeOwnCards(Gdx.graphics.getWidth());
		placeTopCards(Gdx.graphics.getWidth(), topHand);
		placeLeftCards(Gdx.graphics.getHeight(), leftHand);
		placeRightCards(Gdx.graphics.getHeight(), rightHand);

		// same for card
		/*Texture wildTexture = new Texture(Gdx.files.internal("Wild_Draw.png"));
		Image wildCard = new Image(wildTexture);
		wildCard.setSize(ownCardWidth, ownCardHeight);
		wildCard.setPosition(Gdx.graphics.getWidth()/2, ownCardWidth * 4.0f);
		stage.addActor(wildCard);*/
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
	public void placeOwnCards(float screenWidth) {
	    // define the constants
        final float rotation = CARD_ROTATION;   // rotation in degrees
        final float rotationOffset = MathUtils.sinDeg(rotation) * ownCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * ownCardWidth;
		if(ownHand.size() > 8) {
		    offset = (screenWidth - ownCardWidth *3.0f) / ((float)ownHand.size() - 1.0f);
        }
		final float offsetGlobal = (((float)ownHand.size() * offset + offset) / 2.0f) - rotationOffset;
        float xPosCard;

        Gdx.app.log("Handwidth: ", String.valueOf(screenWidth));
        Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
        // calculate the x-pos of every card
		for(int i = ownHand.size(); i > 0; i--) {
			float index = (float)i;
			xPosCard = (screenWidth / 2.0f) + ((offset * index - ownCardWidth / 2.0f)) - offsetGlobal;

			ownHand.get(ownHand.size()-i).setPosition(xPosCard, -ownCardHeight/3.0f);
            ownHand.get(i-1).rotateBy(rotation);
			stage.addActor(ownHand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeTopCards(float screenWidth, List<Image> hand) {
		// define the constants
		final float rotation = CARD_ROTATION + 180.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation) * foreignCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * foreignCardWidth;
		if(hand.size() > 8) {
			offset = (screenWidth - foreignCardWidth *6.0f) / ((float)hand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)hand.size() * offset + offset) / 2.0f) - rotationOffset;
		float xPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(screenWidth));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = hand.size(); i > 0; i--) {
			float index = (float)i;
			xPosCard = (screenWidth / 2.0f) + ((offset * index + foreignCardWidth / 2.0f)) - offsetGlobal;

			hand.get(hand.size()-i).setPosition(xPosCard, Gdx.graphics.getHeight()+ foreignCardHeight * 1.0f/3.0f);
			hand.get(i-1).rotateBy(rotation);
			stage.addActor(hand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeLeftCards(float screenHeight, List<Image> hand) {
		// define the constants
		final float rotation = CARD_ROTATION + 270.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation-270.0f) * foreignCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * foreignCardWidth;
		if(hand.size() > 8) {
			offset = (screenHeight - foreignCardWidth *2.0f) / ((float)hand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)hand.size() * offset + offset) / 2.0f) + rotationOffset - foreignCardWidth;
		float yPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(screenHeight));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = hand.size(); i > 0; i--) {
			float index = (float)i;
			yPosCard = (screenHeight / 2.0f) + ((offset * index - foreignCardWidth / 2.0f)) - offsetGlobal;

			hand.get(hand.size()-i).setPosition( -foreignCardHeight/3.0f, yPosCard);
			hand.get(i-1).rotateBy(rotation);
			stage.addActor(hand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeRightCards(float screenHeight, List<Image> hand) {
		// define the constants
		final float rotation = CARD_ROTATION + 90.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation-90.0f) * foreignCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * foreignCardWidth;
		if(hand.size() > 8) {
			offset = (screenHeight - foreignCardWidth *2.0f) / ((float)hand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)hand.size() * offset + offset) / 2.0f);
		float yPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(screenHeight));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = hand.size(); i > 0; i--) {
			float index = (float)i;
			yPosCard = (screenHeight / 2.0f) + ((offset * index - foreignCardWidth / 2.0f)) - offsetGlobal;

			hand.get(hand.size()-i).setPosition( Gdx.graphics.getWidth()+foreignCardHeight/3.0f, yPosCard);
			hand.get(i-1).rotateBy(rotation);
			stage.addActor(hand.get(i - 1));
		}
	}

	public void addCardToDiscardStack(String imagePath) {
		Texture discardTexture = new Texture(Gdx.files.internal(imagePath));
		Image discard = new Image(discardTexture);
		discard.setSize(ownCardWidth, ownCardHeight);
		discard.setOrigin(ownCardWidth/2, ownCardHeight/2);
		discard.setPosition(discardStackPosition.x, discardStackPosition.y);
		discard.rotateBy(discardAngle);
		discardAngle -= 40.0f;
		stage.addActor(discard);
	}
}
