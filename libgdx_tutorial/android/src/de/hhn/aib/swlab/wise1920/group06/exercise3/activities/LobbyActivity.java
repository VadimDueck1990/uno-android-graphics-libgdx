package de.hhn.aib.swlab.wise1920.group06.exercise3.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group06.exercise3.AndroidLauncher;
import de.hhn.aib.swlab.wise1920.group06.exercise3.R;
import de.hhn.aib.swlab.wise1920.group06.exercise3.helper.PreferenceHelper;
import de.hhn.aib.swlab.wise1920.group06.exercise3.interfaces.MessageListener;
import de.hhn.aib.swlab.wise1920.group06.core.models.LobbyMessage;
import de.hhn.aib.swlab.wise1920.group06.core.models.PlayerImpl;
import de.hhn.aib.swlab.wise1920.group06.exercise3.services.WebSocketService;

public class LobbyActivity extends AppCompatActivity implements MessageListener {

    private TextView myPlayerName, player2Name, player3Name, player4Name;
    private TextView myPlayerStatus,player2Status, player3Status, player4Status;
    private Button myPlayerButton;
    private PreferenceHelper preferenceHelper;
    private boolean isReadyButtonClicked;

    private WebSocketService webSocketService;
    boolean serviceBound = false;
    private Gson gson;
    private PlayerImpl player1;
    private PlayerImpl player2;
    private PlayerImpl player3;
    private PlayerImpl player4;
    private List<PlayerImpl> otherPlayerList = new ArrayList<>();

    //DEBUG
    private Button debug_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        myPlayerName = findViewById(R.id.player1Name);
        player2Name = findViewById(R.id.player2Name);
        player3Name = findViewById(R.id.player3Name);
        player4Name = findViewById(R.id.player4Name);

        myPlayerButton = findViewById(R.id.player1Button);
        myPlayerStatus = findViewById(R.id.player1Status);
        player2Status = findViewById(R.id.player2Status);
        player3Status = findViewById(R.id.player3Status);
        player4Status = findViewById(R.id.player4Status);

        preferenceHelper = new PreferenceHelper(LobbyActivity.this);

        player1 = new PlayerImpl();
        player1.setUserName(preferenceHelper.readUsername());
        player1.setPlayerIndex(1);
        myPlayerName.setText(preferenceHelper.readUsername());
        myPlayerStatus.setText(R.string.lobbyMyPlayerStatusNotReady);
        myPlayerButton.setText(R.string.lobbyMyPlayerButtonNotReady);

        //Initialize other playerUI
        player2Name.setText(R.string.lobbyNoPlayer);
        player2Status.setText(R.string.lobbyNoPlayer);
        player3Name.setText(R.string.lobbyNoPlayer);
        player3Status.setText(R.string.lobbyNoPlayer);
        player4Name.setText(R.string.lobbyNoPlayer);
        player4Status.setText(R.string.lobbyNoPlayer);

        isReadyButtonClicked = false;

        gson = new Gson();

        //DEBUG///////////////////

        //debug_start = findViewById(R.id.debug_start);
        //setOtherPlayersToLobby();

/*        debug_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });*/
        //////////////////////////////////

        //Initialize webSocketService
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        bindService(serviceIntent,connection, Context.BIND_AUTO_CREATE);

        myPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isReadyButtonClicked) {

                    LobbyMessage lm = new LobbyMessage();
                    lm.setAuthentication(preferenceHelper.readAuthToken());
                    lm.setAction(LobbyMessage.Command.NOT_READY);
                    webSocketService.sendMessage(lm); //Sende neue Nachricht an Backend ueber Websocket Verbindung

                    myPlayerStatus.setText(R.string.lobbyMyPlayerStatusNotReady);
                    myPlayerButton.setText(R.string.lobbyMyPlayerButtonNotReady);

                    isReadyButtonClicked = false;
                }
                else{

                    LobbyMessage lm = new LobbyMessage();
                    lm.setAuthentication(preferenceHelper.readAuthToken());
                    lm.setAction(LobbyMessage.Command.READY);
                    webSocketService.sendMessage(lm); //Sende neue Nachricht an Backend ueber Websocket Verbindung

                    myPlayerStatus.setText(R.string.lobbyMyPlayerStatusReady);
                    myPlayerButton.setText(R.string.lobbyMyPlayerButtonReady);

                    isReadyButtonClicked = true;

                }

                //Send info to backend
            }
        });

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LobbyActivity.this)
                .setTitle("Abmelden")
                .setMessage("Bist du dir sicher, dass du die Lobby verlassen willst?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        //Disconnect from webSocketService
                        webSocketService.deregisterListener(LobbyActivity.this);
                        if (connection != null) {
                            unbindService(connection);
                        }

                        //Open LoginActivity
                        Intent intent = new Intent(LobbyActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void setNewPlayerToLobby(PlayerImpl player) {

        if(player2 == null){
            player2 = player;
            player2.setPlayerIndex(2);
            otherPlayerList.add(player2);
        }
        else if(player3 == null){
            player3 = player;
            player3.setPlayerIndex(3);
            otherPlayerList.add(player3);
        }
        else if (player4 == null){
            player4 = player;
            player4.setPlayerIndex(4);
            otherPlayerList.add(player4);
        }

        setPlayerNameAndStatus();
    }

    private void setPlayerNameAndStatus(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI

                int otherPlayersCount = otherPlayerList.size();
                switch (otherPlayersCount) {

                    case 0: {
                        player2Name.setText(R.string.lobbyNoPlayer);
                        player2Status.setText(R.string.lobbyNoPlayer);
                        player3Name.setText(R.string.lobbyNoPlayer);
                        player3Status.setText(R.string.lobbyNoPlayer);
                        player4Name.setText(R.string.lobbyNoPlayer);
                        player4Status.setText(R.string.lobbyNoPlayer);
                        break;
                    }
                    case 1: {
                        player2Name.setText(player2.getUserName());
                        player2Status.setText(setPlayerStatus(player2));
                        player3Name.setText(R.string.lobbyNoPlayer);
                        player3Status.setText(R.string.lobbyNoPlayer);
                        player4Name.setText(R.string.lobbyNoPlayer);
                        player4Status.setText(R.string.lobbyNoPlayer);
                        break;
                    }
                    case 2: {
                        player2Name.setText(player2.getUserName());
                        player2Status.setText(setPlayerStatus(player2));
                        player3Name.setText(player3.getUserName());
                        player3Status.setText(setPlayerStatus(player3));
                        player4Name.setText(R.string.lobbyNoPlayer);
                        player4Status.setText(R.string.lobbyNoPlayer);
                        break;
                    }
                    case 3: {
                        player2Name.setText(player2.getUserName());
                        player2Status.setText(setPlayerStatus(player2));
                        player3Name.setText(player3.getUserName());
                        player3Status.setText(setPlayerStatus(player3));
                        player4Name.setText(player4.getUserName());
                        player4Status.setText(setPlayerStatus(player4));
                        break;
                    }
                }

            }
        });
    }

    private String setPlayerStatus(PlayerImpl pStatus){


        if(pStatus.getStatus() == LobbyMessage.Command.READY){
            return getString(R.string.lobbyMyPlayerStatusReady);
        }
        else {
            return getString(R.string.lobbyMyPlayerStatusNotReady);
        }
    }

    private void changePlayerStatus(final PlayerImpl player){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

        for (PlayerImpl currentPlayer: otherPlayerList) {
            if(currentPlayer.getUserName().equals(player.getUserName())){
                int playerIndex = currentPlayer.getPlayerIndex();

                switch (playerIndex){
                    case 2:
                        player2Status.setText(setPlayerStatus(player));
                        break;
                    case 3:
                        player3Status.setText(setPlayerStatus(player));
                        break;
                    case 4:
                        player4Status.setText(setPlayerStatus(player));
                        break;
                }

            }
        }

            }
        });
    }

    private void removePlayer(final PlayerImpl player){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

        for (PlayerImpl currentPlayer: otherPlayerList) {
            if (currentPlayer.getUserName().equals(player.getUserName())) {
                otherPlayerList.remove(currentPlayer);
                int playerIndex = currentPlayer.getPlayerIndex();

                switch (playerIndex){
                    case 2:
                        player2Name.setText(R.string.lobbyNoPlayer);
                        player2Status.setText(R.string.lobbyNoPlayer);
                        player2 = null;
                        break;
                    case 3:
                        player3Name.setText(R.string.lobbyNoPlayer);
                        player3Status.setText(R.string.lobbyNoPlayer);
                        player3 = null;
                        break;
                    case 4:
                        player4Name.setText(R.string.lobbyNoPlayer);
                        player4Status.setText(R.string.lobbyNoPlayer);
                        player4 = null;
                        break;
                }
            }
        }

            }
        });
    }

    private void checkIfOtherPlayersAreInTheLobby(LobbyMessage message){

        for (PlayerImpl player: message.getPlayerList()) {
                setNewPlayerToLobby(player);
        }
    }


    private void startGame(){
        Intent intent = new Intent(LobbyActivity.this, AndroidLauncher.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }




    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            webSocketService.registerListener(LobbyActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten

            LobbyMessage gm = new LobbyMessage();
            gm.setAction(LobbyMessage.Command.OTHER_PLAYER);
            webSocketService.sendMessage(gm);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    @Override
    public void onMessageReceived(String message) {

            LobbyMessage lobbyMessage = gson.fromJson(message, LobbyMessage.class);

            switch (lobbyMessage.getAction()) {

                //If a other player joined the lobby
                case PLAYER_JOINED:
                    setNewPlayerToLobby(lobbyMessage.getPlayer());
                    break;

                //If a other player leave the lobby
                case PLAYER_LEFT:
                    removePlayer(lobbyMessage.getPlayer());
                    break;

                //If I join the lobby and other player are in the lobby
                case OTHER_PLAYER:
                    checkIfOtherPlayersAreInTheLobby(lobbyMessage);
                    break;

                //If a other player in the lobby click on ready
                case READY:
                    changePlayerStatus(lobbyMessage.getPlayer());
                    break;

                //If a other player in the lobby click on not ready
                case NOT_READY:
                    changePlayerStatus(lobbyMessage.getPlayer());
                    break;

                //All players in the lobby are ready
                case START_GAME:
                    startGame();
                    break;
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webSocketService.deregisterListener(LobbyActivity.this);

        if (connection != null) {
            unbindService(connection);
        }

    }
}
