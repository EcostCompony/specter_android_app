package com.ecost.specter.auth;

import static com.ecost.specter.Routing.authEcostId;
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

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (authEcostId == 0) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SignInFragment()).commit();
        else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SpecterAuthFragment()).commit();
    }

    public void popupOneInput(View view, String textError, EditText editText) {
        editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth_err));

        popup(this, view, textError);
    }

    public void popupTwoInput(View view, String textError, EditText editText, EditText editText2, FrameLayout... frameLayout) {
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth));
        editText2.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth));

        if (frameLayout.length != 0) frameLayout[0].startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        else editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth_err));

        popup(this, view, textError);
    }

}