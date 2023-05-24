/*package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.pushPreferenceUserName;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class AccountSettingsMenuFragment extends Fragment {

    EditText etUserName, etShortUserLink;
    ImageButton bSaveUserName, bSaveShortUserLink;
    MainMenuActivity mainMenuActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_account_settings_menu, container, false);

        etUserName = inflaterView.findViewById(R.id.input_user_name);
        etShortUserLink = inflaterView.findViewById(R.id.input_short_user_link);
        bSaveUserName = inflaterView.findViewById(R.id.button_save_user_name);
        bSaveShortUserLink = inflaterView.findViewById(R.id.button_save_short_user_link);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        etUserName.setText(authUserName);
        etShortUserLink.setText(authShortUserLink);

        etUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        etShortUserLink.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveUserName.setVisibility(etUserName.getText().toString().trim().equals(authUserName) ? View.GONE : View.VISIBLE);
                bSaveShortUserLink.setVisibility(etShortUserLink.getText().toString().equals(authShortUserLink) ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        etUserName.addTextChangedListener(textWatcher);
        etShortUserLink.addTextChangedListener(textWatcher);

        etUserName.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etUserName.getText().toString().trim().equals(authUserName)) saveUserName(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });
        etShortUserLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etShortUserLink.getText().toString().equals(authShortUserLink)) saveShortUserLink(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        bSaveUserName.setOnClickListener(this::saveUserName);
        bSaveShortUserLink.setOnClickListener(this::saveShortUserLink);

        return inflaterView;
    }

    public void saveUserName(View view) {
        String userName = etUserName.getText().toString().trim();
        if (userName.equals("")) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_not_name));
        else {
            myDB.child("specter").child("users").child(String.valueOf(authId)).child("name").setValue(userName);
            pushPreferenceUserName(mainMenuActivity, userName);
            bSaveUserName.setVisibility(View.GONE);
        }
    }

    public void saveShortUserLink(View view) {
        String shortUserLink = etShortUserLink.getText().toString();
        if (shortUserLink.equals("")) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_not_short_user_link));
        else if (shortUserLink.length() < 3) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_small_short_user_link));
        else
            myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").get().addOnCompleteListener(task -> {
                if (task.getResult().getValue() != null) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_busy_short_user_link));
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

}*/