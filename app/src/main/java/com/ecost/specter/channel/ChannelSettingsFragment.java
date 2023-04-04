package com.ecost.specter.channel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.menu.AccountSettingsMenuFragment;
import com.ecost.specter.recyclers.SectionsAdapter;

import java.util.Arrays;

public class ChannelSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_settings, container, false);

        RecyclerView rSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new MainChannelSettingsFragment()).commit();
        channelActivity.settingPosition = 0;

        rSectionsList.setLayoutManager(new LinearLayoutManager(channelActivity, LinearLayoutManager.HORIZONTAL, false));
        rSectionsList.setAdapter(new SectionsAdapter(channelActivity, Arrays.asList("Основное", "Подписчики"), channelActivity.settingPosition, position -> {
            if (channelActivity.settingPosition == position) return;
            if (position == 0) getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new MainChannelSettingsFragment()).commit();
            else if (position == 1) getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new SubscribersChannelSettingsFragment()).commit();
            channelActivity.settingPosition = position;
        }));

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}