package com.ecost.specter.support;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;
import static com.ecost.specter.Routing.pushPreferenceAuth;
import static com.ecost.specter.Routing.pushPreferenceId;
import static com.ecost.specter.Routing.pushPreferenceShortUserLink;
import static com.ecost.specter.Routing.pushPreferenceUserName;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ecost.specter.R;
import com.ecost.specter.menu.MainMenuActivity;
import com.ecost.specter.models.FAQPost;
import com.ecost.specter.models.User;

public class FormAppealSupportFragment extends Fragment {

    EditText eAppealTopic, eAppealText;
    SupportActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_form_appeal_support, container, false);

        eAppealTopic = inflaterView.findViewById(R.id.input_appeal_topic);
        eAppealText = inflaterView.findViewById(R.id.input_appeal_text);
        supportActivity = (SupportActivity) requireActivity();

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> supportActivity.getSupportFragmentManager().popBackStack());

        eAppealText.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) createAppeal(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_create_appeal).setOnClickListener(this::createAppeal);

        return inflaterView;
    }

    public void createAppeal(View view) {
        String appealTopic = eAppealTopic.getText().toString();
        String appealText = eAppealText.getText().toString();

        if (appealTopic.equals("")) popup(supportActivity, view, 1, "Для создания обращения введите его тему.");
        else if (appealText.equals("")) popup(supportActivity, view, 1, "Для создания обращения опишите его.");
        else {
            myDB.child("specter").child("support").child("number_appeals").get().addOnCompleteListener(task -> {
                Integer id = Integer.parseInt(String.valueOf(task.getResult().getValue()))+1;
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(id)).child("id").setValue(id);
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(id)).child("author").setValue(authId);
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(id)).child("topic").setValue(appealTopic);
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(id)).child("body").setValue(appealText);
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(id)).child("posts_number").setValue(1);
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(id)).child("posts").child("0").setValue(new FAQPost(1, appealText));
                myDB.child("specter").child("support").child("number_appeals").setValue(id);
                supportActivity.appealId = id;
                supportActivity.appealAuthor = authId;
                supportActivity.appealTopic = appealTopic;
                supportActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new AppealSupportFragment()).commit();
            });
        }
    }

}