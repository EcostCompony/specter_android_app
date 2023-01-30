package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
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
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.auth.AuthActivity;
import com.ecost.specter.databinding.FragmentSettingsMenuBinding;

import java.util.Objects;
import java.util.regex.Pattern;

public class SettingsMenuFragment extends Fragment {

    EditText eUserName, eShortUserLink;
    ImageButton bSaveUserName, bSaveShortUserLink;
    MainMenuActivity mainMenuActivity;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSettingsMenuBinding binding = FragmentSettingsMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eUserName = binding.inputUserName;
        eShortUserLink = binding.inputShortUserLink;
        bSaveUserName = binding.buttonSaveUserName;
        bSaveShortUserLink = binding.buttonSaveShortUserLink;
        mainMenuActivity = (MainMenuActivity) requireActivity();

        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).hide();
        eUserName.setText(authUserName);
        eShortUserLink.setText(authShortUserLink);

        eUserName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
        eShortUserLink.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                String j = String.valueOf(source.charAt(i));
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(j).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return j.toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        binding.buttonClose.setOnClickListener(view -> mainMenuActivity.navController.navigate(R.id.nav_channels));

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

        binding.buttonSignOut.setOnClickListener(view -> {
            signOut(mainMenuActivity);
            startActivity(new Intent(mainMenuActivity, AuthActivity.class));
            mainMenuActivity.finish();
        });

        return root;
    }

    public void saveUserName(View view) {
        String userName = eUserName.getText().toString();

        if (userName.equals("")) popup(mainMenuActivity, view, getString(R.string.settings_menu_error_not_name));
        else {
            myDB.child("specter").child("users").child(String.valueOf(authId)).child("name").setValue(userName);
            pushPreferenceUserName(mainMenuActivity, userName);
            mainMenuActivity.tUserName.setText(userName);
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
                if (taskTestShortUserLink.getResult().getValue() != null) popup(mainMenuActivity, view, getString(R.string.settings_menu_error_use_short_user_link));
                else {
                    myDB.child("specter").child("uid").child(authShortUserLink.replace(".", "*")).setValue(null);
                    myDB.child("specter").child("users").child(String.valueOf(authId)).child("link").setValue(shortUserLink);
                    myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").setValue(authId);
                    myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("type").setValue("user");
                    pushPreferenceShortUserLink(mainMenuActivity, shortUserLink);
                    mainMenuActivity.tShortUserLink.setText(getString(R.string.symbol_at) + authShortUserLink);
                    bSaveShortUserLink.setVisibility(View.GONE);
                }
            });
    }

}