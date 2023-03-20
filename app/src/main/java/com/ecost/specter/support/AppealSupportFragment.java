package com.ecost.specter.support;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecost.specter.R;

public class AppealSupportFragment extends Fragment {

    SupportActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_appeal_support, container, false);

        supportActivity = (SupportActivity) requireActivity();

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> supportActivity.getSupportFragmentManager().popBackStack());

        return inflaterView;
    }

}