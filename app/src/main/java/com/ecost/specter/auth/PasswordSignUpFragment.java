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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class PasswordSignUpFragment extends Fragment {

    EditText ePassword, eConfirmPassword;
    FrameLayout fPassword, fConfirmPassword;
    LinearLayout bHidePassword, bHideConfirmPassword;
    View vHidePassword, vHideConfirmPassword;
    Boolean passwordView = false;
    Boolean confirmPasswordView = false;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_password_sign_up, container, false);

        ePassword = inflaterView.findViewById(R.id.input_password);
        eConfirmPassword = inflaterView.findViewById(R.id.input_confirm_password);
        fPassword = inflaterView.findViewById(R.id.frame_input_password);
        fConfirmPassword = inflaterView.findViewById(R.id.frame_input_confirm_password);
        bHidePassword = inflaterView.findViewById(R.id.button_hide_password);
        bHideConfirmPassword = inflaterView.findViewById(R.id.button_hide_confirm_password);
        vHidePassword = inflaterView.findViewById(R.id.icon_hide_password);
        vHideConfirmPassword = inflaterView.findViewById(R.id.icon_hide_confirm_password);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterPassword = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-ZА-Я\\d_.%+@$#!-]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    (ePassword.hasFocus() ? eConfirmPassword : ePassword).setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth));
                    (ePassword.hasFocus() ? fPassword : fConfirmPassword).startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.input_shake));
                    (ePassword.hasFocus() ? ePassword : eConfirmPassword).setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_error));
                    return "";
                }
            }
            return null;
        };
        ePassword.setFilters(new InputFilter[] {filterPassword, new InputFilter.LengthFilter(128)});
        eConfirmPassword.setFilters(new InputFilter[] {filterPassword, new InputFilter.LengthFilter(128)});

        eConfirmPassword.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) signUp(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        View.OnClickListener eyeClickListener = view -> {
            EditText editText = view == bHidePassword ? ePassword : eConfirmPassword;
            View icon = view == bHidePassword ? vHidePassword : vHideConfirmPassword;
            Boolean pView = view == bHidePassword ? passwordView : confirmPasswordView;
            int select = editText.getSelectionStart();

            editText.setInputType(pView ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setBackground(ContextCompat.getDrawable(authActivity, pView ? R.drawable.icon_eye : R.drawable.icon_eye_slash));
            editText.setSelection(select);
            if (view == bHidePassword) passwordView = !pView;
            else confirmPasswordView = !pView;
        };
        bHidePassword.setOnClickListener(eyeClickListener);
        bHideConfirmPassword.setOnClickListener(eyeClickListener);

        inflaterView.findViewById(R.id.button_sign_up).setOnClickListener(this::signUp);

        return inflaterView;
    }

    public void signUp(View view) {
        String password = ePassword.getText().toString();
        String confirmPassword = eConfirmPassword.getText().toString();

        myDB.child("ecost").child("users_number").get().addOnCompleteListener(task -> {
            Integer uid = Integer.parseInt(String.valueOf(task.getResult().getValue()))+1;

            if (password.equals("")) authActivity.popupTwoInput(view, ePassword, eConfirmPassword, getString(R.string.password_sign_up_error_not_password), fPassword);
            else if (confirmPassword.equals("")) authActivity.popupTwoInput(view, eConfirmPassword, ePassword, getString(R.string.password_sign_up_error_not_confirm_password), fConfirmPassword);
            else if (password.length() < 8) authActivity.popupTwoInput(view, ePassword, eConfirmPassword, getString(R.string.password_sign_up_error_short_password), fPassword);
            else if (!password.equals(confirmPassword)) authActivity.popupTwoInput(view, eConfirmPassword, ePassword, getString(R.string.password_sign_up_error_wrong_confirm_password), fConfirmPassword);
            else {
                myDB.child("ecost").child("users").child(String.valueOf(uid)).child("phone").setValue(authActivity.numberPhone);
                myDB.child("ecost").child("users").child(String.valueOf(uid)).child("password").setValue(hash(password));
                myDB.child("ecost").child("uid").child(authActivity.numberPhone).child("id").setValue(uid);
                myDB.child("ecost").child("users_number").setValue(uid);
                pushPreferenceEcostId(authActivity, uid);
                authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SpecterAuthFragment()).addToBackStack(null).commit();
            }
        });
    }

}