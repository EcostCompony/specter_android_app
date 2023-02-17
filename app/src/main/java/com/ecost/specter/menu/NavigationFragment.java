package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecost.specter.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NavigationFragment extends BottomSheetDialogFragment {

    TextView tName, tShortUserLink;
    MainMenuActivity mainMenuActivity;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_navigation, container, false);

        tName = inflaterView.findViewById(R.id.navigate_name);
        tShortUserLink = inflaterView.findViewById(R.id.navigate_short_user_link);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        tName.setText(authUserName);
        tShortUserLink.setText(getString(R.string.symbol_at) + authShortUserLink);

        inflaterView.findViewById(R.id.button_create_channel).setOnClickListener(view -> {
            dismiss();
            mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new CreateChannelMenuFragment()).commit();
        });

        inflaterView.findViewById(R.id.button_settings).setOnClickListener(view -> {
            dismiss();
            mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SettingsMenuFragment()).commit();
        });

        return inflaterView;
    }

}