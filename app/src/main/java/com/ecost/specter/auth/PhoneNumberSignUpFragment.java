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
import java.util.regex.Pattern;

public class PhoneNumberSignUpFragment extends Fragment {

    private EditText etPhoneNumber;
    private AuthActivity authActivity;
    private Response response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_phone_number_sign_up, container, false);

        etPhoneNumber = inflaterView.findViewById(R.id.input_phone_number);
        authActivity = (AuthActivity) requireActivity();

        etPhoneNumber.setFilters(new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
            return null;
        }, new InputFilter.LengthFilter(13) });

        etPhoneNumber.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) next(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_continue).setOnClickListener(this::next);

        return inflaterView;
    }

    private void next(View view) {
        String phoneNumber = etPhoneNumber.getText().toString();

        if (phoneNumber.equals("")) showToastMessage(authActivity, view, 2, getString(R.string.phone_number_sign_up_error_not_phone_number));
        else if (phoneNumber.length() < 3) showToastMessage(authActivity, view, 2, getString(R.string.phone_number_sign_up_error_incorrect_phone_number));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://95.163.236.254:3500/api/auth/method/signup.confirmPhoneNumber?v=1.0&service=specter&phone_number=" + phoneNumber).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        if (response.getError().getErrorCode() == 51) showToastMessage(authActivity, view, 2, getString(R.string.phone_number_sign_up_error_already_in_use));
                        else if (response.getError().getErrorCode() == 5) showToastMessage(authActivity, view, 2, getString(R.string.phone_number_sign_up_error_too_often));
                        else if (response.getError().getErrorCode() == 100) showToastMessage(authActivity, view, 2, getString(R.string.phone_number_sign_up_error_incorrect_phone_number));
                        else showToastMessage(authActivity, view, 2, getString(R.string.unknown_error));
                    } else {
                        ConfirmCodeSignUpFragment confirmCodeSignUpFragment = new ConfirmCodeSignUpFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("TOKEN", response.getRes().getConfirmToken());
                        confirmCodeSignUpFragment.setArguments(bundle);
                        authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, confirmCodeSignUpFragment).addToBackStack(null).commit();
                    }
                });
            }
        });
    }

}