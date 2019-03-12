package com.example.tufel.chatapp.services;


import com.example.tufel.chatapp.models.Messages;
import com.example.tufel.chatapp.models.user;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    @POST("user/login")
    Call<user> login(@Body user u1);

    @GET("user")
    Call<ArrayList<user>> getUsers(@Header("Authorization") String token);


    @GET("chat/{id}")
    Call<ArrayList<Messages>> getMessages(@Path("id") int id, @Header("Authorization") String token);

    @POST("chat")
    Call<Void> sendMessage(@Body Messages message,@Header("Authorization") String token);


}
