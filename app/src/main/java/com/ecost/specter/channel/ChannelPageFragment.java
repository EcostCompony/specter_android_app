package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;

public class ChannelPageFragment extends Fragment {

    LinearLayout bBack;
    TextView tTitle, tLink;
    View bChannelSettings;
    Button bSubscribe;
    boolean subscribe = false;
    ChannelActivity channelActivity;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_page, container, false);

        bBack = inflaterView.findViewById(R.id.back);
        tTitle = inflaterView.findViewById(R.id.title);
        tLink = inflaterView.findViewById(R.id.url);
        bChannelSettings = inflaterView.findViewById(R.id.channel_settings);
        bSubscribe = inflaterView.findViewById(R.id.subscribe);
        channelActivity = (ChannelActivity) requireActivity();

        tTitle.setText(channelActivity.channelTitle);
        tLink.setText(getText(R.string.symbol_at) + channelActivity.channelLink);
        for (int i = 0; i < channelActivity.subscribers.size(); i++) if (channelActivity.subscribers.get(i) == authId) subscribe(R.drawable.unsub, R.string.button_unsubscribe, R.color.red, true);
        if (channelActivity.channelAdmin.equals(authId)) {
            bSubscribe.setVisibility(View.GONE);
            bChannelSettings.setVisibility(View.VISIBLE);
        }

        bBack.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).addToBackStack(null).commit());

        bSubscribe.setOnClickListener(view -> {
            if (subscribe) {
                for (int i = 0; i < channelActivity.subscribers.size(); i++) {
                    if (channelActivity.subscribers.get(i) == authId) {
                        channelActivity.subscribers.remove(i);
                        myDB.child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").setValue(channelActivity.subscribers);
                        break;
                    }
                }
                subscribe(R.drawable.button_neutral, R.string.button_subscribe, R.color.background, false);
            } else {
                subscribe(R.drawable.unsub, R.string.button_unsubscribe, R.color.red, true);
                myDB.child("channels").child(channelActivity.channelId).child("subscribers").child(String.valueOf(channelActivity.subscribers.size())).setValue(authId);
            }
        });

        bChannelSettings.setOnClickListener(view -> channelActivity.startChannelSettingsFragment());

        return inflaterView;
    }

    public void subscribe(int drawable, int string, int color, boolean sub) {
        bSubscribe.setBackground(ContextCompat.getDrawable(channelActivity, drawable));
        bSubscribe.setText(string);
        bSubscribe.setTextColor(getResources().getColor(color));
        subscribe = sub;
    }

}