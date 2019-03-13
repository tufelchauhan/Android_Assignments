package com.example.tufel.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

public class ListActivity extends AppCompatActivity {
    ArrayList<user> usersList;
    ArrayList<Messages> messageList;
    ArrayList<Messages> offlineMessages;
    ListView userView, messagesView;
    Button sendButton;
    usersAdapter adapter;
    Context context;
    String token;
    int userid = -1;
    int touserid = 0;
    int pos = 0;
    Gson gson;
    ChatService chatService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        context = this;
        offlineMessages = new ArrayList<>();
        userView = (ListView) findViewById(R.id.userlist);
        messagesView = (ListView) findViewById(R.id.messagelist);
        sendButton = (Button) findViewById(R.id.send);
        gson = new Gson();

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".my_file", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "N/A");

        //display userlist
        chatService = ChatServiceBuilder.buildService(ChatService.class);
        Call<ArrayList<user>> call = chatService.getUsers(token);
        call.enqueue(new Callback<ArrayList<user>>() {
            @Override
            public void onResponse(Call<ArrayList<user>> call, Response<ArrayList<user>> response) {
                usersList = response.body();
                //adapter = new usersAdapter(usersList);
                String userarr[] = new String[usersList.size()];
                for (int i = 0; i < usersList.size(); i++) {
                    userarr[i] = usersList.get(i).getName();
                }
                userView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, userarr));
            }
            @Override
            public void onFailure(Call<ArrayList<user>> call, Throwable t) {
            }
        });//display userlist

        //userlist itemclick
        userView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(context,String.valueOf(usersList.get(pos).getId()),Toast.LENGTH_SHORT).show();
                userid = usersList.get(pos).getId();
                uploadOfflineMessages();
                showMessages();
            }
        });//userlist itemclick

        //sendbutton click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText sendmessagetext = (EditText) findViewById(R.id.sendmessagetext);
                String message = sendmessagetext.getText().toString();
                String tmp = message.trim();
                if(tmp.isEmpty()){
                    Toast.makeText(context,"Please type some text",Toast.LENGTH_SHORT).show();
                }
                else{
                    Messages tmpMessage = new Messages();
                    tmpMessage.setToUserId(userid);
                    tmpMessage.setMessage(message);
                    if(isNetworkAvailable()) {
                        sendMessage(tmpMessage);
                    }
                    else{
                        storeOfflineMessage(tmpMessage);
                    }
                    sendmessagetext.setText("");//clear edittext
                }
            }
        });//sendbutton click
    }

    //sendMessage
    void sendMessage(Messages tmpMessage) {
        uploadOfflineMessages();
        Call<Void> call3 = chatService.sendMessage(tmpMessage, token);
        call3.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showMessages();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }//sendMessages

    void uploadOfflineMessages() {
        if (isNetworkAvailable()) {
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + "OfflineMessages", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = sharedPreferences.getString("messages", "default");
            Type type = new TypeToken<ArrayList<Messages>>() {}.getType();
            gson = new Gson();
            if(json.compareTo("default")!=0) {
                offlineMessages = gson.fromJson(json, type);
                for (int i = 0; i < offlineMessages.size(); i++) {
                    Messages tmpMessage = offlineMessages.get(i);
                    Call<Void> call3 = chatService.sendMessage(tmpMessage, token);
                    call3.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            //showMessages();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                        }
                    });
                }
                offlineMessages.clear();
                editor.remove("messages");
                editor.apply();
                showMessages();
            }
            else{
                Toast.makeText(context,"No Offline Messages",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //storeOfllineMEssage
    void storeOfflineMessage(Messages tmpMessage) {
        offlineMessages.add(tmpMessage);
        SharedPreferences sharedPreferences=getSharedPreferences(getPackageName()+"OfflineMessages", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(offlineMessages);
        editor.putString("messages",json);
        editor.apply();
    }//storeOfllineMEssage


    //showMessages
    void showMessages(){
        Call<ArrayList<Messages>> call2 = chatService.getMessages(userid, token);
        call2.enqueue(new Callback<ArrayList<Messages>>() {
            @Override
            public void onResponse(Call<ArrayList<Messages>> call, Response<ArrayList<Messages>> response) {
                messageList = response.body();
                String messagesarr[] = new String[messageList.size()];
                for (int i = 0; i < messageList.size(); i++) {
                    messagesarr[i] = messageList.get(i).getMessage();
                }
                messagesView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, messagesarr));
            }
            @Override
            public void onFailure(Call<ArrayList<Messages>> call, Throwable t) {
                }
            });
        }//showMessages

    //checkinternetconnectivity
    public boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }//checkinternetconnectivity

}