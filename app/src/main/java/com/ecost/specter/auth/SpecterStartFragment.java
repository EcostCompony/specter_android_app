package com.ecost.specter.auth;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceAuth;
import static com.ecost.specter.Routing.pushPreferenceId;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.pushPreferenceUserName;

import android.content.Intent;
import android.os.Bundle;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ecost.specter.R;
import com.ecost.specter.menu.MainMenuActivity;
import com.ecost.specter.models.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.regex.Pattern;

public class SpecterStartFragment extends BottomSheetDialogFragment {

    EditText eName, eShortUserLink;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_specter_start, container, false);

        eName = inflaterView.findViewById(R.id.input_user_name);
        eShortUserLink = inflaterView.findViewById(R.id.input_short_user_link);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterShortUserLink = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        };
        eName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
        eShortUserLink.setFilters(new InputFilter[] { filterShortUserLink, new InputFilter.LengthFilter(16) });

        inflaterView.findViewById(R.id.button_start).setOnClickListener(view -> {
            String name = eName.getText().toString();
            String shortUserLink = eShortUserLink.getText().toString();

            if (name.equals("")) popup(authActivity, view, 1, "error");
            else if (shortUserLink.equals("")) popup(authActivity, view, 1, "error");
            else if (shortUserLink.length() < 3) popup(authActivity, view, 1, "ссылка маленькая");
            else
                myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortLink -> {
                    if (taskTestShortLink.getResult().getValue() != null) popup(authActivity, view, 1, "ссылка занята");
                    else
                        myDB.child("specter").child("users_number").get().addOnCompleteListener(taskId -> {
                            Integer uid = Integer.parseInt(String.valueOf(taskId.getResult().getValue()))+1;
                            myDB.child("specter").child("users").child(String.valueOf(uid)).setValue(new User(uid, authEcostId, name, shortUserLink));
                            myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("id").setValue(uid);
                            myDB.child("specter").child("uid").child(shortUserLink.replace('.', '*')).child("type").setValue("user");
                            myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("services").child("specter").setValue(uid);
                            myDB.child("specter").child("users_number").setValue(uid);
                            pushPreferenceAuth(authActivity, true);
                            pushPreferenceId(authActivity, uid);
                            pushPreferenceUserName(authActivity, name);
                            pushPreferenceShortUserLink(authActivity, shortUserLink);
                            Intent intent = new Intent(authActivity, MainMenuActivity.class);
                            intent.putExtra("CREATE", true);
                            startActivity(intent);
                            authActivity.finish();
                        });
                });
        });

        return inflaterView;
    }

}