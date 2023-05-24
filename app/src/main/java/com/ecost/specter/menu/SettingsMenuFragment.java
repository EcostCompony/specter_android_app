/* package com.ecost.specter.menu;

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

        RecyclerView rvSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, (settingsSection == 0 ? new AccountSettingsMenuFragment() : (settingsSection == 1 ? new AppSettingsMenuFragment() : new EcostSettingsMenuFragment()))).commit();

        rvSectionsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity, LinearLayoutManager.HORIZONTAL, false));
        rvSectionsList.setAdapter(new SectionsAdapter(mainMenuActivity, Arrays.asList(getString(R.string.settings_menu_section_account), getString(R.string.settings_menu_section_app), getString(R.string.settings_menu_section_ecost_account)), settingsSection, position -> {
            if (settingsSection == position) return;
            getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container_view, (position == 0 ? new AccountSettingsMenuFragment() : (position == 1 ? new AppSettingsMenuFragment() : new EcostSettingsMenuFragment()))).commit();
            pushPreferenceSettingsSection(mainMenuActivity, position);
        }));

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

} */