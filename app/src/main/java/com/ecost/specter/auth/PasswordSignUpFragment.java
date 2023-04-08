package com.ecost.specter.auth;

import static com.ecost.specter.Routing.hash;
import static com.ecost.specter.Routing.myDB;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class PasswordSignUpFragment extends Fragment {

    EditText etPassword, etConfirmPassword;
    FrameLayout flPassword, flConfirmPassword;
    LinearLayout bHidePassword, bHideConfirmPassword;
    View vHidePassword, vHideConfirmPassword;
    Boolean passwordView = false;
    Boolean confirmPasswordView = false;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_password_sign_up, container, false);

        etPassword = inflaterView.findViewById(R.id.input_password);
        etConfirmPassword = inflaterView.findViewById(R.id.input_confirm_password);
        flPassword = inflaterView.findViewById(R.id.frame_input_password);
        flConfirmPassword = inflaterView.findViewById(R.id.frame_input_confirm_password);
        bHidePassword = inflaterView.findViewById(R.id.button_hide_password);
        bHideConfirmPassword = inflaterView.findViewById(R.id.button_hide_confirm_password);
        vHidePassword = inflaterView.findViewById(R.id.icon_hide_password);
        vHideConfirmPassword = inflaterView.findViewById(R.id.icon_hide_confirm_password);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterPassword = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-ZА-Я\\d_.%+@$#!-]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    (etPassword.hasFocus() ? etConfirmPassword : etPassword).setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth));
                    (etPassword.hasFocus() ? flPassword : flConfirmPassword).startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.input_shake));
                    (etPassword.hasFocus() ? etPassword : etConfirmPassword).setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_error));
                    return "";
                }
            }
            return null;
        };
        etPassword.setFilters(new InputFilter[]{filterPassword, new InputFilter.LengthFilter(128)});
        etConfirmPassword.setFilters(new InputFilter[]{filterPassword, new InputFilter.LengthFilter(128)});

        View.OnClickListener hideClickListener = view -> {
            EditText editText = view == bHidePassword ? etPassword : etConfirmPassword;
            View icon = view == bHidePassword ? vHidePassword : vHideConfirmPassword;
            Boolean pView = view == bHidePassword ? passwordView : confirmPasswordView;

            editText.setInputType(pView ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setBackground(ContextCompat.getDrawable(authActivity, pView ? R.drawable.icon_eye : R.drawable.icon_eye_slash));
            editText.setSelection(editText.getSelectionStart());
            if (view == bHidePassword) passwordView = !pView;
            else confirmPasswordView = !pView;
        };
        bHidePassword.setOnClickListener(hideClickListener);
        bHideConfirmPassword.setOnClickListener(hideClickListener);

        etConfirmPassword.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) signUp(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_sign_up).setOnClickListener(this::signUp);

        return inflaterView;
    }

    public void signUp(View view) {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        myDB.child("ecost").child("users_number").get().addOnCompleteListener(task -> {
            Integer uid = Integer.parseInt(String.valueOf(task.getResult().getValue()))+1;

            if (password.equals("") || confirmPassword.equals("")) authActivity.popupInput(view, etPassword, etConfirmPassword, password.equals("") ? getString(R.string.password_sign_up_error_not_password) : getString(R.string.password_sign_up_error_not_confirm_password), password.equals("") ? flPassword : flConfirmPassword);
            else if (password.length() < 8) authActivity.popupInput(view, etPassword, etConfirmPassword, getString(R.string.password_sign_up_error_short_password), flPassword);
            else if (!password.equals(confirmPassword)) authActivity.popupInput(view, etConfirmPassword, etPassword, getString(R.string.password_sign_up_error_wrong_confirm_password), flConfirmPassword);
            else {
                myDB.child("ecost").child("users").child(String.valueOf(uid)).child("phone").setValue(authActivity.phoneNumber);
                myDB.child("ecost").child("users").child(String.valueOf(uid)).child("password").setValue(hash(password));
                myDB.child("ecost").child("uid").child(authActivity.phoneNumber).child("id").setValue(uid);
                myDB.child("ecost").child("users_number").setValue(uid);
                authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SignInFragment()).commit();
                new SpecterStartFragment().show(authActivity.getSupportFragmentManager(), new SpecterStartFragment().getTag());
            }
        });
    }

}