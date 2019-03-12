package com.example.tufel.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufel.chatapp.controls.usersAdapter;
import com.example.tufel.chatapp.models.Messages;
import com.example.tufel.chatapp.models.user;
import com.example.tufel.chatapp.services.ChatService;
import com.example.tufel.chatapp.services.ChatServiceBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

public class ListActivity extends AppCompatActivity {
    ArrayList<user> usersList;
    ArrayList<Messages> messageList;
    ListView userView,messagesView;
    Button sendButton;
    usersAdapter adapter;
    Context context;
    String token;
    int userid=0;
    int touserid=0;
    int pos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        userView = (ListView) findViewById(R.id.userlist);
        messagesView = (ListView) findViewById(R.id.messagelist);
        sendButton = (Button)findViewById(R.id.send);
        context=this;
        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+".my_file", Context.MODE_PRIVATE);
        token=sharedPreferences.getString("token","N/A");

        ChatService userListService = ChatServiceBuilder.buildService(ChatService.class);
        Call<ArrayList<user>> call = userListService.getUsers(token);
        call.enqueue(new Callback<ArrayList<user>>() {
            @Override
            public void onResponse(Call<ArrayList<user>> call, Response<ArrayList<user>> response) {
                usersList = response.body();
                //adapter = new usersAdapter(usersList);
                String userarr[] = new String[usersList.size()];
                for(int i=0;i<usersList.size();i++){
                    userarr[i]=usersList.get(i).getName();
                }
                userView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,userarr));
                //userView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onFailure(Call<ArrayList<user>> call, Throwable t) {

            }
        });
        userView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(context,String.valueOf(usersList.get(pos).getId()),Toast.LENGTH_SHORT).show();
                userid = usersList.get(pos).getId();
                showMessages(userid);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText sendmessagetext = (EditText) findViewById(R.id.sendmessagetext);
                String message = sendmessagetext.getText().toString();
                String tmp = message.trim();
                if(tmp.isEmpty()){
                    Toast.makeText(context,"Please type some text",Toast.LENGTH_SHORT).show();
                }
                else {
                    Messages newMessage = new Messages();
                    newMessage.setToUserId(userid);
                    newMessage.setMessage(message);
                    ChatService sendMessageService = ChatServiceBuilder.buildService(ChatService.class);
                    Call<Void> call3 = sendMessageService.sendMessage(newMessage, token);
                    call3.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            showMessages(userid);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            showMessages(userid);
                        }
                    });
                }
            }
        });

    }

    void showMessages(int userid){
        ChatService messageListService = ChatServiceBuilder.buildService(ChatService.class);
        Call<ArrayList<Messages>> call2 = messageListService.getMessages(userid,token);
        call2.enqueue(new Callback<ArrayList<Messages>>() {
            @Override
            public void onResponse(Call<ArrayList<Messages>> call, Response<ArrayList<Messages>> response) {
                messageList = response.body();
                String messagesarr[] = new String[messageList.size()];
                for(int i=0;i<messageList.size();i++){
                    messagesarr[i]=messageList.get(i).getMessage();
                }
                messagesView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,messagesarr));
            }

            @Override
            public void onFailure(Call<ArrayList<Messages>> call, Throwable t) {

            }
        });
    }
}