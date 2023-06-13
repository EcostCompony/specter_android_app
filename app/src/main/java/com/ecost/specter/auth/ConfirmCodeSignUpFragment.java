package com.ecost.specter.auth;

import static com.ecost.specter.Routing.showToastMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ConfirmCodeSignUpFragment extends Fragment {

    private EditText etCode;
    private AuthActivity authActivity;
    private Response response;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_confirm_code_sign_up, container, false);

        etCode = inflaterView.findViewById(R.id.input_code);
        authActivity = (AuthActivity) requireActivity();

        assert getArguments() != null;
        token = getArguments().getString("TOKEN");

        etCode.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(4) });

        etCode.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) confirm(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_confirm).setOnClickListener(this::confirm);

        return inflaterView;
    }

    private void confirm(View view) {
        String code = etCode.getText().toString();

        if (code.length() != 4) showToastMessage(authActivity, view, 2, getString(R.string.confirm_code_sign_up_error_incorrect_code));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://thespecterlife.com:3500/api/auth/method/signup.checkConfirmCode?v=1.0&code=" + code, token).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        if (response.getError().getErrorCode() == 101) showToastMessage(authActivity, view, 2, getString(R.string.confirm_code_sign_up_error_wrong_code));
                        else showToastMessage(authActivity, view, 2, getString(R.string.unknown_error));
                    } else {
                        PasswordSignUpFragment passwordSignUpFragment = new PasswordSignUpFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("TOKEN", response.getRes().getSignupToken());
                        passwordSignUpFragment.setArguments(bundle);
                        authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, passwordSignUpFragment).addToBackStack(null).commit();
                    }
                });
            }
        });
    }

}