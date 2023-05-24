/*package com.ecost.specter.channel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.recyclers.SectionsAdapter;

import java.util.Arrays;

public class ChannelSettingsFragment extends Fragment {

    int settingPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_settings, container, false);

        RecyclerView rvSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new MainChannelSettingsFragment()).commit();

        rvSectionsList.setLayoutManager(new LinearLayoutManager(channelActivity, LinearLayoutManager.HORIZONTAL, false));
        rvSectionsList.setAdapter(new SectionsAdapter(channelActivity, Arrays.asList(getString(R.string.channel_settings_chapter_basic), getString(R.string.channel_settings_chapter_subscribers)), settingPosition, position -> {
            if (settingPosition == position) return;
            getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, position == 0 ? new MainChannelSettingsFragment() : new SubscribersChannelSettingsFragment()).commit();
            settingPosition = position;
        }));

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}*/