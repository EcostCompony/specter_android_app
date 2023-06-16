package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ChannelPageFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_page, container, false);

        ImageButton ibSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        ImageButton ibChannelSettings = inflaterView.findViewById(R.id.button_channel_settings);
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        ((TextView) inflaterView.findViewById(R.id.title)).setText(channelActivity.channelTitle);
        ((TextView) inflaterView.findViewById(R.id.short_link)).setText(getText(R.string.symbol_at) + channelActivity.channelShortLink);
        ((TextView) inflaterView.findViewById(R.id.number_subscribers)).setText(pluralForm(channelActivity.channelSubscriberNumbers, getResources().getStringArray(R.array.subscribers)));
        if (channelActivity.userSubscribe) ibSubscribe.setImageResource(R.drawable.icon_profile_delete);
        if (channelActivity.userAdmin) {
            ibChannelSettings.setVisibility(View.VISIBLE);
            ibSubscribe.setVisibility(View.GONE);
        }
        if (channelActivity.channelDescription != null) {
            inflaterView.findViewById(R.id.info_description).setVisibility(View.VISIBLE);
            ((TextView) inflaterView.findViewById(R.id.description)).setText(channelActivity.channelDescription);
        }
        if (channelActivity.channelDescription != null && channelActivity.channelCategory != 0) inflaterView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
        if (channelActivity.channelCategory != 0) {
            inflaterView.findViewById(R.id.info_category).setVisibility(View.VISIBLE);
            ((TextView) inflaterView.findViewById(R.id.category)).setText(getResources().getStringArray(R.array.categories_array)[channelActivity.channelCategory]);
        }

        inflaterView.findViewById(R.id.header).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        ibSubscribe.setOnClickListener(view -> Executors.newSingleThreadExecutor().execute(() -> {
            try {
                new API((channelActivity.userSubscribe ? "http://thespecterlife.com:3501/api/method/channels.unsubscribe?v=1.0&channel_id=" : "http://thespecterlife.com:3501/api/method/channels.subscribe?v=1.0&channel_id=") + channelActivity.channelId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    channelActivity.userSubscribe = !channelActivity.userSubscribe;
                    ibSubscribe.setImageResource(channelActivity.userSubscribe ? R.drawable.icon_profile_delete : R.drawable.icon_profile_add);
                });
            }
        }));

        ibChannelSettings.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelSettingsFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

}