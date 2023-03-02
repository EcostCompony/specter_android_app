package com.ecost.specter.support;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ecost.specter.R;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new FAQSupportFragment()).commit();
    }

}