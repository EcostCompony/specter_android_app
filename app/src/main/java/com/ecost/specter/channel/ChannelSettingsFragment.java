package com.ecost.specter.channel;

import static com.ecost.specter.Routing.sectionPosition;

import android.annotation.SuppressLint;
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

    private SectionsAdapter sectionsAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_settings, container, false);

        RecyclerView rvSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        sectionPosition = 0;
        getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new MainChannelSettingsFragment()).commit();

        rvSectionsList.setLayoutManager(new LinearLayoutManager(channelActivity, LinearLayoutManager.HORIZONTAL, false));
        sectionsAdapter = new SectionsAdapter(channelActivity, Arrays.asList(getResources().getStringArray(R.array.channel_settings_section)), position -> {
            if (sectionPosition == position) return;
            getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, (position == 0 ? new MainChannelSettingsFragment() : new SubscribersChannelSettingsFragment())).commit();
            sectionPosition = position;
            sectionsAdapter.notifyDataSetChanged();
        });
        rvSectionsList.setAdapter(sectionsAdapter);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}