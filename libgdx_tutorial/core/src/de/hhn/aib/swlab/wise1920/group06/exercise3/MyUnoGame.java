package de.hhn.aib.swlab.wise1920.group06.exercise3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;


public class MyUnoGame extends ApplicationAdapter {
	// dummy numbers to test hand generation
	int ownNumber;
	int rightNumber, topNumber, leftNumber;

	Label ownLabel, rightLabel, topLabel, leftLabel;

	private float discardAngle; // rotation angle of the discard cards

	float WIDTH_HEIGHT_RATIO; // the width to height ratio
	float WIDTH_SCREEN_RATIO; //  the card to screen ratio
	float OTHERS_SCREEN_RATIO; // the card to screen ratio of the other hands
	float CARD_ROTATION;

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
		leftNumber = 6;
		topNumber = 5;
		rightNumber = 4;

		discardAngle = 0.0f;

		setPositions();

		ownHand  = new ArrayList<>();
		leftHand = new ArrayList<>();
		topHand = new ArrayList<>();
		rightHand = new ArrayList<>();

		// fill own hand with dummy cards
		createOwnHand(ownNumber);

		// load table texture and create an Image
		Texture tableTexture = new Texture(Gdx.files.internal("Table_1.png"));
		Image table = new Image(tableTexture);
		table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		table.setPosition(0,0);
		stage.addActor(table);

		// debug
		setName(new Color(1.0f, 0.78f, 0.14f, 1), "Vadim", 0);
		setName(new Color(1.0f, 0.78f, 0.14f, 1), "Andriy", 1);
		setName(new Color(1.0f, 0.78f, 0.14f, 1), "Benjamin", 2);
		setName(new Color(1.0f, 0.78f, 0.14f, 1), "Simon", 3);


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

		placeOwnCards();
		placeTopCards(topNumber);
		placeLeftCards(leftNumber);
		placeRightCards(rightNumber);
	}

	@Override
	public void render () {
		// render the stage
		Gdx.gl.glClearColor(1, 1,1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resume () {
		setPositions();
	}

	public void createOwnHand(int numberOfCards) {
		for (int i = 0; i < numberOfCards; i++) {
			// same for card
			Texture texture = new Texture(Gdx.files.internal("Red_2.png"));
			Image image = new Image(texture);
			image.setSize(ownCardWidth, ownCardHeight);
			ownHand.add(image);
		}
	}

	// place the hand of the own player
	public void placeOwnCards() {
	    // define the constants
        final float rotation = CARD_ROTATION;   // rotation in degrees
        final float rotationOffset = MathUtils.sinDeg(rotation) * ownCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * ownCardWidth;
		if(ownHand.size() > 8) {
		    offset = (Gdx.graphics.getWidth() - ownCardWidth *3.0f) / ((float)ownHand.size() - 1.0f);
        }
		final float offsetGlobal = (((float)ownHand.size() * offset + offset) / 2.0f) - rotationOffset;
        float xPosCard;

        Gdx.app.log("Handwidth: ", String.valueOf(Gdx.graphics.getWidth()));
        Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
        // calculate the x-pos of every card
		for(int i = ownHand.size(); i > 0; i--) {
			float index = (float)i;
			xPosCard = (Gdx.graphics.getWidth() / 2.0f) + ((offset * index - ownCardWidth / 2.0f)) - offsetGlobal;

			ownHand.get(ownHand.size()-i).setPosition(xPosCard, -ownCardHeight/3.0f);
            ownHand.get(i-1).rotateBy(rotation);
			stage.addActor(ownHand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeTopCards(int numberOfCards) {
		// create List of images
		for (int i = 0; i < numberOfCards; i++) {
			Texture texture = new Texture(Gdx.files.internal("Deck.png"));
			Image image = new Image(texture);
			image.setSize(foreignCardWidth, foreignCardHeight);
			topHand.add(image);
		}

		// define the constants
		final float rotation = CARD_ROTATION + 180.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation) * foreignCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * foreignCardWidth;
		if(topHand.size() > 8) {
			offset = (Gdx.graphics.getWidth() - foreignCardWidth *6.0f) / ((float)topHand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)topHand.size() * offset + offset) / 2.0f) - rotationOffset;
		float xPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(Gdx.graphics.getWidth()));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every image
		for(int i = topHand.size(); i > 0; i--) {
			float index = (float)i;
			xPosCard = (Gdx.graphics.getWidth() / 2.0f) + ((offset * index + foreignCardWidth / 2.0f)) - offsetGlobal;

			topHand.get(topHand.size()-i).setPosition(xPosCard, Gdx.graphics.getHeight()+ foreignCardHeight * 1.0f/3.0f);
			topHand.get(i-1).rotateBy(rotation);
			stage.addActor(topHand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeLeftCards(int numberOfCards) {
		// create List of images
		for (int i = 0; i < numberOfCards; i++) {
			Texture texture = new Texture(Gdx.files.internal("Deck.png"));
			Image image = new Image(texture);
			image.setSize(foreignCardWidth, foreignCardHeight);
			leftHand.add(image);
		}
		// define the constants
		final float rotation = CARD_ROTATION + 270.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation-270.0f) * foreignCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * foreignCardWidth;
		if(leftHand.size() > 8) {
			offset = (Gdx.graphics.getHeight() - foreignCardWidth *2.0f) / ((float)leftHand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)leftHand.size() * offset + offset) / 2.0f) + rotationOffset - foreignCardWidth;
		float yPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(Gdx.graphics.getHeight()));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = leftHand.size(); i > 0; i--) {
			float index = (float)i;
			yPosCard = (Gdx.graphics.getHeight() / 2.0f) + ((offset * index - foreignCardWidth / 2.0f)) - offsetGlobal;

			leftHand.get(leftHand.size()-i).setPosition( -foreignCardHeight/3.0f, yPosCard);
			leftHand.get(i-1).rotateBy(rotation);
			stage.addActor(leftHand.get(i - 1));
		}
	}

	// place the hand of the own player
	public void placeRightCards(int numberOfCards) {
		// create List of images
		for (int i = 0; i < numberOfCards; i++) {
			Texture texture = new Texture(Gdx.files.internal("Deck.png"));
			Image image = new Image(texture);
			image.setSize(foreignCardWidth, foreignCardHeight);
			rightHand.add(image);
		}
		final float rotation = CARD_ROTATION + 90.0f;   // rotation in degrees
		final float rotationOffset = MathUtils.sinDeg(rotation-90.0f) * foreignCardHeight;     // offset to show the edge of the card on the left
		float offset = -2.0f/3.0f * foreignCardWidth;
		if(rightHand.size() > 8) {
			offset = (Gdx.graphics.getHeight() - foreignCardWidth *2.0f) / ((float)rightHand.size() - 1.0f);
		}
		final float offsetGlobal = (((float)rightHand.size() * offset + offset) / 2.0f);
		float yPosCard;

		Gdx.app.log("Handwidth: ", String.valueOf(Gdx.graphics.getHeight()));
		Gdx.app.log("Handwidth: ", String.valueOf(offsetGlobal * 2.0f));
		// calculate the x-pos of every card
		for(int i = rightHand.size(); i > 0; i--) {
			float index = (float)i;
			yPosCard = (Gdx.graphics.getHeight() / 2.0f) + ((offset * index - foreignCardWidth / 2.0f)) - offsetGlobal;

			rightHand.get(rightHand.size()-i).setPosition( Gdx.graphics.getWidth()+foreignCardHeight/3.0f, yPosCard);
			rightHand.get(i-1).rotateBy(rotation);
			stage.addActor(rightHand.get(i - 1));
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

    // used to set the player names
    public void setName(Color color, String name, int position) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Saiyan-Sans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Gdx.graphics.getHeight()/12;
        parameter.borderWidth = 1;
        parameter.color = color;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = Color.DARK_GRAY;
        BitmapFont font24 = generator.generateFont(parameter); // font size 24 pixels
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font24;

        switch (position) {
			case 0:
				ownLabel = new Label(name,labelStyle);
				ownLabel.setPosition(0, 0);
				stage.addActor(ownLabel);
				break;
			case 1:
				rightLabel = new Label(name,labelStyle);
				rightLabel.setPosition(Gdx.graphics.getWidth() - rightLabel.getWidth()
								, Gdx.graphics.getHeight() - rightLabel.getHeight());
				stage.addActor(rightLabel);
				break;
			case 2:
				topLabel = new Label(name,labelStyle);
				topLabel.setPosition(Gdx.graphics.getWidth()/2 - topLabel.getWidth()/2
						, Gdx.graphics.getHeight() - foreignCardHeight/3.0f*2.0f - topLabel.getHeight());
				stage.addActor(topLabel);
				break;
			case 3:
				leftLabel = new Label(name, labelStyle);
				leftLabel.setPosition(0
						, Gdx.graphics.getHeight() - leftLabel.getHeight());
				stage.addActor(leftLabel);
				break;
		}
    }

    // initialize the used variables
    private void setPositions() {

		WIDTH_HEIGHT_RATIO = 1.44845f; // the width to height ratio
		WIDTH_SCREEN_RATIO = 7.66667f; //  the card to screen ratio
		OTHERS_SCREEN_RATIO = 11.0f; // the card to screen ratio of the other hands
		CARD_ROTATION = -7.0f;
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
	}
}
