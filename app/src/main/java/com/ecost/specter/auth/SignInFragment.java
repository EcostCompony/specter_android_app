package com.ecost.specter.auth;

import static com.ecost.specter.Routing.hash;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pushPreferenceAuth;
import static com.ecost.specter.Routing.pushPreferenceEcostId;
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

    EditText eNumberPhone, ePassword;
    FrameLayout fPassword;
    LinearLayout bHidePassword;
    View vHidePassword;
    Boolean passwordView = false;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        eNumberPhone = inflaterView.findViewById(R.id.input_number_phone);
        ePassword = inflaterView.findViewById(R.id.input_password);
        fPassword = inflaterView.findViewById(R.id.frame_input_password);
        bHidePassword = inflaterView.findViewById(R.id.button_hide_password);
        vHidePassword = inflaterView.findViewById(R.id.icon_hide_password);
        authActivity = (AuthActivity) requireActivity();

        eNumberPhone.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    eNumberPhone.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_error));
                    eNumberPhone.startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.input_shake));
                    return "";
                }
            }
            return null;
        }, new InputFilter.LengthFilter(13)});
        ePassword.setFilters(new InputFilter[] {new InputFilter.LengthFilter(128)});

        eNumberPhone.setOnKeyListener((view, keyCode, event) -> keyCode == KeyEvent.KEYCODE_ENTER && testNumberPhone(view, eNumberPhone.getText().toString()));

        ePassword.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) signIn(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        bHidePassword.setOnClickListener(view -> {
            int select = ePassword.getSelectionStart();
            ePassword.setInputType(passwordView ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            vHidePassword.setBackground(ContextCompat.getDrawable(authActivity, passwordView ? R.drawable.eye : R.drawable.eye_slash));
            ePassword.setSelection(select);
            passwordView = !passwordView;
        });

        inflaterView.findViewById(R.id.button_sign_in).setOnClickListener(this::signIn);

        inflaterView.findViewById(R.id.button_sign_up).setOnClickListener(view -> authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new NumberPhoneSignUpFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

    public void signIn(View view) {
        String numberPhone = eNumberPhone.getText().toString();
        String password = hash(ePassword.getText().toString());

        //noinspection StatementWithEmptyBody
        if (testNumberPhone(view, numberPhone)) { }
        else if (password.equals("")) authActivity.popupTwoInput(view, ePassword, eNumberPhone, getString(R.string.error_login_not_password), fPassword);
        else
            myDB.child("ecost").child("uid").child(numberPhone).child("id").get().addOnCompleteListener(taskId -> {
                String uid = String.valueOf(taskId.getResult().getValue());
                if (uid.equals("null")) authActivity.popupTwoInput(view, eNumberPhone, ePassword, getString(R.string.error_no_ph));
                else
                    myDB.child("ecost").child("users").child(uid).child("password").get().addOnCompleteListener(taskPassword -> {
                        if (!String.valueOf(taskPassword.getResult().getValue()).equals(password)) authActivity.popupTwoInput(view, ePassword, eNumberPhone, getString(R.string.error_sign_in_wrong_password), fPassword);
                        else
                            myDB.child("ecost").child("users").child(uid).child("services").child("specter").get().addOnCompleteListener(taskSpecterId -> {
                                String id = String.valueOf(taskSpecterId.getResult().getValue());
                                pushPreferenceEcostId(authActivity, Integer.parseInt(uid));
                                if (id.equals("null")) authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SpecterAuthFragment()).commit();
                                else
                                    myDB.child("specter").child("users").child(id).get().addOnCompleteListener(taskUser -> {
                                        User user = Objects.requireNonNull(taskUser.getResult().getValue(User.class));
                                        pushPreferenceAuth(authActivity, true);
                                        pushPreferenceId(authActivity, user.id);
                                        pushPreferenceUserName(authActivity, user.name);
                                        pushPreferenceShortUserLink(authActivity, user.link);
                                        startActivity(new Intent(authActivity, MainMenuActivity.class));
                                        authActivity.finish();
                                    });
                            });
                    });
            });
    }

    public boolean testNumberPhone(View view, String phone) {
        if (phone.equals("")) authActivity.popupTwoInput(view, eNumberPhone, ePassword, getString(R.string.error_signin_not_username));
        return phone.equals("");
    }

}