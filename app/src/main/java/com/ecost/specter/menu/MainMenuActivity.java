package com.ecost.specter.menu;

import static com.ecost.specter.Routing.appLanguage;
import static com.ecost.specter.Routing.appTheme;
import static com.ecost.specter.Routing.changeLocale;
import static com.ecost.specter.Routing.pushPreferenceLanguage;
import static com.ecost.specter.Routing.pushPreferenceTheme;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ecost.specter.R;

import java.util.Locale;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int checkLanguage = 0;
    int checkTheme = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (getIntent().getBooleanExtra("CREATE", false)) {
            getIntent().putExtra("CREATE", false);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsMenuFragment()).commit();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getId() == R.id.spinner_language) {
            if (++checkLanguage <= 2 && !Objects.equals(appLanguage, getResources().getStringArray(R.array.setting_array_language)[0]) || checkLanguage <= 1) {
                if (Objects.equals(appLanguage, getResources().getStringArray(R.array.setting_array_language)[1])) adapterView.setSelection(1);
                return;
            }
            changeLocale(this, new Locale(adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_language)[0]) ? "ru" : "en"));
            pushPreferenceLanguage(this, getResources().getStringArray(R.array.setting_array_language)[adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_language)[0]) ? 0 : 1]);
        } else {
            if (++checkTheme <= 2 && !Objects.equals(appTheme, getResources().getStringArray(R.array.setting_array_theme)[0]) || checkTheme <= 1) {
                if (Objects.equals(appTheme, getResources().getStringArray(R.array.setting_array_theme)[1])) adapterView.setSelection(1);
                else if (Objects.equals(appTheme, getResources().getStringArray(R.array.setting_array_theme)[2])) adapterView.setSelection(2);
                return;
            }
            if (adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[2])) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else if (adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[1])) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            pushPreferenceTheme(this, getResources().getStringArray(R.array.setting_array_theme)[adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[0]) ? 0 : (adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_theme)[1]) ? 1 : 2)]);
        }
        recreate();
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}

}