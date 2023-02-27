package com.ecost.specter.menu;

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
import java.util.Objects;

public class AppSettingsMenuFragment extends Fragment {

    Spinner sLanguage, sTheme;
    int checkLanguage = 0;
    int checkTheme = 0;
    MainMenuActivity mainMenuActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_app_settings_menu, container, false);

        sLanguage = inflaterView.findViewById(R.id.spinner_language);
        sTheme = inflaterView.findViewById(R.id.spinner_theme);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.setting_array_language, R.layout.spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sLanguage.setAdapter(languageAdapter);
        sLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (++checkLanguage <= 2 && !Objects.equals(appLanguage, getResources().getStringArray(R.array.setting_array_language)[0]) || checkLanguage <= 1) {
                    if (Objects.equals(appLanguage, getResources().getStringArray(R.array.setting_array_language)[1])) adapterView.setSelection(1);
                    return;
                }
                changeLocale(mainMenuActivity, new Locale(adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_language)[0]) ? "ru" : "en"));
                pushPreferenceLanguage(mainMenuActivity, getResources().getStringArray(R.array.setting_array_language)[adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_language)[0]) ? 0 : 1]);
                mainMenuActivity.recreate();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.setting_array_theme, R.layout.spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTheme.setAdapter(themeAdapter);
        sTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (++checkTheme <= 2 && !Objects.equals(appTheme, getResources().getStringArray(R.array.setting_array_theme)[0]) || checkTheme <= 1) {
                    if (Objects.equals(appTheme, getResources().getStringArray(R.array.setting_array_theme)[1])) adapterView.setSelection(1);
                    else if (Objects.equals(appTheme, getResources().getStringArray(R.array.setting_array_theme)[2])) adapterView.setSelection(2);
                    return;
                }
                if (adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[2])) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else if (adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[1])) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                pushPreferenceTheme(mainMenuActivity, getResources().getStringArray(R.array.setting_array_theme)[adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[0]) ? 0 : (adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[1]) ? 1 : 2)]);
                mainMenuActivity.recreate();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        return inflaterView;
    }

}