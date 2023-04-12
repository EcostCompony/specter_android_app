package com.ecost.specter.menu;

import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.regex.Pattern;

public class ChannelsSearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_search, container, false);

        EditText etChannelSearch = inflaterView.findViewById(R.id.input_channel_title);
        RecyclerView rvChannelsList = inflaterView.findViewById(R.id.recycler_channels_list);
        List<Channel> channels = new ArrayList<>();
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        etChannelSearch.requestFocus();
        etChannelSearch.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mainMenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(etChannelSearch, InputMethodManager.SHOW_IMPLICIT);
        }, 1);

        rvChannelsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        ChannelsAdapter channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, mainMenuActivity::startChannel);
        rvChannelsList.setAdapter(channelsAdapter);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Channel channel = Objects.requireNonNull(dataSnapshot.getValue(Channel.class));
                String text = etChannelSearch.getText().toString().trim();
                if (!text.equals("") && (Pattern.compile(text, Pattern.CASE_INSENSITIVE).matcher(channel.title.toLowerCase()).find() || Pattern.compile(text, Pattern.CASE_INSENSITIVE).matcher(channel.shortLink.toLowerCase()).find())) {
                    channel.body = getString(R.string.symbol_at) + channel.shortLink;
                    channel.markBody = true;
                    channels.add(channel);
                    channelsAdapter.notifyItemInserted(channels.size()-1);
                }
                ((TextView) inflaterView.findViewById(R.id.number_channels)).setText(pluralForm(channelsAdapter.getItemCount(), getString(R.string.number_channels_nominative_case), getString(R.string.number_channels_genitive_case), getString(R.string.number_channels_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) { }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        etChannelSearch.addTextChangedListener(new TextWatcher() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                myDB.child("specter").child("channels").removeEventListener(childEventListener);
                channels.clear();
                channelsAdapter.notifyDataSetChanged();
                myDB.child("specter").child("channels").addChildEventListener(childEventListener);
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}