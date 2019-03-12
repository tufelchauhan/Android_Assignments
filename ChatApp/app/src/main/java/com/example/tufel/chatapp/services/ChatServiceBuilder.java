package com.example.tufel.chatapp.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatServiceBuilder {
    public static ChatService buildService;
    public static String str = "hello";
    public static final String URL = "https://chat.promactinfo.com/api/";
    public static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create());
    public static Retrofit retrofit = builder.build();
    public static <S> S buildService(Class<S> serviceType){
        return retrofit.create(serviceType);
    }
}