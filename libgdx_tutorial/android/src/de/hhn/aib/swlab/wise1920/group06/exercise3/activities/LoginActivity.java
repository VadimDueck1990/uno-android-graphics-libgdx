package de.hhn.aib.swlab.wise1920.group06.exercise3.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.net.HttpURLConnection;

import de.hhn.aib.swlab.wise1920.group06.exercise3.R;
import de.hhn.aib.swlab.wise1920.group06.exercise3.helper.PreferenceHelper;
import de.hhn.aib.swlab.wise1920.group06.exercise3.interfaces.MessageListener;
import de.hhn.aib.swlab.wise1920.group06.exercise3.models.LobbyMessage;
import de.hhn.aib.swlab.wise1920.group06.exercise3.models.User;
import de.hhn.aib.swlab.wise1920.group06.exercise3.serverCommunication.RetrofitAPI;
import de.hhn.aib.swlab.wise1920.group06.exercise3.services.WebSocketService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.hhn.aib.swlab.wise1920.group06.exercise3.serverCommunication.RetrofitAPI.BASE_URL;

public class LoginActivity extends AppCompatActivity implements MessageListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private PreferenceHelper preferenceHelper;
    private RetrofitAPI retrofitAPI;
    private Retrofit retrofit;
    private WebSocketService webSocketService;
    private Gson gson;
    private Context context;
    boolean serviceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        usernameEditText = findViewById(R.id.editTextUsernameLogin);
        passwordEditText = findViewById(R.id.editTextPasswordLogin);
        Button registerButton = findViewById(R.id.registerButtonOnLogin);
        Button loginButton = findViewById(R.id.buttonLogin);
        preferenceHelper = new PreferenceHelper(LoginActivity.this);
        gson = new Gson();

        Intent intent = new Intent(this,WebSocketService.class);
        bindService(intent ,connection, Context.BIND_AUTO_CREATE);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRegisterActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intentToRegisterActivity);
                finish();

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                preferenceHelper.writeUsername(username);
                preferenceHelper.writePW(password);

                //Retrofit initialisieren
                retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                retrofitAPI = retrofit.create(RetrofitAPI.class);


                //DEBUG
                //Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                //startActivity(intent);
                //finish();


                //Check if username and password are empty
                if (!username.equals("") && !password.equals("")) {

                    User userData = new User(username, password);
                    preferenceHelper.writeUsername(username);

                    Call<Void> call = retrofitAPI.loginUser(userData);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                            switch (response.code()) {

                                case HttpURLConnection.HTTP_OK: {

                                    preferenceHelper.writeAuthToken(response.headers().get("Authorization"));
                                    LobbyMessage gm = new LobbyMessage();
                                    gm.setAction(LobbyMessage.Command.LOGIN);
                                    gm.setAuthentication(preferenceHelper.readAuthToken());

                                    //Sende neue Join Action ueber Websocket Verbindung
                                    webSocketService.sendMessage(gm);

                                    break;
                                }

                                case HttpURLConnection.HTTP_FORBIDDEN: {

                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            R.string.failed_authorization, Toast.LENGTH_SHORT);
                                    toast.show();

                                    break;
                                }

                                default: {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.unexpected_error, Toast.LENGTH_SHORT);
                                    toast.show();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet_connection + " Code: " + t.getMessage(), Toast.LENGTH_SHORT);
                            toast.show();
                            Log.e("Error: ", t.getMessage());
                        }
                    });


                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.missing_data, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

        });


    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService.WebSocketServiceBinder binder = (WebSocketService.WebSocketServiceBinder) service;
            webSocketService = binder.getService();
            serviceBound = true;
            webSocketService.registerListener(LoginActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
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

            case SUCCESS:

                Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                startActivity(intent);
                finish();

                break;


            case SERVER_FULL:

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, R.string.serverFull, Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case GAME_RUNNING:

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, R.string.gameIsRunning, Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case PLAYER_EXIST:

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, R.string.playerExist, Toast.LENGTH_SHORT).show();
                    }
                });

                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webSocketService.deregisterListener(LoginActivity.this);
        if (connection != null) {
            unbindService(connection);
        }

    }
}
