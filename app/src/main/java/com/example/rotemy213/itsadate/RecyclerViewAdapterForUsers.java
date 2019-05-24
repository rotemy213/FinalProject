package com.example.rotemy213.itsadate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapterForUsers extends RecyclerView.Adapter<RecyclerViewAdapterForUsers.ViewHolder>{
    private static ArrayList<String> users = new ArrayList<>();
    private Context mContext;
    private ArrayList allInvitedPeople = new ArrayList();
    private Button createMutualEvent;
    private String usermail;
    private TextView invited_people;



    public RecyclerViewAdapterForUsers(ArrayList<String> users, Context mContext, Button createMutualEvent
    , String usermail, TextView textView)
    {
        this.usermail = usermail.replace(" ", "");
        this.mContext = mContext;
        this.invited_people = textView;
        if(!allInvitedPeople.contains(usermail))
            allInvitedPeople.add(usermail);
        System.out.println("allInvitedPeople " + allInvitedPeople);
        this.users = users;
        this.createMutualEvent = createMutualEvent;
        System.out.println("USERS ARRAY: " + users);
//        allInvitedPeople = new ArrayList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.listitem_users, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.username.setText(users.get(i));
        viewHolder.username.setTypeface(viewHolder.username.getTypeface(), Typeface.BOLD);
        viewHolder.inviteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    if (createMutualEvent.getVisibility() != View.VISIBLE) {
                        createMutualEvent.setVisibility(View.VISIBLE);
                    }
                    if (!allInvitedPeople.contains(viewHolder.username.getText())) {
                        allInvitedPeople.add((String) viewHolder.username.getText());
                        viewHolder.cancelInvitation.setVisibility(View.VISIBLE);
                        invited_people.setText(allInvitedPeople.toString().replace("[", "").replace("]", ""));
                    }
                    System.out.println("allInvitedPeople" + allInvitedPeople);
                    createMutualEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(mContext, MutualEvent.class).putExtra("invitedPeople", allInvitedPeople.toString());
                            mContext.startActivity(in);
                        }
                    });

            }
        });
        viewHolder.cancelInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allInvitedPeople.remove(viewHolder.username.getText().toString());
                viewHolder.cancelInvitation.setVisibility(View.INVISIBLE);
                viewHolder.inviteButton.setVisibility(View.VISIBLE);
                invited_people.setText(allInvitedPeople.toString().replace("[", "").replace("]", ""));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        LinearLayout parentLayout;
        Button inviteButton;
        Button cancelInvitation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username= itemView.findViewById(R.id.username);
            parentLayout = itemView.findViewById(R.id.parent);
            inviteButton = itemView.findViewById(R.id.btn_invite);
            cancelInvitation = itemView.findViewById(R.id.btn_uninvite);
        }
    }
}
