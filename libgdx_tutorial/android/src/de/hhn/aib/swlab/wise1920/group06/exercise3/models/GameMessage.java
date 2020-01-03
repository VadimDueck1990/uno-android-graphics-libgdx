package de.hhn.aib.swlab.wise1920.group06.exercise3.models;


import java.util.List;

public class GameMessage {

    private GameAction action;
    private int activePlayerIndex;
    private int nextPlayerIndex;
    private List<Card> hand;
    private Card lastDiscardedCard;
    private Card placedCard;
    private List<Card> drawCardList;
    //private LocalDateTime timeReceived;
    private List<PlayerImpl> playerList;
    private Card.Color wildCardColor;
    private boolean unoPressed;


    public enum GameAction {
        SETUP_GAME, PLACE_CARD, INVALID_CARD, VALID_CARD, DRAW_CARD, YOUR_TURN, NEXT_PLAYER, GAME_OVER, UNO, PLAYER_LEFT_GAME, NOT_YOUR_TURN
    }

    public GameAction getAction() {
        return action;
    }

    public void setAction(GameAction action) {
        this.action = action;
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }

    public int getNextPlayerIndex() {
        return nextPlayerIndex;
    }

    public void setNextPlayerIndex(int nextPlayerIndex) {
        this.nextPlayerIndex = nextPlayerIndex;
    }


    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public Card getLastDiscardedCard() {
        return lastDiscardedCard;
    }

    public void setLastDiscardedCard(Card lastDiscardedCard) {
        this.lastDiscardedCard = lastDiscardedCard;
    }

    public Card getPlacedCard() {
        return placedCard;
    }

    public void setPlacedCard(Card placedCard) {
        this.placedCard = placedCard;
    }

    public List<Card> getDrawCardList() {
        return drawCardList;
    }

    public void setDrawCardList(List<Card> drawCardList) {
        this.drawCardList = drawCardList;
    }

    public List<PlayerImpl> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerImpl> playerList) {
        this.playerList = playerList;
    }

    public Card.Color getWildCardColor() {
        return wildCardColor;
    }

    public void setWildCardColor(Card.Color wildCardColor) {
        this.wildCardColor = wildCardColor;
    }

    public boolean isUnoPressed() {
        return unoPressed;
    }

    public void setUnoPressed(boolean unoPressed) {
        this.unoPressed = unoPressed;
    }
}
