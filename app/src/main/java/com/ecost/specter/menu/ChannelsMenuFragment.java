package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

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
import com.ecost.specter.models.User;
import com.ecost.specter.recyclers.ChannelsAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChannelsMenuFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_menu, container, false);

        RecyclerView rvChannelsList = inflaterView.findViewById(R.id.recycler_channels_list);
        List<Channel> channels = new ArrayList<>();
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        rvChannelsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        ChannelsAdapter channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, mainMenuActivity::startChannel);
        rvChannelsList.setAdapter(channelsAdapter);

        myDB.child("specter").child("channels").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (Map.Entry<String, User> subscriber : channel.subscribers.entrySet()) {
                    if (subscriber.getValue().id.equals(authId)) {
                        if (channel.body.equals("%CHANNEL_CREATED%")) channel.body = mainMenuActivity.getString(R.string.channels_menu_attribute_channel_created);
                        else if (channel.body.equals("%NOT_POSTS%")) channel.body = mainMenuActivity.getString(R.string.channels_menu_attribute_not_posts);
                        channels.add(channel);
                        ((TextView) inflaterView.findViewById(R.id.number_channels)).setText(pluralForm(channelsAdapter.getItemCount(), mainMenuActivity.getString(R.string.number_channels_nominative_case), mainMenuActivity.getString(R.string.number_channels_genitive_case), mainMenuActivity.getString(R.string.number_channels_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
                        channelsAdapter.notifyItemInserted(channels.size()-1);
                        break;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channelsAdapter.getItemCount(); i++) {
                    if (channels.get(i).id.equals(channel.id)) {
                        if (channel.body.equals("%NOT_POSTS%")) channel.body = mainMenuActivity.getString(R.string.channels_menu_attribute_not_posts);
                        channels.set(i, channel);
                        channelsAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        inflaterView.findViewById(R.id.button_navigate).setOnClickListener(view -> new NavigationFragment().show(mainMenuActivity.getSupportFragmentManager(), new NavigationFragment().getTag()));
        inflaterView.findViewById(R.id.button_search).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsSearchFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

}