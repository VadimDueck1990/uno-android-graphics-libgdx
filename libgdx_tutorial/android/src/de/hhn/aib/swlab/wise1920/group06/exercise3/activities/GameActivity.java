package de.hhn.aib.swlab.wise1920.group06.exercise3.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hhn.aib.swlab.wise1920.group06.exercise3.ExampleAdapter;
import de.hhn.aib.swlab.wise1920.group06.exercise3.ExampleItem;
import de.hhn.aib.swlab.wise1920.group06.exercise3.R;
import de.hhn.aib.swlab.wise1920.group06.exercise3.helper.PreferenceHelper;
import de.hhn.aib.swlab.wise1920.group06.exercise3.interfaces.MessageListener;
import de.hhn.aib.swlab.wise1920.group06.exercise3.models.Card;
import de.hhn.aib.swlab.wise1920.group06.exercise3.models.GameMessage;
import de.hhn.aib.swlab.wise1920.group06.exercise3.models.PlayerImpl;
import de.hhn.aib.swlab.wise1920.group06.exercise3.services.WebSocketService;

public class GameActivity extends AppCompatActivity implements MessageListener {

    private TextView otherplayerCount1, otherplayerCount2, otherplayerCount3;
    private TextView playername1, playername2, playername3;
    private TextView activePlayer;
    private TextView nextPlayer;
    private TextView currentStackCard;
    private TextView putCardText;
    private ImageView colorImage;
    //private TextView myCards;

    private RecyclerView myCards;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button unoButton;
    private Button putCardButton;
    private PlayerImpl player1, player2, player3, player4;
    private PlayerImpl myPlayer;
    private PreferenceHelper preferenceHelper;
    private List<PlayerImpl> allPlayerList = new ArrayList<>();
    private Card chosenCard;
    private String[] listItems;
    private Card.Color wildColor = null;

    private boolean unoPressed;

    private CountDownTimer mCountDownTimer;
    private TextView mTextViewCountDown;
    private boolean mTimerRunning;

    private static final long START_TIME_IN_MILLIS = 15000;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    ArrayList<ExampleItem> cardList = new ArrayList<>();

    //DEBUG
    private GameMessage testGM;
    private ArrayList<Card> drawnCards = new ArrayList<>();
    private ArrayList<PlayerImpl> playerL = new ArrayList<>();
    private HashMap<Integer, Integer> cardsProPlayer = new HashMap<>();
    ////

    private WebSocketService webSocketService;
    boolean serviceBound = false;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        otherplayerCount1 = findViewById(R.id.player2CardCount);
        otherplayerCount2 = findViewById(R.id.player3CardCount);
        otherplayerCount3 = findViewById(R.id.player4CardCount);
        playername1 = findViewById(R.id.otherPlayerName1);
        playername2 = findViewById(R.id.otherPlayerName2);
        playername3 = findViewById(R.id.otherPlayerName3);

        activePlayer = findViewById(R.id.activePlayer);
        nextPlayer = findViewById(R.id.nextPlayer);
        currentStackCard = findViewById(R.id.StackCard);
        putCardText = findViewById(R.id.putCardText);
        colorImage = findViewById(R.id.cardColorImage);
        mTextViewCountDown = findViewById(R.id.timer);

        unoButton = findViewById(R.id.UnoButton);
        putCardButton = findViewById(R.id.putCardButton);

        unoPressed = false;

        gson = new Gson();
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        preferenceHelper = new PreferenceHelper(GameActivity.this);

        buildRecyclerView();

        putCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(chosenCard!=null) {
                    if (!chosenCard.isWildCard()) {
                        placeCard();
                    } else {
                        choseWildCardColor();
                    }
                }

            }
        });

        unoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Macht keinen Sinn es im Player zu speichern!!!!
                unoPressed = true;
            }
        });


        //DEBGUG

       /* testGM = new GameMessage();
        PlayerImpl p1 = new PlayerImpl();
        p1.setUserName("Spieler1");
        p1.setPlayerIndex(2);
        p1.setHandCardsCount(5);
        PlayerImpl p2 = new PlayerImpl();
        p2.setUserName("pp");
        p2.setPlayerIndex(3);
        p2.setHandCardsCount(7);
        PlayerImpl p3 = new PlayerImpl();
        p3.setUserName("Spieler2");
        p3.setPlayerIndex(1);
        p3.setHandCardsCount(7);
        PlayerImpl p4 = new PlayerImpl();
        p4.setUserName("Player4");
        p4.setPlayerIndex(4);
        p4.setHandCardsCount(80);
        playerL.add(p1);
        playerL.add(p2);
        playerL.add(p3);
        playerL.add(p4);
        testGM.setNextPlayerIndex(2);
        testGM.setAction(GameMessage.GameAction.SETUP_GAME);
        testGM.setActivePlayerIndex(1);
        testGM.setCardsProPlayer(cardsProPlayer);
        testGM.setLastDiscardedCard(new Card(Card.Color.BLUE,Card.Value.FOUR));
        testGM.setUnoPressed(false);
        testGM.setDrawCardList(drawnCards);
        ArrayList<Card> handCards = new ArrayList<>();
        handCards.add(new Card(Card.Color.BLACK, Card.Value.DRAWFOUR));
        handCards.add(new Card(Card.Color.BLUE, Card.Value.NINE));
        handCards.add(new Card(Card.Color.YELLOW, Card.Value.FIVE));
        handCards.add(new Card(Card.Color.RED, Card.Value.SIX));
        testGM.setHand(handCards);
        testGM.setPlayerList(playerL);

        setupGame(testGM);
*/
    }

    public void buildRecyclerView() {
        myCards = findViewById(R.id.cardHandList);
        myCards.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(cardList);

        myCards.setLayoutManager(mLayoutManager);
        myCards.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                setChosenCard(position);
            }
        });
    }


    private void choseWildCardColor() {
        listItems = getResources().getStringArray(R.array.WildColors);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GameActivity.this);
        mBuilder.setTitle("Wähle eine Farbe");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //mResult.setText(listItems[i]);
                if (listItems[i].equals("RED")) {
                    wildColor = Card.Color.RED;
                }
                if (listItems[i].equals("BLUE")) {
                    wildColor = Card.Color.BLUE;
                }
                if (listItems[i].equals("YELLOW")) {
                    wildColor = Card.Color.YELLOW;
                }
                if (listItems[i].equals("GREEN")) {
                    wildColor = Card.Color.GREEN;
                }
            }
        });

        // Set the a;ert dialog positive button
        mBuilder.setPositiveButton("Legen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GameMessage gm = new GameMessage();
                gm.setAction(GameMessage.GameAction.PLACE_CARD);
                gm.setPlacedCard(chosenCard);
                gm.setWildCardColor(wildColor);
                webSocketService.sendMessage(gm);

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    private void setChosenCard(int position) {

        chosenCard = cardList.get(position).getText1();
        putCardText.setText(chosenCard.getColor().toString() + " " + chosenCard.getValue().toString());
    }

    private void placeCard() {

        if (chosenCard != null) {
            GameMessage gm = new GameMessage();
            gm.setAction(GameMessage.GameAction.PLACE_CARD);
            gm.setPlacedCard(chosenCard);
            gm.setUnoPressed(unoPressed);
            webSocketService.sendMessage(gm);

            //remove chosen card from ui after placing
            chosenCard = null;
            putCardText.setText("");
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(GameActivity.this)
                .setTitle("Abmelden")
                .setMessage("Bist du dir sicher, dass du das Spiel verlassen willst?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent(GameActivity.this, LobbyActivity.class);
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


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            webSocketService.registerListener(GameActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten

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
        setActiveAndNextPlayer(gameMessage);
        setMyHand(gameMessage);
        showLastDiscardedCard(gameMessage);

    }


    private void setActiveAndNextPlayer(final GameMessage gameMessage) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Log.i("SET_ACTIVE_NEXT_PLAYER", " NextPlayer: " + String.valueOf(gameMessage.getNextPlayerIndex()));
                Log.i("SET_ACTIVE_NEXT_PLAYER", " ActivePlayer: " + String.valueOf(gameMessage.getActivePlayerIndex()));

                for (PlayerImpl player : allPlayerList) {
                    if (player.getPlayerIndex() == gameMessage.getActivePlayerIndex()) {
                        if (myPlayer.getPlayerIndex() == gameMessage.getActivePlayerIndex()) {
                            activePlayer.setText("Du");
                        } else {
                            activePlayer.setText(player.getUserName());
                        }
                    }
                    if (player.getPlayerIndex() == gameMessage.getNextPlayerIndex()) {
                        if (myPlayer.getPlayerIndex() == gameMessage.getNextPlayerIndex()) {
                            nextPlayer.setText("Du");
                        } else {
                            nextPlayer.setText(player.getUserName());
                        }
                    }
                }


            }
        });

    }

    private void setUpAfterOtherPlaceCard(GameMessage gameMessage) {

        for (PlayerImpl currentPlayer : gameMessage.getPlayerList()) {


            switch (currentPlayer.getPlayerIndex()) {
                case 1:
                    player1.setHandCardsCount(currentPlayer.getHandCardsCount());
                    break;
                case 2:
                    player2.setHandCardsCount(currentPlayer.getHandCardsCount());
                    break;
                case 3:
                    player3.setHandCardsCount(currentPlayer.getHandCardsCount());
                    break;
                case 4:
                    player4.setHandCardsCount(currentPlayer.getHandCardsCount());
                    break;
            }

        }

        setOtherPlayerCardCount();

    }

    private void setOtherPlayerCardCount() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                int myPlayerIndex = myPlayer.getPlayerIndex();
                int otherPlayersCount = allPlayerList.size() - 1;

                switch (otherPlayersCount) {

                    //One other players in the game
                    case 1:

                        Log.i("SET_OTHER_PLAYERS_CASE1", " " + String.valueOf(otherPlayersCount));
                        switch (myPlayerIndex) {
                            case 1:
                                playername1.setText(player2.getUserName());
                                otherplayerCount1.setText(String.valueOf(player2.getHandCardsCount()));
                                break;

                            case 2:
                                playername1.setText(player1.getUserName());
                                otherplayerCount1.setText(String.valueOf(player1.getHandCardsCount()));
                                break;
                        }

                        playername2.setText("");
                        otherplayerCount2.setText("");
                        playername3.setText("");
                        otherplayerCount3.setText("");
                        break;

                    //Two other players in the game
                    case 2:
                        Log.i("SET_OTHER_PLAYERS_CASE2", " " + String.valueOf(otherPlayersCount));
                        switch (myPlayerIndex) {
                            case 1:
                                playername1.setText(player2.getUserName());
                                otherplayerCount1.setText(String.valueOf(player2.getHandCardsCount()));
                                playername2.setText(player3.getUserName());
                                otherplayerCount2.setText(String.valueOf(player3.getHandCardsCount()));
                                break;

                            case 2:
                                playername1.setText(player3.getUserName());
                                otherplayerCount1.setText(String.valueOf(player3.getHandCardsCount()));
                                playername2.setText(player1.getUserName());
                                otherplayerCount2.setText(String.valueOf(player1.getHandCardsCount()));
                                break;

                            case 3:
                                playername1.setText(player1.getUserName());
                                otherplayerCount1.setText(String.valueOf(player1.getHandCardsCount()));
                                playername2.setText(player2.getUserName());
                                otherplayerCount2.setText(String.valueOf(player2.getHandCardsCount()));
                                break;
                        }

                        playername3.setText("");
                        otherplayerCount3.setText("");
                        break;

                    //Three other players in the game
                    case 3:
                        Log.i("SET_OTHER_PLAYERS_CASE3", " " + String.valueOf(otherPlayersCount));
                        switch (myPlayerIndex) {
                            case 1:
                                playername1.setText(player2.getUserName());
                                otherplayerCount1.setText(String.valueOf(player2.getHandCardsCount()));
                                playername2.setText(player3.getUserName());
                                otherplayerCount2.setText(String.valueOf(player3.getHandCardsCount()));
                                playername3.setText(player4.getUserName());
                                otherplayerCount3.setText(String.valueOf(player4.getHandCardsCount()));
                                break;

                            case 2:
                                playername1.setText(player3.getUserName());
                                otherplayerCount1.setText(String.valueOf(player3.getHandCardsCount()));
                                playername2.setText(player4.getUserName());
                                otherplayerCount2.setText(String.valueOf(player4.getHandCardsCount()));
                                playername3.setText(player1.getUserName());
                                otherplayerCount3.setText(String.valueOf(player1.getHandCardsCount()));
                                break;

                            case 3:
                                playername1.setText(player4.getUserName());
                                otherplayerCount1.setText(String.valueOf(player4.getHandCardsCount()));
                                playername2.setText(player1.getUserName());
                                otherplayerCount2.setText(String.valueOf(player1.getHandCardsCount()));
                                playername3.setText(player2.getUserName());
                                otherplayerCount3.setText(String.valueOf(player2.getHandCardsCount()));
                                break;

                            case 4:
                                playername1.setText(player1.getUserName());
                                otherplayerCount1.setText(String.valueOf(player1.getHandCardsCount()));
                                playername2.setText(player2.getUserName());
                                otherplayerCount2.setText(String.valueOf(player2.getHandCardsCount()));
                                playername3.setText(player3.getUserName());
                                otherplayerCount3.setText(String.valueOf(player3.getHandCardsCount()));
                                break;
                        }

                        break;

                }
            }
        });
    }


    //Sets card to my Hand
    private void setMyHand(final GameMessage gameMessage) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                cardList.clear();

                for (Card card : gameMessage.getHand()) {
                    cardList.add(new ExampleItem(card));
                }
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    private void showLastDiscardedCard(final GameMessage gameMessage) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                currentStackCard.setText(gameMessage.getLastDiscardedCard().getColor().toString() + " " + gameMessage.getLastDiscardedCard().getValue().toString());

                if (!gameMessage.getLastDiscardedCard().isWildCard())
                    switch (gameMessage.getLastDiscardedCard().getColor()) {
                        case RED:
                            colorImage.setImageResource(R.drawable.ic_red);
                            break;
                        case BLUE:
                            colorImage.setImageResource(R.drawable.ic_blue);
                            break;
                        case GREEN:
                            colorImage.setImageResource(R.drawable.ic_green);
                            break;
                        case YELLOW:
                            colorImage.setImageResource(R.drawable.ic_yellow);
                            break;
                        case WILD:
                    }
                else {
                    if (gameMessage.getWildCardColor() != null) {

                        switch (gameMessage.getWildCardColor()) {
                            case RED:
                                colorImage.setImageResource(R.drawable.ic_red);
                                break;
                            case BLUE:
                                colorImage.setImageResource(R.drawable.ic_blue);
                                break;
                            case GREEN:
                                colorImage.setImageResource(R.drawable.ic_green);
                                break;
                            case YELLOW:
                                colorImage.setImageResource(R.drawable.ic_yellow);
                                break;
                        }
                    }
                }

            }
        });

    }

    private void DrawCard() {

    }

    private void isValidCard(final GameMessage gameMessage) {


        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                showLastDiscardedCard(gameMessage);
                setActiveAndNextPlayer(gameMessage);
                setMyHand(gameMessage);
            }
        });
    }

    private void otherPlayerPlacedCard(final GameMessage gameMessage) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                setActiveAndNextPlayer(gameMessage);
                showLastDiscardedCard(gameMessage);
                setUpAfterOtherPlaceCard(gameMessage);
            }
        });

    }


    @Override
    public void onMessageReceived(String message) {

        GameMessage gameMessage = gson.fromJson(message, GameMessage.class);
        //LobbyMessage lobbyMessage = gson.fromJson(message, LobbyMessage.class);

        switch (gameMessage.getAction()) {

            //If the game begins
            case SETUP_GAME:

                Log.i("Receive", "Setup_Game is called on GameActivity");
                setupGame(gameMessage);
                startTimer();

                break;

            //If an other player place a card
            case PLACE_CARD:

                otherPlayerPlacedCard(gameMessage);

                break;

            //If the card you placed is invalid
            case INVALID_CARD:

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(GameActivity.this, R.string.invalidCard, Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case VALID_CARD:

                isValidCard(gameMessage);

                break;

            //If you must draw min. one card
            case DRAW_CARD:
                setMyHand(gameMessage);
                break;

            //If the time is over and its next players turn
            case NEXT_PLAYER:
                resetTimer();
                setActiveAndNextPlayer(gameMessage);
                setUpAfterOtherPlaceCard(gameMessage);
                startTimer();
                break;

            //If an other player leave the game
            case PLAYER_LEFT_GAME:

                break;

            //If the Game is over / a player wins
            case GAME_OVER:

                break;


            case NOT_YOUR_TURN:

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(GameActivity.this, R.string.notYourTurn, Toast.LENGTH_SHORT).show();
                    }
                });

                break;


        }
    }

    private void startTimer() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mTimeLeftInMillis = millisUntilFinished;
                        updateCountDownText();
                    }

                    @Override
                    public void onFinish() {
                        mTimerRunning = false;
                    }
                }.start();

                mTimerRunning = true;
            }
        });

    }


    private void resetTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void updateCountDownText() {
        //int minutes = (int) (mTimeLeftInMillis / 1000) / 60;


        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

                mTextViewCountDown.setText(timeLeftFormatted);
           }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webSocketService.deregisterListener(GameActivity.this);

        if (connection != null) {
            unbindService(connection);
        }

    }

}
