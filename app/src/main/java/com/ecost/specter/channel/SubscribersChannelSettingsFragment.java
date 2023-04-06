package com.ecost.specter.channel;

import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecost.specter.R;
import com.ecost.specter.models.Post;
import com.ecost.specter.models.User;
import com.ecost.specter.recyclers.PostsAdapter;
import com.ecost.specter.recyclers.UsersAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubscribersChannelSettingsFragment extends Fragment {

    RecyclerView rUsersList;
    UsersAdapter usersAdapter;
    List<User> users = new ArrayList<>();
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_subscribers_channel_settings, container, false);

        rUsersList = inflaterView.findViewById(R.id.recycler_users_list);
        channelActivity = (ChannelActivity) requireActivity();

        rUsersList.setLayoutManager(new LinearLayoutManager(channelActivity));
        usersAdapter = new UsersAdapter(channelActivity, users);
        rUsersList.setAdapter(usersAdapter);

        @SuppressLint("NotifyDataSetChanged")
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                users.add(Objects.requireNonNull(dataSnapshot.getValue(User.class)));
                usersAdapter.notifyDataSetChanged();
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);

        return inflaterView;
    }

}