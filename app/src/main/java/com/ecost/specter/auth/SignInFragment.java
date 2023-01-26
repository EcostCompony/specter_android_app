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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ecost.specter.menu.MainMenuActivity;
import com.ecost.specter.R;
import com.ecost.specter.models.User;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    EditText ePhone, ePassword;
    View bHidePassword;
    FrameLayout fInputPassword;
    Button bSignIn, bSignUp;
    Boolean passwordView = false;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        ePhone = inflaterView.findViewById(R.id.input_phone);
        ePassword = inflaterView.findViewById(R.id.input_password);
        bHidePassword = inflaterView.findViewById(R.id.button_hide_password);
        fInputPassword = inflaterView.findViewById(R.id.frame_input_password);
        bSignIn = inflaterView.findViewById(R.id.button_sign_in);
        bSignUp = inflaterView.findViewById(R.id.button_sign_up);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterPhone = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("\\d", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) {
                    ePhone.startAnimation(AnimationUtils.loadAnimation(authActivity, R.anim.shake));
                    ePhone.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.input_auth_err));
                    return "";
                }
            }
            return null;
        };
        ePhone.setFilters(new InputFilter[] { filterPhone, new InputFilter.LengthFilter(13) });
        ePassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(128) });

        bSignIn.setOnClickListener(this::signIn);
        bSignUp.setOnClickListener(view -> authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new PhoneSignUpFragment()).addToBackStack(null).commit());

        bHidePassword.setOnClickListener(view -> {
            int select = ePassword.getSelectionStart();
            if (!passwordView) {
                ePassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ePassword.setSelection(select);
                bHidePassword.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.eye_slash));
                passwordView = true;
            } else {
                ePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ePassword.setSelection(select);
                bHidePassword.setBackground(ContextCompat.getDrawable(authActivity, R.drawable.eye));
                passwordView = false;
            }
        });

        ePhone.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) return testPhone(getView(), ePhone.getText().toString());
            return false;
        });

        ePassword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                signIn(getView());
                return true;
            }
            return false;
        });

        return inflaterView;
    }

    public void signIn(View view) {
        String phone = ePhone.getText().toString();
        String password = hash(ePassword.getText().toString());

        if (testPhone(view, phone)) { }
        else if (password.equals("")) authActivity.popupTwoInput(view, getString(R.string.error_login_not_password), ePassword, ePhone, fInputPassword);
        else
            myDB.child("ecost").child("uid").child(phone).child("id").get().addOnCompleteListener(taskId -> {
                String uid = String.valueOf(taskId.getResult().getValue());
                if (uid.equals("null")) authActivity.popupTwoInput(view, getString(R.string.error_no_ph), ePhone, ePassword);
                else
                    myDB.child("ecost").child("users").child(uid).child("password").get().addOnCompleteListener(taskPassword -> {
                        if (!String.valueOf(taskPassword.getResult().getValue()).equals(password)) authActivity.popupTwoInput(view, getString(R.string.error_sign_in_wrong_password), ePassword, ePhone, fInputPassword);
                        else
                            myDB.child("ecost").child("users").child(uid).child("services").child("specter").get().addOnCompleteListener(taskSpecterId -> {
                                String id = String.valueOf(taskSpecterId.getResult().getValue());
                                if (id.equals("null")) {
                                    pushPreferenceEcostId(authActivity, Integer.parseInt(uid));
                                    authActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SpecterAuthFragment()).addToBackStack(null).commit();
                                } else
                                    myDB.child("specter").child("users").child(id).get().addOnCompleteListener(taskName -> {
                                        User user = Objects.requireNonNull(taskName.getResult().getValue(User.class));
                                        pushPreferenceAuth(authActivity, true);
                                        pushPreferenceId(authActivity, user.id);
                                        pushPreferenceEcostId(authActivity, user.ecost_id);
                                        pushPreferenceUserName(authActivity, user.name);
                                        pushPreferenceShortUserLink(authActivity, user.link);
                                        startActivity(new Intent(authActivity, MainMenuActivity.class));
                                        authActivity.finish();
                                    });
                            });
                    });
            });
    }

    public boolean testPhone(View view, String phone) {
        if (phone.equals("")) {
            authActivity.popupTwoInput(view, getString(R.string.error_signin_not_username), ePhone, ePassword);
            return true;
        }
        return false;
    }

}