package com.ecost.specter.auth;

import static com.ecost.specter.Routing.hash;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pushPreferenceEcostId;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class PasswordSignUpFragment extends Fragment {

    EditText ePassword, eConfirmPassword;
    View bHidePassword, bHideConfirmPassword;
    FrameLayout fPassword, fConfirmPassword;
    Button bSignUp;
    Boolean passwordView = false;
    Boolean confirmPasswordView = false;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_password_sign_up, container, false);

        ePassword = inflaterView.findViewById(R.id.input_password);
        eConfirmPassword = inflaterView.findViewById(R.id.input_confirm_password);
        bHidePassword = inflaterView.findViewById(R.id.button_hide_password);
        bHideConfirmPassword = inflaterView.findViewById(R.id.button_hide_confirm_password);
        fPassword = inflaterView.findViewById(R.id.frame_input_password);
        fConfirmPassword = inflaterView.findViewById(R.id.frame_input_confirm_password);
        bSignUp = inflaterView.findViewById(R.id.button_sign_up);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterPassword = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-ZА-Я\\d_.%+@$#!-]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    (ePassword.hasFocus() ? eConfirmPassword : ePassword).setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth));
                    (ePassword.hasFocus() ? fPassword : fConfirmPassword).startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.shake));
                    (ePassword.hasFocus() ? ePassword : eConfirmPassword).setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_err));
                    return "";
                }
            }
            return null;
        };
        ePassword.setFilters(new InputFilter[] { filterPassword, new InputFilter.LengthFilter(128) });
        eConfirmPassword.setFilters(new InputFilter[] { filterPassword, new InputFilter.LengthFilter(128) });

        bSignUp.setOnClickListener(this::signUp);

        bHidePassword.setOnClickListener(view -> {
            if (passwordView) {
                ePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                bHidePassword.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.eye));
            } else {
                ePassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                bHidePassword.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.eye_slash));
            }
            ePassword.setSelection(ePassword.getSelectionStart());
            passwordView = !passwordView;
        });

        bHideConfirmPassword.setOnClickListener(view -> {
            if (confirmPasswordView) {
                eConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                bHideConfirmPassword.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.eye));
            } else {
                eConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                bHideConfirmPassword.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.eye_slash));
            }
            eConfirmPassword.setSelection(eConfirmPassword.getSelectionStart());
            confirmPasswordView = !confirmPasswordView;
        });

        eConfirmPassword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                signUp(getView());
                return true;
            }
            return false;
        });

        return inflaterView;
    }

    public void signUp(View view) {
        String password = ePassword.getText().toString();
        String confirmPassword = eConfirmPassword.getText().toString();

        myDB.child("ecost").child("users_number").get().addOnCompleteListener(task -> {
            Integer uid = Integer.parseInt(String.valueOf(task.getResult().getValue()))+1;

            if (password.equals("")) authActivity.popupTwoInput(view, getString(R.string.error_signup_not_password), ePassword, eConfirmPassword, fPassword);
            else if (confirmPassword.equals("")) authActivity.popupTwoInput(view, getString(R.string.error_signup_not_confirm_password), eConfirmPassword, ePassword, fConfirmPassword);
            else if (password.length() < 8) authActivity.popupTwoInput(view, getString(R.string.error_signup_short_password), ePassword, eConfirmPassword, fPassword);
            else if (!password.equals(confirmPassword)) authActivity.popupTwoInput(view, getString(R.string.error_signup_wrong_confirm_password), eConfirmPassword, ePassword, fConfirmPassword);
            else {
                myDB.child("ecost").child("users").child(String.valueOf(uid)).child("phone").setValue(authActivity.phone);
                myDB.child("ecost").child("users").child(String.valueOf(uid)).child("password").setValue(hash(password));
                myDB.child("ecost").child("uid").child(authActivity.phone).child("id").setValue(uid);
                myDB.child("ecost").child("users_number").setValue(uid);
                pushPreferenceEcostId(authActivity, uid);
                authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SpecterAuthFragment()).addToBackStack(null).commit();
            }
        });
    }

}