package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.hash;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.signOut;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.auth.AuthActivity;

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

            tHeader.setText(R.string.edit_number_phone_alert_dialog_header);
            eNewNumberPhone.setHint(R.string.edit_number_phone_alert_dialog_hint_number_phone);
            ePass.setHint(R.string.edit_number_phone_alert_dialog_hint_password);

            eNewNumberPhone.setInputType(3); // 3 - id Input type phone
            ePass.setInputType(129); // 129 - id Input type textPassword

            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            promptsView.findViewById(R.id.button_okay).setOnClickListener(view1 -> {
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
                alertDialog.cancel();
            });

            alertDialog.show();

            promptsView.findViewById(R.id.button_cancel).setOnClickListener(view1 -> alertDialog.cancel());
        });

        inflaterView.findViewById(R.id.setting_password).setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.edit_alert_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
            mDialogBuilder.setView(promptsView);

            TextView tHeader = (TextView) promptsView.findViewById(R.id.header);
            EditText eNewPass = (EditText) promptsView.findViewById(R.id.input_one);
            EditText eOldPass = (EditText) promptsView.findViewById(R.id.input_two);

            eNewPass.setInputType(129);
            eOldPass.setInputType(129);

            tHeader.setText(R.string.edit_password_alert_dialog_header);
            eNewPass.setHint(R.string.edit_password_alert_dialog_hint_new_password);
            eOldPass.setHint(R.string.edit_password_alert_dialog_hint_old_password);

            InputFilter filterPassword = (source, start, end, dest, dstart, dend) -> {
                for (int i = start; i < end; i++) if (!Pattern.compile("^[A-ZА-Я\\d_.%+@$#!-]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                return null;
            };
            eNewPass.setFilters(new InputFilter[] {filterPassword, new InputFilter.LengthFilter(128)});

            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            promptsView.findViewById(R.id.button_okay).setOnClickListener(view1 -> {
                myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("password").get().addOnCompleteListener(taskPassword -> {
                    if (!String.valueOf(taskPassword.getResult().getValue()).equals(hash(eOldPass.getText().toString()))) popup(mainMenuActivity, view, 1, getString(R.string.ecost_settings_menu_error_wrong_password));
                    else {
                        if (eNewPass.getText().toString().equals("")) popup(mainMenuActivity, view, 1 , getString(R.string.ecost_settings_menu_error_not_password));
                        else if (eNewPass.getText().toString().length() < 8) popup(mainMenuActivity, view, 1 , getString(R.string.ecost_settings_menu_error_small_password));
                        else myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("password").setValue(hash(eNewPass.getText().toString()));
                    }
                });
                alertDialog.cancel();
            });

            alertDialog.show();

            promptsView.findViewById(R.id.button_cancel).setOnClickListener(view1 -> alertDialog.cancel());
        });

        inflaterView.findViewById(R.id.button_sign_out).setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.agree_alert_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
            mDialogBuilder.setView(promptsView);


            TextView tHeader = promptsView.findViewById(R.id.header);
            TextView description = promptsView.findViewById(R.id.description);

            tHeader.setText(R.string.account_settings_log_out_text_header);
            description.setText(R.string.account_settings_log_out_text_description);

            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            promptsView.findViewById(R.id.button_yes).setOnClickListener(view1 -> {
                signOut(mainMenuActivity);
                startActivity(new Intent(mainMenuActivity, AuthActivity.class));
                mainMenuActivity.finish();
                alertDialog.cancel();
            });

            alertDialog.show();

            promptsView.findViewById(R.id.button_cancel).setOnClickListener(view1 -> alertDialog.cancel());
        });

        return inflaterView;
    }

}