package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;

public class ChannelPageFragment extends Fragment {

    LinearLayout bBack, bChannelSettings, lDescription;
    TextView tTitle, tShortChannelLink, tChannelCategory, tChannelDescription;
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
        lDescription = inflaterView.findViewById(R.id.channel_description_block);
        channelActivity = (ChannelActivity) requireActivity();

        tTitle.setText(channelActivity.channelTitle);
        tShortChannelLink.setText(getText(R.string.symbol_at) + channelActivity.channelShortLink);
        if (channelActivity.channelAdmin.equals(authId)) bChannelSettings.setVisibility(View.VISIBLE);
        if (channelActivity.categoryId != 0) {
            tChannelCategory.setText(getString(R.string.symbol_dot) + " " + getResources().getStringArray(R.array.channel_settings_array_category)[channelActivity.categoryId].toLowerCase());
            tChannelCategory.setVisibility(View.VISIBLE);
        }
        if (channelActivity.channelDescription != null) {
            tChannelDescription.setText(channelActivity.channelDescription);
            lDescription.setVisibility(View.VISIBLE);
        }

        bBack.setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        bChannelSettings.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelSettingsFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

}