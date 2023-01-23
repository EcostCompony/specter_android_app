package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authName;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceName;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.signOut;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.auth.AuthActivity;
import com.ecost.specter.databinding.FragmentSettingsBinding;

import java.util.Objects;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    EditText eName, eShortLink;
    ImageButton bSaveName, bSaveShortLink;
    MainMenuActivity mainMenuActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSettingsBinding binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eName = binding.inputTitle;
        eShortLink = binding.inputShortLink;
        bSaveName = binding.buttonSaveName;
        bSaveShortLink = binding.buttonSaveShortLink;
        mainMenuActivity = (MainMenuActivity) requireActivity();

        eName.setText(authName);
        eShortLink.setText(authShortUserLink);

        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).hide();

        InputFilter filterShortLink = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        };
        eName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
        eShortLink.setFilters(new InputFilter[] { filterShortLink, new InputFilter.LengthFilter(16) });

        eName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!eName.getText().toString().equals(authName)) bSaveName.setVisibility(View.VISIBLE);
                else bSaveName.setVisibility(View.GONE);
            }
        });

        eShortLink.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!eShortLink.getText().toString().equals(authShortUserLink)) bSaveShortLink.setVisibility(View.VISIBLE);
                else bSaveShortLink.setVisibility(View.GONE);
            }
        });

        bSaveName.setOnClickListener(view -> {
            String name = eName.getText().toString();
            if (name.equals("")) popup(mainMenuActivity, view, "error");
            else {
                myDB.child("specter").child("users").child(String.valueOf(authId)).child("name").setValue(name);
                pushPreferenceName(mainMenuActivity, name);
                bSaveName.setVisibility(View.GONE);
            }
        });

        bSaveShortLink.setOnClickListener(view -> {
            String shortLink = eShortLink.getText().toString();
            if (shortLink.equals("")) popup(mainMenuActivity, view, "error");
            else if (shortLink.length() < 3) popup(mainMenuActivity, view, "ссылка маленькая");
            else
                myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortLink -> {
                    if (taskTestShortLink.getResult().getValue() != null && !shortLink.equals(authShortUserLink)) popup(mainMenuActivity, view, "ссылка занята");
                    else {
                        myDB.child("specter").child("uid").child(authShortUserLink.replace(".", "*")).setValue(null);
                        myDB.child("specter").child("users").child(String.valueOf(authId)).child("link").setValue(shortLink);
                        myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("id").setValue(authId);
                        myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("type").setValue("user");
                        pushPreferenceShortUserLink(mainMenuActivity, shortLink);
                        bSaveShortLink.setVisibility(View.GONE);
                    }
                });
        });

        binding.buttonClose.setOnClickListener(view -> {
            mainMenuActivity.navController.navigate(R.id.nav_channels);
        });

        binding.buttonSignOut.setOnClickListener(view -> {
            signOut(mainMenuActivity);
            startActivity(new Intent(getActivity(), AuthActivity.class));
            mainMenuActivity.finish();
        });

        return root;
    }

}