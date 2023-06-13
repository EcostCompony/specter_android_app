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
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class PasswordSignUpFragment extends Fragment {

    private EditText etPassword, etConfirmPassword;
    private AuthActivity authActivity;
    private Response response;
    private boolean passwordDisplay = false;
    private boolean confirmPasswordDisplay = false;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_password_sign_up, container, false);

        etPassword = inflaterView.findViewById(R.id.input_password);
        etConfirmPassword = inflaterView.findViewById(R.id.input_confirm_password);
        LinearLayout llToggleShowPassword = inflaterView.findViewById(R.id.hitbox_toggle_show);
        LinearLayout llToggleShowConfirmPassword = inflaterView.findViewById(R.id.hitbox_toggle_show2);
        authActivity = (AuthActivity) requireActivity();

        assert getArguments() != null;
        token = getArguments().getString("TOKEN");

        InputFilter[] inputFilters = new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) if (!Pattern.compile("^[A-ZА-Я\\d_.%+@$#!-]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
            return null;
        }, new InputFilter.LengthFilter(128) };
        etPassword.setFilters(inputFilters);
        etConfirmPassword.setFilters(inputFilters);

        View.OnClickListener onClickListener = view -> {
            EditText editText = view == llToggleShowPassword ? etPassword : etConfirmPassword;
            View icon = view == llToggleShowPassword ? inflaterView.findViewById(R.id.icon_toggle_show) : inflaterView.findViewById(R.id.icon_toggle_show2);
            boolean display = view == llToggleShowPassword ? passwordDisplay : confirmPasswordDisplay;

            editText.setInputType(display ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setBackground(ContextCompat.getDrawable(authActivity, display ? R.drawable.icon_eye : R.drawable.icon_eye_slash));
            editText.setSelection(editText.getSelectionStart());
            if (view == llToggleShowPassword) passwordDisplay = !display;
            else confirmPasswordDisplay = !display;
        };
        llToggleShowPassword.setOnClickListener(onClickListener);
        llToggleShowConfirmPassword.setOnClickListener(onClickListener);

        etConfirmPassword.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) signUp(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_sign_up).setOnClickListener(this::signUp);

        return inflaterView;
    }

    private void signUp(View view) {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (password.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.password_sign_up_error_not_password));
        else if (confirmPassword.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.password_sign_up_error_not_confirm_password));
        else if (password.length() < 8) showToastMessage(authActivity, view, 2, getString(R.string.password_sign_up_error_small_password));
        else if (!password.equals(confirmPassword)) showToastMessage(authActivity, view, 2, getString(R.string.password_sign_up_error_different_passwords));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://thespecterlife.com:3500/api/auth/method/signup?v=1.0&password=" + password, token).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(authActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SignInFragment()).commit();
                        SpecterStartFragment specterStartFragment = new SpecterStartFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("TOKEN", response.getRes().getServiceAuthToken());
                        specterStartFragment.setArguments(bundle);
                        specterStartFragment.show(authActivity.getSupportFragmentManager(), specterStartFragment.getTag());
                    }
                });
            }
        });
    }

}