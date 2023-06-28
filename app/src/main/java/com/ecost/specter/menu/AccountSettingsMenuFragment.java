package com.ecost.specter.menu;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.putAccessToken;
import static com.ecost.specter.Routing.putUserName;
import static com.ecost.specter.Routing.putUserShortLink;
import static com.ecost.specter.Routing.showToastMessage;
import static com.ecost.specter.Routing.userName;
import static com.ecost.specter.Routing.userShortLink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;
import com.ecost.specter.auth.AuthActivity;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class AccountSettingsMenuFragment extends Fragment {

    private EditText etName, etShortLink;
    private ImageButton ibSaveName, ibSaveShortLink;
    private MainMenuActivity mainMenuActivity;
    private Response response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_account_settings_menu, container, false);

        etName = inflaterView.findViewById(R.id.input_name);
        etShortLink = inflaterView.findViewById(R.id.input_short_link);
        ibSaveName = inflaterView.findViewById(R.id.button_save_name);
        ibSaveShortLink = inflaterView.findViewById(R.id.button_save_short_link);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        etName.setText(userName);
        etShortLink.setText(userShortLink);

        etName.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(64) });
        etShortLink.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(32) });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String name = etName.getText().toString().trim();
                String shortLink = etShortLink.getText().toString();

                ibSaveName.setVisibility(name.equals(userName) || name.equals("") ? View.GONE : View.VISIBLE);
                ibSaveShortLink.setVisibility(shortLink.equals(userShortLink) || shortLink.length() < 4 || !Pattern.compile("^[a-z][a-z\\d_.]{2,30}[a-z\\d]$", Pattern.CASE_INSENSITIVE).matcher(shortLink).find() || (float) Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE).matcher(shortLink).replaceAll("").length() / (float) shortLink.length() * 100 > 40 ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        etName.addTextChangedListener(textWatcher);
        etShortLink.addTextChangedListener(textWatcher);

        etName.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etName.getText().toString().trim().equals(userName)) saveName(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        etShortLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etShortLink.getText().toString().equals(userShortLink)) saveShortLink(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        ibSaveName.setOnClickListener(this::saveName);
        ibSaveShortLink.setOnClickListener(this::saveShortLink);

        inflaterView.findViewById(R.id.button_sign_out).setOnClickListener(view -> {
            putAccessToken(mainMenuActivity, null);
            startActivity(new Intent(mainMenuActivity, AuthActivity.class));
            mainMenuActivity.finish();
        });

        return inflaterView;
    }

    private void saveName(View view) {
        String name = etName.getText().toString().trim();

        if (name.equals("")) showToastMessage(mainMenuActivity, view, 2, getString(R.string.account_settings_menu_error_not_name));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://95.163.236.254:3501/api/method/account.edit?v=0.7&name=" + name, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(mainMenuActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        putUserName(mainMenuActivity, name);
                        ibSaveName.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void saveShortLink(View view) {
        String shortLink = etShortLink.getText().toString();

        if (shortLink.equals("")) showToastMessage(mainMenuActivity, view, 2, getString(R.string.account_settings_menu_error_not_short_link));
        else if (shortLink.length() < 4) showToastMessage(mainMenuActivity, view, 2, getString(R.string.account_settings_menu_error_small_short_link));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://95.163.236.254:3501/api/method/account.edit?v=0.7&short_link=" + shortLink, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        if (response.getError().getCode() == 51) showToastMessage(mainMenuActivity, view, 2, getString(R.string.account_settings_menu_error_already_in_use));
                        else showToastMessage(mainMenuActivity, view, 2, getString(R.string.unknown_error));
                    } else {
                        putUserShortLink(mainMenuActivity, shortLink);
                        ibSaveShortLink.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

}