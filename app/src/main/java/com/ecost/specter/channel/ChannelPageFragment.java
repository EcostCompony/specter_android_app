package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Objects;

public class ChannelPageFragment extends Fragment {

    LinearLayout bBack, bChannelSettings, lDescription;
    FrameLayout bUnsubscribe, bSubscribe;
    TextView tTitle, tShortChannelLink, tChannelCategory, tChannelDescription;
    ChildEventListener childEventListener;
    ChannelActivity channelActivity;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_page, container, false);

        bBack = inflaterView.findViewById(R.id.button_back);
        tTitle = inflaterView.findViewById(R.id.channel_title);
        tChannelCategory = inflaterView.findViewById(R.id.channel_category);
        tShortChannelLink = inflaterView.findViewById(R.id.short_channel_link);
        tChannelDescription = inflaterView.findViewById(R.id.channel_text_description);
        bChannelSettings = inflaterView.findViewById(R.id.button_channel_settings);
        bUnsubscribe = inflaterView.findViewById(R.id.button_unsubscribe);
        bSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        lDescription = inflaterView.findViewById(R.id.channel_description_block);
        channelActivity = (ChannelActivity) requireActivity();

        tTitle.setText(channelActivity.channelTitle);
        tShortChannelLink.setText(getText(R.string.symbol_at) + channelActivity.channelShortLink);
        if (channelActivity.channelAdmin.equals(authId)) bChannelSettings.setVisibility(View.VISIBLE);
        if (channelActivity.categoryId != 0) {
            tChannelCategory.setText(getString(R.string.symbol_dot) + " " + getResources().getStringArray(R.array.channel_settings_array_category)[channelActivity.categoryId].toLowerCase());
            tChannelCategory.setVisibility(View.VISIBLE);
        }
        if (!channelActivity.channelAdmin.equals(authId)) (channelActivity.userSubscribe ? bUnsubscribe : bSubscribe).setVisibility(View.VISIBLE);
        if (channelActivity.channelDescription != null && !channelActivity.channelDescription.equals("")) {
            tChannelDescription.setText(channelActivity.channelDescription);
            lDescription.setVisibility(View.VISIBLE);
        }

        bBack.setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        bChannelSettings.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelSettingsFragment()).addToBackStack(null).commit());

        bUnsubscribe.setOnClickListener(view -> {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (Objects.equals(snapshot.getValue(Integer.class), authId)) {
                        assert previousChildName != null;
                        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").child(String.valueOf(snapshot.getKey())).setValue(0);
                        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(childEventListener);
                        bUnsubscribe.setVisibility(View.GONE);
                        bSubscribe.setVisibility(View.VISIBLE);
                        channelActivity.userSubscribe = false;
                    }
                }

                @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            };
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);
        });

        bSubscribe.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").child(String.valueOf(channelActivity.subNumber)).setValue(authId);
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subNumber").setValue(channelActivity.subNumber+1);
            channelActivity.userSubscribe = true;
            bSubscribe.setVisibility(View.GONE);
            bUnsubscribe.setVisibility(View.VISIBLE);
        });

        return inflaterView;
    }

}