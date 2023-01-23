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

public class PhoneSignUpFragment extends Fragment {

    EditText ePhone;
    Button bContinue;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_phone_sign_up, container, false);

        ePhone = inflaterView.findViewById(R.id.input_phone);
        bContinue = inflaterView.findViewById(R.id.button_continue);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterNumber = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    ePhone.startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.shake));
                    ePhone.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_err));
                    return "";
                }
            }
            return null;
        };
        ePhone.setFilters(new InputFilter[] { filterNumber, new InputFilter.LengthFilter(13) });

        bContinue.setOnClickListener(this::next);

        ePhone.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                next(getView());
                return true;
            }
            return false;
        });

        return inflaterView;
    }

    public void next(View view) {
        String phone = ePhone.getText().toString();

        if (phone.equals("")) authActivity.popupOneInput(view, getString(R.string.error_sign_up_no_username), ePhone);
        else
            myDB.child("ecost").child("uid").child(phone).get().addOnCompleteListener(task -> {
                if (task.getResult().getValue() != null) authActivity.popupOneInput(view, getString(R.string.error_sign_up_already_username), ePhone);
                else {
                    authActivity.phone = phone;
                    authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new PasswordSignUpFragment()).addToBackStack(null).commit();
                }
            });
    }

}