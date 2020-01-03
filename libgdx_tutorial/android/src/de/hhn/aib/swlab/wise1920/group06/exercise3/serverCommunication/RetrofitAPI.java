package de.hhn.aib.swlab.wise1920.group06.exercise3.serverCommunication;

import de.hhn.aib.swlab.wise1920.group06.exercise3.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {

    String BASE_URL = "https://user.ex3.swlab-hhn.de/";

    @POST("user")
    Call<Void> registerUser(@Body User user);

    @POST("user/login")
    Call<Void> loginUser(@Body User user);

}
