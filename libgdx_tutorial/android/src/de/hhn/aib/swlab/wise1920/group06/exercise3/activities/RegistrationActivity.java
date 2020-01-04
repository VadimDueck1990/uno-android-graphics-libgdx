package de.hhn.aib.swlab.wise1920.group06.exercise3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;

import de.hhn.aib.swlab.wise1920.group06.exercise3.R;
import de.hhn.aib.swlab.wise1920.group06.exercise3.helper.PreferenceHelper;
import de.hhn.aib.swlab.wise1920.group06.core.models.User;
import de.hhn.aib.swlab.wise1920.group06.exercise3.serverCommunication.RetrofitAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.hhn.aib.swlab.wise1920.group06.exercise3.serverCommunication.RetrofitAPI.BASE_URL;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private RetrofitAPI retrofitAPI;
    private Retrofit retrofit;
    private PreferenceHelper preferenceHelper;
    public static final Gson GSON = new GsonBuilder().setLenient().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameEditText = findViewById(R.id.editTextUsernameRegister);
        passwordEditText = findViewById(R.id.editTextPasswordRegister);
        Button registerButton = findViewById(R.id.buttonRegister);
        preferenceHelper = new PreferenceHelper(RegistrationActivity.this);

        //Retrofit initialisieren
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GSON)).build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //Check if username and password are empty
                if (!username.equals("") && !password.equals("")) {

                    User user = new User(username, password);
                    user.setUsername(username);
                    user.setPassword(password);
                    preferenceHelper.writeUsername(username);

                    Call<Void> call = retrofitAPI.registerUser(user);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                            switch (response.code()) {

                                case HttpURLConnection.HTTP_CREATED: {

                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.register_successful, Toast.LENGTH_SHORT);
                                    toast.show();

                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                    break;
                                }

                                case HttpURLConnection.HTTP_CONFLICT: {

                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            R.string.failed_authorization, Toast.LENGTH_SHORT);
                                    toast.show();

                                    break;
                                }

                                default: {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.unexpected_error, Toast.LENGTH_SHORT);
                                    toast.show();
                                    Log.e("Error: ", Integer.toString(response.code()));
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
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
}
