package de.hhn.aib.swlab.wise1920.group06.core.models;

import java.util.List;

public class LobbyMessage {

    private Command action;
    private PlayerImpl player;
    private String authentication;
    private List<PlayerImpl> playerList;

    public enum Command {
        LOGIN, SERVER_FULL, PLAYER_JOINED, PLAYER_LEFT, READY, NOT_READY, OTHER_PLAYER, START_GAME, SUCCESS, GAME_RUNNING, PLAYER_EXIST, SETUP_GAME

    }


    public Command getAction() {
        return action;
    }

    public void setAction(Command action) {
        this.action = action;
    }

    public PlayerImpl getPlayer() {
        return player;
    }

    public void setPlayer(PlayerImpl player) {
        this.player = player;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public List<PlayerImpl> getPlayerList() {
        return playerList;
    }
}
