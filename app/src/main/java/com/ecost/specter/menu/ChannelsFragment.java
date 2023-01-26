package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.channel.ChannelActivity;
import com.ecost.specter.R;
import com.ecost.specter.databinding.FragmentChannelsBinding;
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
import java.util.regex.Pattern;

public class ChannelsFragment extends Fragment {

    TextView tChannelsNumber;
    ChannelsAdapter channelsAdapter;
    ChaptersAdapter chaptersAdapter;
    List<Channel> channels = new ArrayList<>();
    List<Chapter> chapters = new ArrayList<>();
    Integer chapter = 0;
    ChildEventListener childEventListener;
    MainMenuActivity mainMenuActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        childEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channel.subscribers.size(); i++) {
                    if (channel.subscribers.get(i).equals(authId)) {
                        channel.body = channel.body.replace("%CHANNEL_CREATED%", getString(R.string.channel_created));
                        channels.add(channel);
                        tChannelsNumber.setText(dec(channelsAdapter.getItemCount()));
                        channelsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
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
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                for (int i = 0; i < channels.size(); i++) {
                    if (channels.get(i).id.equals(channel.id)) {
                        channels.remove(i);
                        tChannelsNumber.setText(dec(channelsAdapter.getItemCount()));
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
        FragmentChannelsBinding binding = FragmentChannelsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView rChaptersList = binding.recyclerChaptersList;
        RecyclerView rChannelList = binding.recyclerChannelsList;
        tChannelsNumber = binding.numberChannels;
        mainMenuActivity = (MainMenuActivity) requireActivity();

        chapters.add(new Chapter(0, getString(R.string.channels)));
        chapters.add(new Chapter(1, getString(R.string.special)));
        chapters.add(new Chapter(2, getString(R.string.archive)));

        ChannelsAdapter.OnChannelClickListener channelClickListener = (channel, position) -> {
            if (channel.id != 99194165) {
                Intent intent = new Intent(mainMenuActivity, ChannelActivity.class);
                intent.putExtra("CHANNEL_ID", String.valueOf(channel.id));
                intent.putExtra("CHANNEL_ADMINS", channel.author);
                intent.putExtra("CHANNEL_TITLE", channel.title);
                intent.putExtra("CHANNEL_SHORT_LINK", String.valueOf(channel.shortLink));
                startActivity(intent);
            }
        };
        ChannelsAdapter.OnChannelLongClickListener channelLongClickListener = (channel, position) -> true;
        rChannelList.setLayoutManager(new LinearLayoutManager(getActivity()));
        channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, channelClickListener, channelLongClickListener);
        rChannelList.setAdapter(channelsAdapter);

        @SuppressLint("NotifyDataSetChanged") ChaptersAdapter.OnChapterClickListener chapterClickListener = (chapter, position) -> {
            if (!chapter.id.equals(this.chapter)) {
                myDB.child("specter").child("channels").removeEventListener(childEventListener);
                channels.clear();
                tChannelsNumber.setText(dec(0));
                if (chapter.id == 0) {
                    myDB.child("specter").child("channels").addChildEventListener(childEventListener);
                } else if (chapter.id == 1) {
                    channels.add(new Channel(99194165, "favourites", 0, "Favourites", "Your favorite posts", true));
                    tChannelsNumber.setText(dec(1));
                }
                this.chapter = chapter.id;
                channelsAdapter.notifyDataSetChanged();
            }
        };
        rChaptersList.setLayoutManager(new LinearLayoutManager(mainMenuActivity, LinearLayoutManager.HORIZONTAL, false));
        chaptersAdapter = new ChaptersAdapter(mainMenuActivity, chapters, chapterClickListener);
        rChaptersList.setAdapter(chaptersAdapter);

        return root;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).show();
        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ava_nav);

        mainMenuActivity.tName.setText(authUserName);
        mainMenuActivity.tShortUserLink.setText(getString(R.string.symbol_at) + authShortUserLink);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((InputMethodManager) mainMenuActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenuActivity.getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_channels));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String s) { return false; }

            @Override
            public boolean onQueryTextChange(String s) {
                channels.clear();

                ChildEventListener childEventListener = new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                        if (s.equals("")) {
                            for (int i = 0; i < channel.subscribers.size(); i++) {
                                if (channel.subscribers.get(i).equals(authId)) {
                                    channels.add(channel);
                                    break;
                                }
                            }
                        } else if (Pattern.compile(s, Pattern.CASE_INSENSITIVE).matcher(channel.title).find()) {
                            channel.body = getString(R.string.symbol_at) + channel.shortLink;
                            channels.add(channel);
                        }
                        tChannelsNumber.setText(dec(channels.size()));
                        channelsAdapter.notifyDataSetChanged();
                    }

                    @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
                };
                myDB.child("specter").child("channels").addChildEventListener(childEventListener);

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public String dec(int i) {
        return Locale.getDefault().getLanguage().equals("ru") ? pluralForm(i, getString(R.string.subscriber1), getString(R.string.subscribers2), getString(R.string.subscribers3)) : pluralForm(i, getString(R.string.subscriber1), getString(R.string.subscribers2));
    }

}