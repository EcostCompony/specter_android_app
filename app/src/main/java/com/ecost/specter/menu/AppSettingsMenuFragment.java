package com.ecost.specter.menu;

import static com.ecost.specter.Routing.appLanguage;
import static com.ecost.specter.Routing.appTheme;
import static com.ecost.specter.Routing.putAppLanguage;
import static com.ecost.specter.Routing.putAppTheme;
import static com.ecost.specter.Routing.showToastMessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;

public class AppSettingsMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_app_settings_menu, container, false);

        Spinner sLanguages = inflaterView.findViewById(R.id.spinner_languages);
        Spinner sThemes = inflaterView.findViewById(R.id.spinner_themes);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        ArrayAdapter<CharSequence> languagesAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.languages_array, R.layout.spinner_item);
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sLanguages.setAdapter(languagesAdapter);
        sLanguages.setSelection(appLanguage, false);
        sLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                putAppLanguage(mainMenuActivity, position);
                showToastMessage(mainMenuActivity, view, 1, getString(R.string.app_settings_menu_message_change_language));
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> themesAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.themes_array, R.layout.spinner_item);
        themesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sThemes.setAdapter(themesAdapter);
        sThemes.setSelection(appTheme, false);
        sThemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                putAppTheme(mainMenuActivity, position);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        return inflaterView;
    }

}