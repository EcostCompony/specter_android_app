package com.ecost.specter.menu;

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

public class SettingsMenuFragment extends Fragment {

    private SectionsAdapter sectionsAdapter;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_settings_menu, container, false);

        RecyclerView rvSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        sectionPosition = 0;
        getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new AccountSettingsMenuFragment()).commit();

        rvSectionsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity, LinearLayoutManager.HORIZONTAL, false));
        sectionsAdapter = new SectionsAdapter(mainMenuActivity, Arrays.asList(getString(R.string.settings_menu_section_account), getString(R.string.settings_menu_section_app)), position -> {
            if (sectionPosition == position) return;
            getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, (position == 0 ? new AccountSettingsMenuFragment() : new AppSettingsMenuFragment())).commit();
            sectionPosition = position;
            sectionsAdapter.notifyDataSetChanged();
        });
        rvSectionsList.setAdapter(sectionsAdapter);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}