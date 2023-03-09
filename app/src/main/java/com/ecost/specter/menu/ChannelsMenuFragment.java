package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Channel;
import com.ecost.specter.recyclers.ChannelsAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChannelsMenuFragment extends Fragment {

    ChannelsAdapter channelsAdapter;
    TextView tChannelsNumber;
    List<Channel> channels = new ArrayList<>();
    MainMenuActivity mainMenuActivity;

    @Override
    public void onStart() {
        super.onStart();
        channels.clear();
        ChildEventListener childEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channel.subscribers.size(); i++) {
                    if (channel.subscribers.get(i).equals(authId)) {
                        if (channel.body.equals("%CHANNEL_CREATED%")) channel.body = mainMenuActivity.getString(R.string.channels_menu_attribute_channel_created);
                        else if (channel.body.equals("%NOT_POSTS%")) channel.body = mainMenuActivity.getString(R.string.channels_menu_attribute_not_posts);
                        channels.add(channel);
                        tChannelsNumber.setText(pluralForm(channelsAdapter.getItemCount(), mainMenuActivity.getString(R.string.number_channels_nominative_case), mainMenuActivity.getString(R.string.number_channels_genitive_case), mainMenuActivity.getString(R.string.number_channels_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
                        channelsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channelsAdapter.getItemCount(); i++) {
                    if (channels.get(i).id.equals(channel.id)) {
                        if (channel.body.equals("%NOT_POSTS%")) channel.body = mainMenuActivity.getString(R.string.channels_menu_attribute_not_posts);
                        channels.set(i, channel);
                        channelsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channels.size(); i++) {
                    if (channels.get(i).id.equals(channel.id)) {
                        channels.remove(i);
                        tChannelsNumber.setText(pluralForm(channelsAdapter.getItemCount(), mainMenuActivity.getString(R.string.number_channels_nominative_case), mainMenuActivity.getString(R.string.number_channels_genitive_case), mainMenuActivity.getString(R.string.number_channels_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
                        channelsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) { }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        myDB.child("specter").child("channels").addChildEventListener(childEventListener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_menu, container, false);

        RecyclerView rChannelList = inflaterView.findViewById(R.id.recycler_channels_list);
        tChannelsNumber = inflaterView.findViewById(R.id.number_channels);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        rChannelList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, (channel, position) -> mainMenuActivity.startChannel(channel, true), (channel, position) -> true);
        rChannelList.setAdapter(channelsAdapter);

        inflaterView.findViewById(R.id.button_navigate).setOnClickListener(view -> new NavigationFragment().show(mainMenuActivity.getSupportFragmentManager(), new NavigationFragment().getTag()));

        inflaterView.findViewById(R.id.button_search).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsSearchFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

}