package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

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
import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Post;
import com.ecost.specter.models.User;
import com.ecost.specter.recyclers.PostsAdapter;
import com.ecost.specter.recyclers.UsersAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SubscribersChannelSettingsFragment extends Fragment {

    EditText eSearchUser;
    RecyclerView rUsersList;
    UsersAdapter usersAdapter;
    List<User> users = new ArrayList<>();
    List<String> keys = new ArrayList<>();
    ChildEventListener childEventListenerB;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_subscribers_channel_settings, container, false);

        eSearchUser = inflaterView.findViewById(R.id.input_search_user);
        rUsersList = inflaterView.findViewById(R.id.recycler_users_list);
        channelActivity = (ChannelActivity) requireActivity();

        UsersAdapter.OnAddAdminClickListener onClickListener = position -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").child(keys.get(position)).child("channel_admin").setValue(true);
        };
        rUsersList.setLayoutManager(new LinearLayoutManager(channelActivity));
        usersAdapter = new UsersAdapter(channelActivity, users, onClickListener);
        rUsersList.setAdapter(usersAdapter);

        @SuppressLint("NotifyDataSetChanged")
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                users.add(Objects.requireNonNull(dataSnapshot.getValue(User.class)));
                keys.add(dataSnapshot.getKey());
                usersAdapter.notifyDataSetChanged();
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);

        eSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                users.clear();
                keys.clear();
                childEventListenerB = new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        User user = Objects.requireNonNull(dataSnapshot.getValue(User.class));
                        if (!eSearchUser.getText().toString().trim().equals("")) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(childEventListener);
                        if (!eSearchUser.getText().toString().trim().equals("") && (Pattern.compile(eSearchUser.getText().toString().trim(), Pattern.CASE_INSENSITIVE).matcher(user.name.toLowerCase()).find() || Pattern.compile(eSearchUser.getText().toString().trim(), Pattern.CASE_INSENSITIVE).matcher(user.link.toLowerCase()).find())) {
                            users.add(user);
                            keys.add(dataSnapshot.getKey());
                        } else if (eSearchUser.getText().toString().trim().equals("")) {
                            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);
                            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(childEventListenerB);
                        }
                        usersAdapter.notifyDataSetChanged();
                    }

                    @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) { }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                };
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListenerB);
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return inflaterView;
    }

}