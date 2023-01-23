package com.ecost.specter.auth;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceAuth;
import static com.ecost.specter.Routing.pushPreferenceId;
import static com.ecost.specter.Routing.pushPreferenceName;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.signOut;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ecost.specter.R;
import com.ecost.specter.menu.MainMenuActivity;
import com.ecost.specter.models.User;

import java.util.regex.Pattern;

public class SpecterAuthFragment extends Fragment {

    EditText eName, eShortLink;
    Button bSignOut, bSignIn;
    AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_specter_auth, container, false);

        eName = inflaterView.findViewById(R.id.edit_name);
        eShortLink = inflaterView.findViewById(R.id.edit_link);
        bSignOut = inflaterView.findViewById(R.id.button_sign_out);
        bSignIn = inflaterView.findViewById(R.id.sign_in);
        authActivity = (AuthActivity) requireActivity();

        InputFilter filterShortLink = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        };
        eName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
        eShortLink.setFilters(new InputFilter[] { filterShortLink, new InputFilter.LengthFilter(16) });

        bSignOut.setOnClickListener(view -> {
            signOut(authActivity);
            startActivity(new Intent(authActivity, AuthActivity.class));
            authActivity.finish();
        });

        bSignIn.setOnClickListener(view -> {
            String name = eName.getText().toString();
            String shortLink = eShortLink.getText().toString();

            if (name.equals("")) popup(authActivity, view, "error");
            else if (shortLink.equals("")) popup(authActivity, view, "error");
            else if (shortLink.length() < 3) popup(authActivity, view, "ссылка маленькая");
            else
                myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortLink -> {
                    if (taskTestShortLink.getResult().getValue() != null) popup(authActivity, view, "ссылка занята");
                    else
                        myDB.child("specter").child("users_number").get().addOnCompleteListener(taskId -> {
                            Integer uid = Integer.parseInt(String.valueOf(taskId.getResult().getValue()))+1;
                            myDB.child("specter").child("users").child(String.valueOf(uid)).setValue(new User(uid, authEcostId, name, shortLink));
                            myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("id").setValue(uid);
                            myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("type").setValue("user");
                            myDB.child("ecost").child("users").child(String.valueOf(authEcostId)).child("services").child("specter").setValue(uid);
                            myDB.child("specter").child("users_number").setValue(uid);
                            pushPreferenceAuth(authActivity, true);
                            pushPreferenceId(authActivity, uid);
                            pushPreferenceName(authActivity, name);
                            pushPreferenceShortUserLink(authActivity, shortLink);
                            startActivity(new Intent(authActivity, MainMenuActivity.class));
                            authActivity.finish();
                        });
                });
        });

        return inflaterView;
    }

}