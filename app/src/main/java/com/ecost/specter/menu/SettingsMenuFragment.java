package com.ecost.specter.menu;

import static com.ecost.specter.Routing.pushPreferenceSettingsSection;
import static com.ecost.specter.Routing.settingsSection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.recyclers.SectionsAdapter;

import java.util.Arrays;

public class SettingsMenuFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_settings_menu, container, false);

        RecyclerView rSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        rSectionsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity, LinearLayoutManager.HORIZONTAL, false));
        rSectionsList.setAdapter(new SectionsAdapter(mainMenuActivity, Arrays.asList(getString(R.string.settings_menu_section_account), getString(R.string.settings_menu_section_app), getString(R.string.settings_menu_section_ecost_account)), settingsSection, position -> {
            if (settingsSection == position) return;
            if (position == 0) getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new AccountSettingsMenuFragment()).commit();
            else if (position == 1) getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new AppSettingsMenuFragment()).commit();
            else getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new EcostSettingsMenuFragment()).commit();
            pushPreferenceSettingsSection(mainMenuActivity, position);
            mainMenuActivity.recreate();
        }));

        if (settingsSection == 0) getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new AccountSettingsMenuFragment()).commit();
        else if (settingsSection == 1) getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new AppSettingsMenuFragment()).commit();
        else getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, new EcostSettingsMenuFragment()).commit();

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}