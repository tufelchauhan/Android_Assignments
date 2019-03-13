package com.example.tufel.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tufel.chatapp.models.user;
import com.example.tufel.chatapp.services.ChatService;
import com.example.tufel.chatapp.services.ChatServiceBuilder;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    user curuser;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+".my_file", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        ChatService loginService = ChatServiceBuilder.buildService(ChatService.class);
        TextView username = (TextView) findViewById(R.id.username);
        String tmp = username.getText().toString();
        if(tmp.contains(" "))
        {
            Toast.makeText(context,"Please provide username without spaces",Toast.LENGTH_SHORT).show();
        }
        else {
            curuser = new user();
            curuser.setName(username.getText().toString());
            Call<user> call = loginService.login(curuser);
            call.enqueue(new Callback<user>() {
                @Override
                public void onResponse(Call<user> call, Response<user> response) {
                    editor.putString("token", response.body().getToken());
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<user> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "No Internet Connectivity. Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
