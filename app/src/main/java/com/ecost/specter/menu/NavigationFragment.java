package com.ecost.specter.menu;

import static com.ecost.specter.Routing.userName;
import static com.ecost.specter.Routing.userShortLink;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecost.specter.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NavigationFragment extends BottomSheetDialogFragment {

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_navigation, container, false);

        TextView tvName = inflaterView.findViewById(R.id.name);
        TextView tvShortLink = inflaterView.findViewById(R.id.short_link);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        tvName.setText(userName);
        tvShortLink.setText(getString(R.string.symbol_at) + userShortLink);

        View.OnClickListener onClickListener = view -> {
            dismiss();
            //mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, (view == inflaterView.findViewById(R.id.button_opening_menu_create_channel) ? new CreateChannelMenuFragment() : new SettingsMenuFragment())).addToBackStack(null).commit();
        };
        inflaterView.findViewById(R.id.button_opening_menu_create_channel).setOnClickListener(onClickListener);
        inflaterView.findViewById(R.id.button_opening_menu_settings).setOnClickListener(onClickListener);

        return inflaterView;
    }

}