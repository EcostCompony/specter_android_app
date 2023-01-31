package com.ecost.specter.auth;

import static com.ecost.specter.Routing.myDB;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class PhoneNumberSignUpFragment extends Fragment {

    EditText eNumberPhone;
    Button bContinue;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_phone_number_sign_up, container, false);

        eNumberPhone = inflaterView.findViewById(R.id.input_number_phone);
        bContinue = inflaterView.findViewById(R.id.button_continue);
        authActivity = (AuthActivity) requireActivity();

        eNumberPhone.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    eNumberPhone.startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.input_shake));
                    eNumberPhone.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_error));
                    return "";
                }
            }
            return null;
        }, new InputFilter.LengthFilter(13)});

        eNumberPhone.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) next(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        bContinue.setOnClickListener(this::next);

        return inflaterView;
    }

    public void next(View view) {
        String numberPhone = eNumberPhone.getText().toString();

        if (numberPhone.equals("")) authActivity.popupOneInput(view, eNumberPhone, getString(R.string.phone_number_sign_up_error_not_username));
        else
            myDB.child("ecost").child("uid").child(numberPhone).get().addOnCompleteListener(task -> {
                if (task.getResult().getValue() != null) authActivity.popupOneInput(view, eNumberPhone, getString(R.string.phone_number_sign_up_error_already_phone_number));
                else {
                    authActivity.numberPhone = numberPhone;
                    authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new PasswordSignUpFragment()).addToBackStack(null).commit();
                }
            });
    }

}