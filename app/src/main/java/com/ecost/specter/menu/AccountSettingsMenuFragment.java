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
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.auth.AuthActivity;

import java.util.regex.Pattern;

public class AccountSettingsMenuFragment extends Fragment {

    EditText eUserName, eShortUserLink;
    ImageButton bSaveUserName, bSaveShortUserLink;
    MainMenuActivity mainMenuActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_account_settings_menu, container, false);

        eUserName = inflaterView.findViewById(R.id.input_user_name);
        eShortUserLink = inflaterView.findViewById(R.id.input_short_user_link);
        bSaveUserName = inflaterView.findViewById(R.id.button_save_user_name);
        bSaveShortUserLink = inflaterView.findViewById(R.id.button_save_short_user_link);
        mainMenuActivity = (MainMenuActivity) requireActivity();

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
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.agree_alert_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());

            mDialogBuilder.setView(promptsView);

            TextView tHeader = promptsView.findViewById(R.id.header);
            TextView description = promptsView.findViewById(R.id.description);

            tHeader.setText(R.string.account_settings_log_out_text_header);
            description.setText(R.string.account_settings_log_out_text_description);

            promptsView.findViewById(R.id.button_yes).setOnClickListener(view1 -> {
                signOut(mainMenuActivity);
                startActivity(new Intent(mainMenuActivity, AuthActivity.class));
                mainMenuActivity.finish();
            });

            AlertDialog alertDialog = mDialogBuilder.create();

            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();

            promptsView.findViewById(R.id.button_cancel).setOnClickListener(view1 -> alertDialog.cancel());
        });

        return inflaterView;
    }

    public void saveUserName(View view) {
        String userName = eUserName.getText().toString();

        if (userName.equals("")) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_not_name));
        else {
            myDB.child("specter").child("users").child(String.valueOf(authId)).child("name").setValue(userName);
            pushPreferenceUserName(mainMenuActivity, userName);
            bSaveUserName.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void saveShortUserLink(View view) {
        String shortUserLink = eShortUserLink.getText().toString();

        if (shortUserLink.equals("")) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_not_short_user_link));
        else if (shortUserLink.length() < 3) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_small_short_user_link));
        else
            myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortUserLink -> {
                if (taskTestShortUserLink.getResult().getValue() != null) popup(mainMenuActivity, view, 1, getString(R.string.account_settings_error_busy_short_user_link));
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