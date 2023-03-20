package com.ecost.specter.support;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecost.specter.R;

public class AppealsSupportFragment extends Fragment {

    SupportActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_appeals_support, container, false);

        supportActivity = (SupportActivity) requireActivity();

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> supportActivity.finish());

        inflaterView.findViewById(R.id.button_open_faq).setOnClickListener(view -> supportActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new FAQSupportFragment()).commit());

        return inflaterView;
    }

}