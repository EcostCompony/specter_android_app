package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.channel.ChannelActivity;
import com.ecost.specter.models.Channel;
import com.ecost.specter.recyclers.ChannelsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class ChannelsSearchFragment extends Fragment {

    EditText eChannelTitle;
    CardView bClose;
    RecyclerView rChannelList;
    TextView tChannelsNumber;
    ChannelsAdapter channelsAdapter;
    List<Channel> channels = new ArrayList<>();
    MainMenuActivity mainMenuActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_search, container, false);

        eChannelTitle = inflaterView.findViewById(R.id.input_channel_title);
        bClose = inflaterView.findViewById(R.id.button_close);
        rChannelList = inflaterView.findViewById(R.id.recycler_channels_list);
        tChannelsNumber = inflaterView.findViewById(R.id.number_channels);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).hide();
        eChannelTitle.requestFocus();
        eChannelTitle.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mainMenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(eChannelTitle, InputMethodManager.SHOW_IMPLICIT);
        }, 1);

        rChannelList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, (channel, position) -> {
            Intent intent = new Intent(mainMenuActivity, ChannelActivity.class);
            intent.putExtra("CHANNEL_ID", channel.id);
            intent.putExtra("CHANNEL_ADMINS", channel.author);
            intent.putExtra("CHANNEL_POSTS_NUMBER", channel.postsNumber);
            intent.putExtra("CHANNEL_TITLE", channel.title);
            intent.putExtra("CHANNEL_SHORT_LINK", String.valueOf(channel.shortLink));
            intent.putExtra("USER_SUBSCRIBE", false);
            startActivity(intent);
        }, (channel, position) -> true);
        rChannelList.setAdapter(channelsAdapter);

        eChannelTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                channels.clear();
                ChildEventListener childEventListener = new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                        boolean subscribe = false;
                        if (!eChannelTitle.getText().toString().equals("") && Pattern.compile(eChannelTitle.getText().toString(), Pattern.CASE_INSENSITIVE).matcher(channel.title.toLowerCase()).find()) {
                            for (int i = 0; i < channel.subscribers.size(); i++) if (channel.subscribers.get(i).equals(authId)) subscribe = true;
                            if (!subscribe) {
                                channel.body = getString(R.string.symbol_at) + channel.shortLink;
                                channel.markBody = true;
                                channels.add(channel);
                            }
                        }
                        tChannelsNumber.setText(pluralForm(channelsAdapter.getItemCount(), getString(R.string.number_channels_nominative_case), getString(R.string.number_channels_genitive_case), getString(R.string.number_channels_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
                        channelsAdapter.notifyDataSetChanged();
                    }

                    @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) { }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                };
                myDB.child("specter").child("channels").addChildEventListener(childEventListener);
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        bClose.setOnClickListener(view -> {
            ((InputMethodManager) mainMenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            mainMenuActivity.navController.navigate(R.id.nav_channels);
        });

        return inflaterView;
    }

}