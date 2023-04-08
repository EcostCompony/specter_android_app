package com.ecost.specter.auth;

import static com.ecost.specter.Routing.hash;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pushPreferenceAuth;
import static com.ecost.specter.Routing.pushPreferenceId;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.pushPreferenceUserName;

import android.content.Intent;
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

import com.ecost.specter.menu.MainMenuActivity;
import com.ecost.specter.R;
import com.ecost.specter.models.User;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    EditText etPhoneNumber, etPassword;
    FrameLayout flPassword;
    LinearLayout bHidePassword;
    View vHidePassword;
    Boolean passwordView = false;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        etPhoneNumber = inflaterView.findViewById(R.id.input_number_phone);
        etPassword = inflaterView.findViewById(R.id.input_password);
        flPassword = inflaterView.findViewById(R.id.frame_input_password);
        bHidePassword = inflaterView.findViewById(R.id.button_hide_password);
        vHidePassword = inflaterView.findViewById(R.id.icon_hide_password);
        authActivity = (AuthActivity) requireActivity();

        etPhoneNumber.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    etPhoneNumber.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_error));
                    etPhoneNumber.startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.input_shake));
                    return "";
                }
            }
            return null;
        }, new InputFilter.LengthFilter(13)});
        etPassword.setFilters(new InputFilter[] {new InputFilter.LengthFilter(128)});

        etPhoneNumber.setOnKeyListener((view, keyCode, event) -> keyCode == KeyEvent.KEYCODE_ENTER && testPhoneNumber(view, etPhoneNumber.getText().toString()));

        bHidePassword.setOnClickListener(view -> {
            etPassword.setInputType(passwordView ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            vHidePassword.setBackground(ContextCompat.getDrawable(authActivity, passwordView ? R.drawable.icon_eye : R.drawable.icon_eye_slash));
            etPassword.setSelection(etPassword.getSelectionStart());
            passwordView = !passwordView;
        });

        etPassword.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) signIn(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_sign_in).setOnClickListener(this::signIn);

        inflaterView.findViewById(R.id.button_sign_up).setOnClickListener(view -> authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new PhoneNumberSignUpFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

    public void signIn(View view) {
        String phoneNumber = etPhoneNumber.getText().toString();
        String password = hash(etPassword.getText().toString());

        //noinspection StatementWithEmptyBody
        if (testPhoneNumber(view, phoneNumber)) { }
        else if (password.equals("")) authActivity.popupInput(view, etPassword, etPhoneNumber, getString(R.string.sign_in_error_not_password), flPassword);
        else
            myDB.child("ecost").child("uid").child(phoneNumber).child("id").get().addOnCompleteListener(taskId -> {
                String uid = String.valueOf(taskId.getResult().getValue());
                if (uid.equals("null")) authActivity.popupInput(view, etPhoneNumber, etPassword, getString(R.string.sign_in_error_not_account));
                else
                    myDB.child("ecost").child("users").child(uid).child("password").get().addOnCompleteListener(taskPassword -> {
                        if (!String.valueOf(taskPassword.getResult().getValue()).equals(password)) authActivity.popupInput(view, etPassword, etPhoneNumber, getString(R.string.sign_in_error_wrong_password), flPassword);
                        else
                            myDB.child("ecost").child("users").child(uid).child("services").child("specter").get().addOnCompleteListener(taskSpecterId -> {
                                String id = String.valueOf(taskSpecterId.getResult().getValue());
                                if (id.equals("null")) new SpecterStartFragment().show(authActivity.getSupportFragmentManager(), new SpecterStartFragment().getTag());
                                else
                                    myDB.child("specter").child("users").child(id).get().addOnCompleteListener(taskUser -> {
                                        User user = Objects.requireNonNull(taskUser.getResult().getValue(User.class));
                                        pushPreferenceAuth(authActivity, true);
                                        pushPreferenceId(authActivity, user.id);
                                        pushPreferenceUserName(authActivity, user.name);
                                        pushPreferenceShortUserLink(authActivity, user.link);
                                        startActivity(new Intent(authActivity, MainMenuActivity.class).putExtra("CREATE", true));
                                        authActivity.finish();
                                    });
                            });
                    });
            });
    }

    public boolean testPhoneNumber(View view, String phone) {
        if (phone.equals("")) authActivity.popupInput(view, etPhoneNumber, etPassword, getString(R.string.sign_in_error_not_phone_number));
        return phone.equals("");
    }

}