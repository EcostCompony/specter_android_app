package com.ecost.specter.channel;

import static com.ecost.specter.Routing.myDB;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ecost.specter.R;
import com.ecost.specter.models.FullChannel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ChannelSettingsFragment extends Fragment {

    Button bDeleteChannel;
    EditText eTitle, eLink;
    CardView bClose;
    List<FullChannel> channels = new ArrayList<>();
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_settings, container, false);

        bClose = inflaterView.findViewById(R.id.close);
        channelActivity = (ChannelActivity) requireActivity();

        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainMenuSettingsFragment()).commit();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                channels.add(dataSnapshot.getValue(FullChannel.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (!Objects.equals(dataSnapshot.getKey(), channelActivity.channelId)) channels.remove(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())));
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("channels").addChildEventListener(childEventListener);

        bClose.setOnClickListener(view -> {
            channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelPageFragment()).commit();
            String link = eLink.getText().toString().trim();
            if (!eTitle.getText().toString().trim().equals("") && !link.equals("") && Pattern.compile("^[A-Z0-9_]+$", Pattern.CASE_INSENSITIVE).matcher(link).find()) {
                myDB.child("channels").child(String.valueOf(channelActivity.channelId)).child("title").setValue(eTitle.getText().toString().trim());
                myDB.child("channels").child(String.valueOf(channelActivity.channelId)).child("link").setValue(link);
                channelActivity.channelTitle = eTitle.getText().toString().trim();
                channelActivity.channelShortLink = link;
            }
        });

        return inflaterView;
    }

}