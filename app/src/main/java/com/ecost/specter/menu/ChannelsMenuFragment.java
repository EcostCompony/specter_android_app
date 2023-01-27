package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.channel.ChannelActivity;
import com.ecost.specter.R;
import com.ecost.specter.databinding.FragmentChannelsMenuBinding;
import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Chapter;
import com.ecost.specter.recyclers.ChannelsAdapter;

import com.ecost.specter.recyclers.ChaptersAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChannelsMenuFragment extends Fragment {

    ChaptersAdapter chaptersAdapter;
    ChannelsAdapter channelsAdapter;
    TextView tChannelsNumber;
    List<Chapter> chapters = new ArrayList<>();
    List<Channel> channels = new ArrayList<>();
    ChildEventListener childEventListener;
    MainMenuActivity mainMenuActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        childEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channel.subscribers.size(); i++) {
                    if (channel.subscribers.get(i).equals(authId)) {
                        channel.body = channel.body.replace("%CHANNEL_CREATED%", getString(R.string.channels_menu_parametr_channel_created));
                        channels.add(channel);
                        tChannelsNumber.setText(pluralForm(channelsAdapter.getItemCount(), getString(R.string.count_channel1), getString(R.string.count_channel2), getString(R.string.count_channel3), Locale.getDefault().getLanguage().equals("ru")));
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
                        tChannelsNumber.setText(pluralForm(channelsAdapter.getItemCount(), getString(R.string.count_channel1), getString(R.string.count_channel2), getString(R.string.count_channel3), Locale.getDefault().getLanguage().equals("ru")));
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
        FragmentChannelsMenuBinding binding = FragmentChannelsMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView rChaptersList = binding.recyclerChaptersList;
        RecyclerView rChannelList = binding.recyclerChannelsList;
        tChannelsNumber = binding.numberChannels;
        mainMenuActivity = (MainMenuActivity) requireActivity();

        chapters.add(new Chapter(0, getString(R.string.channels)));

        rChannelList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, (channel, position) -> {
            Intent intent = new Intent(mainMenuActivity, ChannelActivity.class);
            intent.putExtra("CHANNEL_ID", String.valueOf(channel.id));
            intent.putExtra("CHANNEL_ADMINS", channel.author);
            intent.putExtra("CHANNEL_TITLE", channel.title);
            intent.putExtra("CHANNEL_SHORT_LINK", String.valueOf(channel.shortLink));
            startActivity(intent);
        }, (channel, position) -> true);
        rChannelList.setAdapter(channelsAdapter);

        rChaptersList.setLayoutManager(new LinearLayoutManager(mainMenuActivity, LinearLayoutManager.HORIZONTAL, false));
        chaptersAdapter = new ChaptersAdapter(mainMenuActivity, chapters, (chapter, position) -> {});
        rChaptersList.setAdapter(chaptersAdapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).show();
        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.navigate_user_photo);
    }

}