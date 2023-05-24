/*package com.ecost.specter.channel;

import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ecost.specter.R;
import com.ecost.specter.models.User;
import com.ecost.specter.recyclers.UsersAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SubscribersChannelSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_subscribers_channel_settings, container, false);

        EditText etSubscriberSearch = inflaterView.findViewById(R.id.input_search_user);
        RecyclerView rvSubscribersList = inflaterView.findViewById(R.id.recycler_users_list);
        List<User> subscribers = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        rvSubscribersList.setLayoutManager(new LinearLayoutManager(channelActivity));
        UsersAdapter subscribersAdapter = new UsersAdapter(channelActivity, subscribers, position -> myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").child(keys.get(position)).child("channel_admin").setValue(true));
        rvSubscribersList.setAdapter(subscribersAdapter);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                subscribers.add(Objects.requireNonNull(dataSnapshot.getValue(User.class)));
                keys.add(dataSnapshot.getKey());
                subscribersAdapter.notifyItemInserted(subscribers.size()-1);
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);

        etSubscriberSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                subscribers.clear();
                keys.clear();
                ChildEventListener childEventListenerSearch = new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        User subscriber = Objects.requireNonNull(dataSnapshot.getValue(User.class));
                        if (!etSubscriberSearch.getText().toString().trim().equals("")) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(childEventListener);
                        if (!etSubscriberSearch.getText().toString().trim().equals("") && (Pattern.compile(etSubscriberSearch.getText().toString().trim(), Pattern.CASE_INSENSITIVE).matcher(subscriber.getName().toLowerCase()).find() || Pattern.compile(etSubscriberSearch.getText().toString().trim(), Pattern.CASE_INSENSITIVE).matcher(subscriber.getShortLink().toLowerCase()).find())) {
                            subscribers.add(subscriber);
                            keys.add(dataSnapshot.getKey());
                        } else if (etSubscriberSearch.getText().toString().trim().equals("")) {
                            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);
                            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(this);
                        }
                        subscribersAdapter.notifyDataSetChanged();
                    }

                    @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) { }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                };
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListenerSearch);
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return inflaterView;
    }

}*/