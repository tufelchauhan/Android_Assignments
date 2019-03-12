package com.example.tufel.chatapp.controls;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tufel.chatapp.R;
import com.example.tufel.chatapp.models.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tufel on 09-03-2019.
 */

public class usersAdapter extends  RecyclerView.Adapter<usersAdapter.ViewHolder> {

    private ArrayList<user> userlist;
    public usersAdapter(ArrayList<user> tempuserlist)
    {
         userlist = tempuserlist;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView)itemView.findViewById(R.id.user);
        }
    }

    @Override
    public usersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.userlist_row_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(usersAdapter.ViewHolder holder, int position) {
        user u = userlist.get(position);
        TextView textView = holder.username;
        textView.setText(u.getName());
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }


}
