package de.hhn.aib.swlab.wise1920.group06.core.models;


public class PlayerImpl {

    private String userName;
    private int playerIndex;
    private LobbyMessage.Command status;
    private String token;
    private int handCardsCount;
    private int rank;

    public String getUserName() {
        return userName;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public LobbyMessage.Command getStatus() {
        return status;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setStatus(LobbyMessage.Command status) {
        this.status = status;
    }

    public int getHandCardsCount() {
        return handCardsCount;
    }

    public void setHandCardsCount(int handCardsCount) {
        this.handCardsCount = handCardsCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
