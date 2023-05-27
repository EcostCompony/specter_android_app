package com.ecost.specter.auth;

import static com.ecost.specter.Routing.putAccessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ecost.specter.R;
import com.ecost.specter.Routing;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class SpecterStartFragment extends BottomSheetDialogFragment {

    private EditText etName, etShortLink;
    private AuthActivity authActivity;
    private Response response;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_specter_start, container, false);

        etName = inflaterView.findViewById(R.id.input_name);
        etShortLink = inflaterView.findViewById(R.id.input_short_link);
        authActivity = (AuthActivity) requireActivity();

        assert getArguments() != null;
        token = getArguments().getString("TOKEN", null);

        etName.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(16) });
        etShortLink.setFilters(new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(32) });

        etShortLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) start(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_start).setOnClickListener(this::start);

        return inflaterView;
    }

    private void start(View view) {
        String name = etName.getText().toString().trim();
        String shortLink = etShortLink.getText().toString();

        if (name.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.specter_start_error_not_name));
        else if (shortLink.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.specter_start_error_not_short_link));
        else if (shortLink.length() < 3) showToastMessage(authActivity, view, 2, getString(R.string.specter_start_error_small_short_link));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/auth?v=1.0&name=" + name + "&short_link=" + shortLink, token).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        if (response.getError().getErrorCode() == 1001) dismiss();
                        else if (response.getError().getErrorCode() == 51) showToastMessage(authActivity, view, 2, getString(R.string.specter_start_error_already_in_use));
                        else showToastMessage(authActivity, view, 2, getString(R.string.unknown_error));
                    } else {
                        putAccessToken(authActivity, response.getRes().getAccessToken());
                        startActivity(new Intent(authActivity, Routing.class));
                        authActivity.finish();
                    }
                });
            }
        });
    }

}