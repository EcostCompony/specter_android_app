package com.ecost.specter.auth;

import static com.ecost.specter.Routing.popup;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ecost.specter.R;

public class AuthActivity extends AppCompatActivity {

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SignInFragment()).commit();
    }

    public void popupInput(View view, EditText editText, EditText editText2, String context, FrameLayout... frameLayout) {
        (frameLayout.length != 0 ? frameLayout[0] : editText).startAnimation(AnimationUtils.loadAnimation(this, R.anim.input_shake));
        editText2.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth));
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth_error));
        popup(this, view, 1, context);
    }

}