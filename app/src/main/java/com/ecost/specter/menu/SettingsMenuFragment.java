package com.ecost.specter.menu;

import static com.ecost.specter.Routing.appLanguage;
import static com.ecost.specter.Routing.appTheme;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.changeLocale;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceLanguage;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.pushPreferenceTheme;
import static com.ecost.specter.Routing.pushPreferenceUserName;
import static com.ecost.specter.Routing.signOut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.auth.AuthActivity;
import com.ecost.specter.recyclers.SectionsAdapter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class SettingsMenuFragment extends Fragment {

    EditText eUserName, eShortUserLink;
    ImageButton bSaveUserName, bSaveShortUserLink;
    Spinner sLanguage, sTheme;
    int checkLanguage = 0;
    int checkTheme = 0;
    MainMenuActivity mainMenuActivity;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_settings_menu, container, false);

        RecyclerView rSectionsList = inflaterView.findViewById(R.id.recycler_sections_list);
        eUserName = inflaterView.findViewById(R.id.input_user_name);
        eShortUserLink = inflaterView.findViewById(R.id.input_short_user_link);
        bSaveUserName = inflaterView.findViewById(R.id.button_save_user_name);
        bSaveShortUserLink = inflaterView.findViewById(R.id.button_save_short_user_link);
        sLanguage = inflaterView.findViewById(R.id.spinner_language);
        sTheme = inflaterView.findViewById(R.id.spinner_theme);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        eUserName.setText(authUserName);
        eShortUserLink.setText(authShortUserLink);

        rSectionsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity, LinearLayoutManager.HORIZONTAL, false));
        rSectionsList.setAdapter(new SectionsAdapter(mainMenuActivity, Arrays.asList(getString(R.string.settings_menu_section_account), getString(R.string.settings_menu_section_app))));

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

        eUserName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
        eShortUserLink.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                String j = String.valueOf(source.charAt(i));
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(j).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return j.toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        eUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveUserName.setVisibility(eUserName.getText().toString().equals(authUserName) ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        eShortUserLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveShortUserLink.setVisibility(eShortUserLink.getText().toString().equals(authShortUserLink) ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        eUserName.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !eUserName.getText().toString().equals(authUserName)) saveUserName(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        eShortUserLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !eShortUserLink.getText().toString().equals(authShortUserLink)) saveShortUserLink(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        bSaveUserName.setOnClickListener(this::saveUserName);

        bSaveShortUserLink.setOnClickListener(this::saveShortUserLink);

        inflaterView.findViewById(R.id.button_sign_out).setOnClickListener(view -> {
            signOut(mainMenuActivity);
            startActivity(new Intent(mainMenuActivity, AuthActivity.class));
            mainMenuActivity.finish();
        });

        return inflaterView;
    }

    public void saveUserName(View view) {
        String userName = eUserName.getText().toString();

        if (userName.equals("")) popup(mainMenuActivity, view, getString(R.string.settings_menu_error_not_name));
        else {
            myDB.child("specter").child("users").child(String.valueOf(authId)).child("name").setValue(userName);
            pushPreferenceUserName(mainMenuActivity, userName);
            bSaveUserName.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void saveShortUserLink(View view) {
        String shortUserLink = eShortUserLink.getText().toString();

        if (shortUserLink.equals("")) popup(mainMenuActivity, view, getString(R.string.settings_menu_error_not_short_user_link));
        else if (shortUserLink.length() < 3) popup(mainMenuActivity, view, getString(R.string.settings_menu_error_small_short_user_link));
        else
            myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortUserLink -> {
                if (taskTestShortUserLink.getResult().getValue() != null) popup(mainMenuActivity, view, getString(R.string.settings_menu_error_busy_short_user_link));
                else {
                    myDB.child("specter").child("uid").child(authShortUserLink.replace(".", "*")).setValue(null);
                    myDB.child("specter").child("users").child(String.valueOf(authId)).child("link").setValue(shortUserLink);
                    myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").setValue(authId);
                    myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("type").setValue("user");
                    pushPreferenceShortUserLink(mainMenuActivity, shortUserLink);
                    bSaveShortUserLink.setVisibility(View.GONE);
                }
            });
    }

}