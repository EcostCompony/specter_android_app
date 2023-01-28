package com.ecost.specter.channel;

import static com.ecost.specter.Routing.myDB;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ecost.specter.R;

public class MainMenuSettingsFragment extends Fragment {

    ChannelSettingsFragment channelSettingsFragment;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_main_menu_settings, container, false);

        channelSettingsFragment = (ChannelSettingsFragment) requireParentFragment();
        channelSettingsFragment.bDeleteChannel = inflaterView.findViewById(R.id.button_delete_channel);
        channelSettingsFragment.eTitle = inflaterView.findViewById(R.id.edit_title);
        channelSettingsFragment.eLink = inflaterView.findViewById(R.id.edit_link);
        channelActivity = (ChannelActivity) requireActivity();

        channelSettingsFragment.eTitle.setText(channelActivity.channelTitle);
        channelSettingsFragment.eLink.setText(channelActivity.channelLink);

        channelSettingsFragment.bDeleteChannel.setOnClickListener(view -> {
            channelSettingsFragment.channels.remove(channelActivity.channelId);
            myDB.child("channels").setValue(channelSettingsFragment.channels);
            myDB.child("count_vlogs").setValue(channelSettingsFragment.channels.size());
            channelActivity.finish();
        });

        return inflaterView;
    }
}