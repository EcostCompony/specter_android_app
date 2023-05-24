/*package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Objects;

public class ChannelPageFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_page, container, false);

        LinearLayout bChannelSettings = inflaterView.findViewById(R.id.button_channel_settings);
        FrameLayout bUnsubscribe = inflaterView.findViewById(R.id.button_unsubscribe);
        FrameLayout bSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        ((TextView) inflaterView.findViewById(R.id.channel_title)).setText(channelActivity.channelTitle);
        ((TextView) inflaterView.findViewById(R.id.short_channel_link)).setText(getText(R.string.symbol_at) + channelActivity.shortChannelLink);
        if (channelActivity.userAdmin) bChannelSettings.setVisibility(View.VISIBLE);
        if (channelActivity.channelCategoryId != 0) ((TextView) inflaterView.findViewById(R.id.channel_category)).setText(getString(R.string.symbol_dot) + " " + getResources().getStringArray(R.array.channel_settings_array_category)[channelActivity.channelCategoryId].toLowerCase());
        if (!channelActivity.userAdmin) (channelActivity.userSubscribe ? bUnsubscribe : bSubscribe).setVisibility(View.VISIBLE);
        if (channelActivity.channelDescription != null && !channelActivity.channelDescription.equals("")) {
            ((TextView) inflaterView.findViewById(R.id.channel_text_description)).setText(channelActivity.channelDescription);
            inflaterView.findViewById(R.id.channel_description_block).setVisibility(View.VISIBLE);
        }

        inflaterView.findViewById(R.id.button_back).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        bChannelSettings.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelSettingsFragment()).addToBackStack(null).commit());

        bUnsubscribe.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").child(channelActivity.userSubscriberId).setValue(null);
            bUnsubscribe.setVisibility(View.GONE);
            bSubscribe.setVisibility(View.VISIBLE);
            channelActivity.userSubscribe = false;
        });

        bSubscribe.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").push().setValue(new User(authId, authEcostId, authUserName, authShortUserLink));
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    if (Objects.requireNonNull(dataSnapshot.getValue(User.class)).getId().equals(authId)) {
                        channelActivity.userSubscriberId = dataSnapshot.getKey();
                        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(this);
                    }
                }

                @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
                @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListener);
            channelActivity.userSubscribe = true;
            bSubscribe.setVisibility(View.GONE);
            bUnsubscribe.setVisibility(View.VISIBLE);
        });

        return inflaterView;
    }

}*/