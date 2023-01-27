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

    String numberPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, authEcostId != 0 ? new SpecterAuthFragment() : new SignInFragment()).commit();
    }

    public void popupOneInput(View view, EditText editText, String text) {
        editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.input_shake));
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth_error));
        popup(this, view, text);
    }

    public void popupTwoInput(View view, EditText editText, EditText editText2, String text, FrameLayout... frameLayout) {
        editText2.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth));
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_auth_error));
        (frameLayout.length != 0 ? frameLayout[0] : editText).startAnimation(AnimationUtils.loadAnimation(this, R.anim.input_shake));
        popup(this, view, text);
    }

}