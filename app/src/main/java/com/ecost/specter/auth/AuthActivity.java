package com.ecost.specter.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SignInFragment()).commit();
    }

}