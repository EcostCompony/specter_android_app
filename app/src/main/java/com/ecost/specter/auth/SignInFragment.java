package com.ecost.specter.auth;

import static com.ecost.specter.Routing.showToastMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private EditText etPhoneNumber, etPassword;
    private AuthActivity authActivity;
    private Response response;
    private boolean passwordDisplay = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        etPhoneNumber = inflaterView.findViewById(R.id.input_phone_number);
        etPassword = inflaterView.findViewById(R.id.input_password);
        authActivity = (AuthActivity) requireActivity();

        etPhoneNumber.setFilters(new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
            return null;
        }, new InputFilter.LengthFilter(13) });
        etPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(128) });

        inflaterView.findViewById(R.id.hitbox_toggle_show).setOnClickListener(view -> {
            etPassword.setInputType(passwordDisplay ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inflaterView.findViewById(R.id.icon_toggle_show).setBackground(ContextCompat.getDrawable(authActivity, passwordDisplay ? R.drawable.icon_eye : R.drawable.icon_eye_slash));
            etPassword.setSelection(etPassword.getSelectionStart());
            passwordDisplay = !passwordDisplay;
        });

        etPassword.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) signIn(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_sign_in).setOnClickListener(this::signIn);

        inflaterView.findViewById(R.id.button_sign_up).setOnClickListener(view -> authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new PhoneNumberSignUpFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

    private void signIn(View view) {
        String phoneNumber = etPhoneNumber.getText().toString();
        String password = etPassword.getText().toString();

        if (phoneNumber.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_not_phone_number));
        else if (password.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_not_password));
        else if (phoneNumber.length() < 3) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_incorrect_phone_number));
        else if (password.length() < 8) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_small_password));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://thespecterlife.com:3500/api/auth/method/signin?v=1.0&service=specter&phone_number=" + phoneNumber + "&password=" + password).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        if (response.getError().getErrorCode() == 50) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_account_not_found));
                        else if (response.getError().getErrorCode() == 200) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_wrong_password));
                        else if (response.getError().getErrorCode() == 5) showToastMessage(authActivity, view, 2, getString(R.string.sign_in_error_too_often));
                        else showToastMessage(authActivity, view, 2, getString(R.string.unknown_error));
                    } else {
                        ConfirmCodeSignInFragment confirmCodeSignInFragment = new ConfirmCodeSignInFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("TOKEN", response.getRes().getConfirmToken());
                        confirmCodeSignInFragment.setArguments(bundle);
                        authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, confirmCodeSignInFragment).addToBackStack(null).commit();
                    }
                });
            }
        });
    }

}