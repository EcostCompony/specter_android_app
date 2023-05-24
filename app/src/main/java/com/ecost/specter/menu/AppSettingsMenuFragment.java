/*package com.ecost.specter.menu;

import static com.ecost.specter.Routing.appLanguage;
import static com.ecost.specter.Routing.appTheme;
import static com.ecost.specter.Routing.changeLocale;
import static com.ecost.specter.Routing.pushPreferenceLanguage;
import static com.ecost.specter.Routing.pushPreferenceTheme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ecost.specter.R;

import java.util.Locale;

public class AppSettingsMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_app_settings_menu, container, false);

        Spinner sLanguages = inflaterView.findViewById(R.id.spinner_language);
        Spinner sThemes = inflaterView.findViewById(R.id.spinner_theme);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        ArrayAdapter<CharSequence> languagesAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.setting_array_language, R.layout.spinner_item);
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sLanguages.setAdapter(languagesAdapter);
        sLanguages.setSelection(appLanguage,false);
        sLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                changeLocale(mainMenuActivity, new Locale(position == 0 ? "ru" : "en"));
                pushPreferenceLanguage(mainMenuActivity, position);
                mainMenuActivity.recreate();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> themesAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.setting_array_theme, R.layout.spinner_item);
        themesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sThemes.setAdapter(themesAdapter);
        sThemes.setSelection(appTheme,false);
        sThemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                AppCompatDelegate.setDefaultNightMode(position == 0 ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM : (position == 1 ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES));
                pushPreferenceTheme(mainMenuActivity, position);
                mainMenuActivity.recreate();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        return inflaterView;
    }

}*/