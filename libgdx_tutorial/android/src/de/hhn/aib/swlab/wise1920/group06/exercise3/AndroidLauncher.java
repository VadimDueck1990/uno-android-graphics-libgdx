package de.hhn.aib.swlab.wise1920.group06.exercise3;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.hhn.aib.swlab.wise1920.group06.core.models.Card;
import de.hhn.aib.swlab.wise1920.group06.exercise3.activities.LobbyActivity;
import de.hhn.aib.swlab.wise1920.group06.exercise3.helper.PreferenceHelper;
import de.hhn.aib.swlab.wise1920.group06.exercise3.interfaces.MessageListener;
import de.hhn.aib.swlab.wise1920.group06.core.models.GameMessage;
import de.hhn.aib.swlab.wise1920.group06.core.models.PlayerImpl;
import de.hhn.aib.swlab.wise1920.group06.exercise3.services.WebSocketService;
import de.hhn.aib.swlab.wise1920.group06.core.MyUnoGame;
import de.hhn.aib.swlab.wise1920.group06.core.interfaces.UiCommunication;

public class AndroidLauncher extends AndroidApplication implements MessageListener, UiCommunication{

    static final Color ACTIVE = new Color(1.0f, 0.78f, 0.14f, 1);
    static final Color NEXT = new Color(1.0f, 0.78f, 0.14f, 0.25f);
    static final Color INACTIVE = new Color(1.0f, 0.78f, 0.14f, 0.05f);

    private PlayerImpl player1, player2, player3, player4;
    private List<PlayerImpl> allPlayerList;
    private Map<Integer, Integer> playerToLabelIndex;
    private PlayerImpl myPlayer;
    private PreferenceHelper preferenceHelper;
    private Gson gson;

    private WebSocketService webSocketService;
    boolean serviceBound = false;

    MyUnoGame unoGame;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        // initialize variables
        super.onCreate(savedInstanceState);

        this.allPlayerList = new ArrayList<>();
        this.playerToLabelIndex = new ConcurrentHashMap<>();
        this.gson = new Gson();

        preferenceHelper = new PreferenceHelper(AndroidLauncher.this);
        // intialize UI
        unoGame = new MyUnoGame(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.initialize(unoGame, config);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(AndroidLauncher.this)
                .setTitle("Abmelden")
                .setMessage("Bist du dir sicher, dass du das Spiel verlassen willst?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent(AndroidLauncher.this, LobbyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        //Send info to backend

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    @Override
    public void onMessageReceived(String message) {
        GameMessage gameMessage = gson.fromJson(message, GameMessage.class);

        switch (gameMessage.getAction()) {

            //If the game begins
            case SETUP_GAME:

                Log.i("Receive", "Setup_Game is called on GameActivity");
                setupGame(gameMessage);
                // startTimer();

                break;

            //If an other player place a card
            case PLACE_CARD:

                // otherPlayerPlacedCard(gameMessage);

                break;

            //If the card you placed is invalid
            case INVALID_CARD:
                // TODO: notify the user somehow
                break;

            case VALID_CARD:
                // isValidCard(gameMessage);
                break;

            //If you must draw min. one card
            case DRAW_CARD:
                // setMyHand(gameMessage);
                break;

            //If the time is over and its next players turn
            case NEXT_PLAYER:
                // resetTimer();
                setActiveAndNextPlayer(gameMessage);
                // setUpAfterOtherPlaceCard(gameMessage);
                // startTimer();
                break;

            //If an other player leave the game
            case PLAYER_LEFT_GAME:

                break;

            //If the Game is over / a player wins
            case GAME_OVER:
                break;


            case NOT_YOUR_TURN:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webSocketService.deregisterListener(AndroidLauncher.this);

        if (connection != null) {
            unbindService(connection);
        }

    }

    private void setupGame(GameMessage gameMessage) {


        for (PlayerImpl currentPlayer : gameMessage.getPlayerList()) {


            switch (currentPlayer.getPlayerIndex()) {
                case 1:
                    player1 = currentPlayer;
                    player1.setPlayerIndex(1);
                    player1.setHandCardsCount(currentPlayer.getHandCardsCount());
                    allPlayerList.add(player1);
                    break;
                case 2:
                    player2 = currentPlayer;
                    player2.setPlayerIndex(2);
                    player2.setHandCardsCount(currentPlayer.getHandCardsCount());
                    allPlayerList.add(player2);
                    break;
                case 3:
                    player3 = currentPlayer;
                    player3.setPlayerIndex(3);
                    player3.setHandCardsCount(currentPlayer.getHandCardsCount());
                    allPlayerList.add(player3);
                    break;
                case 4:
                    player4 = currentPlayer;
                    player4.setPlayerIndex(4);
                    player4.setHandCardsCount(currentPlayer.getHandCardsCount());
                    allPlayerList.add(player4);
                    break;
            }

            Log.i("HELP", preferenceHelper.readUsername());
            Log.i("HELP", currentPlayer.getUserName());

            if (currentPlayer.getUserName().equals(preferenceHelper.readUsername())) {
                myPlayer = currentPlayer;
                //allPlayerList.remove(currentPlayer);
            }
        }

        setOtherPlayerCardCount();
        //setActiveAndNextPlayer(gameMessage);
        setMyHand(gameMessage);
        // showLastDiscardedCard(gameMessage);

    }

    // set the right amount of cards to the other players hand
    private void setOtherPlayerCardCount() {

        int myPlayerIndex = myPlayer.getPlayerIndex();
        int otherPlayersCount = allPlayerList.size() - 1;

        switch (otherPlayersCount) {

            //One other players in the game
            case 1:

                Log.i("SET_OTHER_PLAYERS_CASE1", " " + String.valueOf(otherPlayersCount));
                switch (myPlayerIndex) {
                    case 1:
                        unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), player1.getUserName(), 0); // set the label
                        unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), player2.getUserName(), 2);

                        playerToLabelIndex.put(player1.getPlayerIndex(), 0); // save label and index
                        playerToLabelIndex.put(player2.getPlayerIndex(), 2);

                        unoGame.placeTopCards(player2.getHandCardsCount());
                        break;

                    case 2:
                        unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), player2.getUserName(), 0);
                        unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), player1.getUserName(), 2);

                        playerToLabelIndex.put(player2.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player1.getPlayerIndex(), 2);
                        unoGame.placeTopCards(player1.getHandCardsCount());
                        break;
                }
                break;

            //Two other players in the game
            case 2:
                Log.i("SET_OTHER_PLAYERS_CASE2", " " + String.valueOf(otherPlayersCount));
                switch (myPlayerIndex) {
                    case 1:
                        unoGame.setName(ACTIVE, player2.getUserName(), 1);
                        unoGame.placeRightCards(player2.getHandCardsCount());
                        unoGame.setName(ACTIVE, player3.getUserName(), 2);
                        unoGame.placeTopCards(player3.getHandCardsCount());

                        playerToLabelIndex.put(player1.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player2.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player3.getPlayerIndex(), 2);
                        break;

                    case 2:
                        unoGame.setName(ACTIVE, player3.getUserName(), 1);
                        unoGame.placeRightCards(player3.getHandCardsCount());
                        unoGame.setName(ACTIVE, player1.getUserName(), 2);
                        unoGame.placeTopCards(player1.getHandCardsCount());

                        playerToLabelIndex.put(player2.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player3.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player1.getPlayerIndex(), 2);
                        break;

                    case 3:
                        unoGame.setName(ACTIVE, player1.getUserName(), 1);
                        unoGame.placeRightCards(player1.getHandCardsCount());
                        unoGame.setName(ACTIVE, player2.getUserName(), 2);
                        unoGame.placeTopCards(player2.getHandCardsCount());

                        playerToLabelIndex.put(player3.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player1.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player2.getPlayerIndex(), 2);
                        break;
                }
                break;

            //Three other players in the game
            case 3:
                Log.i("SET_OTHER_PLAYERS_CASE3", " " + String.valueOf(otherPlayersCount));
                switch (myPlayerIndex) {
                    case 1:
                        unoGame.setName(ACTIVE, player2.getUserName(), 1);
                        unoGame.placeRightCards(player2.getHandCardsCount());
                        unoGame.setName(ACTIVE, player3.getUserName(), 2);
                        unoGame.placeTopCards(player3.getHandCardsCount());
                        unoGame.setName(ACTIVE, player4.getUserName(), 3);
                        unoGame.placeLeftCards(player4.getHandCardsCount());

                        playerToLabelIndex.put(player1.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player2.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player3.getPlayerIndex(), 2);
                        playerToLabelIndex.put(player4.getPlayerIndex(), 3);
                        break;

                    case 2:
                        unoGame.setName(ACTIVE, player3.getUserName(), 1);
                        unoGame.placeRightCards(player3.getHandCardsCount());
                        unoGame.setName(ACTIVE, player4.getUserName(), 2);
                        unoGame.placeTopCards(player4.getHandCardsCount());
                        unoGame.setName(ACTIVE, player1.getUserName(), 3);
                        unoGame.placeLeftCards(player1.getHandCardsCount());

                        playerToLabelIndex.put(player2.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player3.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player4.getPlayerIndex(), 2);
                        playerToLabelIndex.put(player1.getPlayerIndex(), 3);
                        break;

                    case 3:
                        unoGame.setName(ACTIVE, player4.getUserName(), 1);
                        unoGame.placeRightCards(player4.getHandCardsCount());
                        unoGame.setName(ACTIVE, player1.getUserName(), 2);
                        unoGame.placeTopCards(player1.getHandCardsCount());
                        unoGame.setName(ACTIVE, player2.getUserName(), 3);
                        unoGame.placeLeftCards(player2.getHandCardsCount());

                        playerToLabelIndex.put(player3.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player4.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player1.getPlayerIndex(), 2);
                        playerToLabelIndex.put(player2.getPlayerIndex(), 3);
                        break;

                    case 4:
                        unoGame.setName(ACTIVE, player1.getUserName(), 1);
                        unoGame.placeRightCards(player1.getHandCardsCount());
                        unoGame.setName(ACTIVE, player2.getUserName(), 2);
                        unoGame.placeTopCards(player2.getHandCardsCount());
                        unoGame.setName(ACTIVE, player3.getUserName(), 3);
                        unoGame.placeLeftCards(player3.getHandCardsCount());

                        playerToLabelIndex.put(player4.getPlayerIndex(), 0);
                        playerToLabelIndex.put(player1.getPlayerIndex(), 1);
                        playerToLabelIndex.put(player2.getPlayerIndex(), 2);
                        playerToLabelIndex.put(player3.getPlayerIndex(), 3);
                        break;
                }
                break;
        }
    }

    private void setActiveAndNextPlayer(final GameMessage gameMessage) {

        Log.i("SET_ACTIVE_NEXT_PLAYER", " NextPlayer: " + String.valueOf(gameMessage.getNextPlayerIndex()));
        Log.i("SET_ACTIVE_NEXT_PLAYER", " ActivePlayer: " + String.valueOf(gameMessage.getActivePlayerIndex()));

        for (PlayerImpl player : allPlayerList) {
            int position = playerToLabelIndex.get(player.getPlayerIndex());
            if (player.getPlayerIndex() == gameMessage.getActivePlayerIndex()) {
                unoGame.setName(ACTIVE, player.getUserName(), position);
            } else if (player.getPlayerIndex() == gameMessage.getNextPlayerIndex()) {
                unoGame.setName(NEXT, player.getUserName(), position);
            } else {
                unoGame.setName(INACTIVE, player.getUserName(), position);
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            webSocketService.registerListener(AndroidLauncher.this); //Setze diese Klasse als Listener fuer neue Nachrichten

            GameMessage gm = new GameMessage();
            //LobbyMessage lb = new LobbyMessage();
            gm.setAction(GameMessage.GameAction.SETUP_GAME);
            //lb.setAction(LobbyMessage.Command.SETUP_GAME);
            webSocketService.sendMessage(gm);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    //Sets card to my Hand
    private void setMyHand(final GameMessage gameMessage) {

        //unoGame.placeOwnCards(gameMessage.getHand());
        /*runOnUiThread(new Runnable() {

            @Override
            public void run() {

                cardList.clear();

                for (Card card : gameMessage.getHand()) {
                    cardList.add(new ExampleItem(card));
                }
                mAdapter.notifyDataSetChanged();

            }
        });*/
    }

    @Override
    public void initializeSetup() {
        // register with socket service

        Intent serviceIntent = new Intent(this, WebSocketService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        //unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), "Vadim", 0);
        //unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), "Andriy", 1);
        //unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), "Benjamin", 2);
        //unoGame.setName(new Color(1.0f, 0.78f, 0.14f, 1), "Simon", 3);
    }
}
