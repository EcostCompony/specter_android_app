package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.support.SupportActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NavigationFragment extends BottomSheetDialogFragment {

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_navigation, container, false);

        TextView tvUserName = inflaterView.findViewById(R.id.navigate_name);
        TextView tvShortUserLink = inflaterView.findViewById(R.id.navigate_short_user_link);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        tvUserName.setText(authUserName);
        tvShortUserLink.setText(getString(R.string.symbol_at) + authShortUserLink);

        View.OnClickListener onClickListener = view -> {
            dismiss();
            if (view != inflaterView.findViewById(R.id.button_support)) mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, (view == inflaterView.findViewById(R.id.button_create_channel) ? new CreateChannelMenuFragment() : new SettingsMenuFragment())).addToBackStack(null).commit();
            else startActivity(new Intent(mainMenuActivity, SupportActivity.class));
        };
        inflaterView.findViewById(R.id.button_create_channel).setOnClickListener(onClickListener);
        inflaterView.findViewById(R.id.button_settings).setOnClickListener(onClickListener);
        inflaterView.findViewById(R.id.button_support).setOnClickListener(onClickListener);

        return inflaterView;
    }

}