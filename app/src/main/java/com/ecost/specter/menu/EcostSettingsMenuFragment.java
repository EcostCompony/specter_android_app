package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.hash;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceAuth;
import static com.ecost.specter.Routing.pushPreferenceEcostId;
import static com.ecost.specter.Routing.pushPreferenceId;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.pushPreferenceUserName;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecost.specter.R;
import com.ecost.specter.Routing;
import com.ecost.specter.auth.PasswordSignUpFragment;
import com.ecost.specter.auth.SpecterStartFragment;
import com.ecost.specter.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;
import java.util.regex.Pattern;

public class EcostSettingsMenuFragment extends Fragment {

    TextView tNumberPhone;
    String phoneNumber;
    MainMenuActivity mainMenuActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_ecost_settings_menu, container, false);

        tNumberPhone = inflaterView.findViewById(R.id.phone_number);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("phone").get().addOnCompleteListener(task -> {
            phoneNumber = task.getResult().getValue(String.class);
            tNumberPhone.setText(phoneNumber);
        });

        inflaterView.findViewById(R.id.setting_phone_number).setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.edit_alert_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());

            mDialogBuilder.setView(promptsView);

            TextView tHeader = (TextView) promptsView.findViewById(R.id.header);
            EditText eNewNumberPhone = (EditText) promptsView.findViewById(R.id.input_one);
            EditText ePass = (EditText) promptsView.findViewById(R.id.input_two);
            TextView tButtonOk = (TextView) promptsView.findViewById(R.id.button_okay);
            TextView tButtonCancel = (TextView) promptsView.findViewById(R.id.button_cancel);

            tHeader.setText(R.string.alert_dialog_edit_number_phone_header);
            eNewNumberPhone.setHint(R.string.alert_dialog_edit_number_phone_header_number_phone);
            ePass.setHint(R.string.alert_dialog_edit_number_phone_header_password);

            eNewNumberPhone.setInputType(3); // 3 - id Input type phone
            ePass.setInputType(129); // 129 - id Input type textPassword

            tButtonOk.setOnClickListener(view1 -> {
                myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("password").get().addOnCompleteListener(taskPassword -> {
                    if (!String.valueOf(taskPassword.getResult().getValue()).equals(hash(ePass.getText().toString()))) popup(mainMenuActivity, view, 1, getString(R.string.ecost_settings_menu_error_wrong_password));
                    else {
                        if (eNewNumberPhone.getText().toString().equals("")) popup(mainMenuActivity, view, 1 ,getString(R.string.phone_number_sign_up_error_not_username));
                        else
                            myDB.child("ecost").child("uid").child(eNewNumberPhone.getText().toString()).get().addOnCompleteListener(task -> {
                                if (task.getResult().getValue() != null) popup(mainMenuActivity, view, 1, getString(R.string.phone_number_sign_up_error_already_phone_number));
                                else {
                                    myDB.child("ecost").child("uid").child(phoneNumber).setValue(null);
                                    myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("phone").setValue(eNewNumberPhone.getText().toString());
                                    myDB.child("ecost").child("uid").child(eNewNumberPhone.getText().toString()).child("id").setValue(authEcostId);
                                    tNumberPhone.setText(eNewNumberPhone.getText().toString());
                                }
                            });
                    }
                });
            });

            AlertDialog alertDialog = mDialogBuilder.create();

            alertDialog.show();

            tButtonCancel.setOnClickListener(view1 -> alertDialog.cancel());
        });

        inflaterView.findViewById(R.id.setting_password).setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.edit_alert_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());

            mDialogBuilder.setView(promptsView);

            TextView tHeader = (TextView) promptsView.findViewById(R.id.header);
            EditText eNewPass = (EditText) promptsView.findViewById(R.id.input_one);
            EditText eOldPass = (EditText) promptsView.findViewById(R.id.input_two);
            TextView tButtonOk = (TextView) promptsView.findViewById(R.id.button_okay);
            TextView tButtonCancel = (TextView) promptsView.findViewById(R.id.button_cancel);

            tHeader.setText(R.string.alert_dialog_edit_password_header);
            eNewPass.setHint(R.string.aler_dialog_edit_password_header_new_password);
            eOldPass.setHint(R.string.aler_dialog_edit_password_header_old_password);

            eNewPass.setInputType(129);
            eOldPass.setInputType(129);

            InputFilter filterPassword = (source, start, end, dest, dstart, dend) -> {
                for (int i = start; i < end; i++) if (!Pattern.compile("^[A-ZА-Я\\d_.%+@$#!-]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                return null;
            };
            eNewPass.setFilters(new InputFilter[] {filterPassword, new InputFilter.LengthFilter(128)});

            tButtonOk.setOnClickListener(view1 -> {
                myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("password").get().addOnCompleteListener(taskPassword -> {
                    if (!String.valueOf(taskPassword.getResult().getValue()).equals(hash(eOldPass.getText().toString()))) popup(mainMenuActivity, view, 1, getString(R.string.ecost_settings_menu_error_wrong_password));
                    else {
                        if (eNewPass.getText().toString().equals("")) popup(mainMenuActivity, view, 1 , getString(R.string.ecost_settings_menu_error_not_password));
                        else if (eNewPass.getText().toString().length() < 8) popup(mainMenuActivity, view, 1 , getString(R.string.ecost_settings_menu_error_small_password));
                        else myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("password").setValue(hash(eNewPass.getText().toString()));
                    }
                });
            });

            AlertDialog alertDialog = mDialogBuilder.create();

            alertDialog.show();

            tButtonCancel.setOnClickListener(view1 -> alertDialog.cancel());
        });

        return inflaterView;
    }

}