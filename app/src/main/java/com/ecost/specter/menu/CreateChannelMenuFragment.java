package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.databinding.FragmentCreateChannelMenuBinding;
import com.ecost.specter.models.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CreateChannelMenuFragment extends Fragment {

    EditText eTitle, eShortChannelLink;
    MainMenuActivity mainMenuActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentCreateChannelMenuBinding binding = FragmentCreateChannelMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eTitle = binding.inputTitle;
        eShortChannelLink = binding.inputShortChannelLink;
        mainMenuActivity = (MainMenuActivity) requireActivity();

        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).hide();

        eTitle.setFilters(new InputFilter[] {new InputFilter.LengthFilter(32)});
        eShortChannelLink.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                String j = String.valueOf(source.charAt(i));
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(j).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return j.toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        binding.buttonClose.setOnClickListener(view -> mainMenuActivity.navController.navigate(R.id.nav_channels));

        eShortChannelLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) createChannel(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        binding.buttonCreateChannel.setOnClickListener(this::createChannel);

        return root;
    }

    public void createChannel(View view) {
        String title = eTitle.getText().toString();
        String shortChannelLink = eShortChannelLink.getText().toString();

        if (title.equals("")) popup(mainMenuActivity, view, getString(R.string.create_channel_menu_error_not_title));
        else if (shortChannelLink.equals("")) popup(mainMenuActivity, view, getString(R.string.create_channel_menu_error_not_short_channel_link));
        else if (shortChannelLink.length() < 3) popup(mainMenuActivity, view, getString(R.string.create_channel_menu_error_small_short_channel_link));
        else
            myDB.child("specter").child("uid").child(shortChannelLink.replace(".", "*")).child("id").get().addOnCompleteListener(taskTestShortChannelLink -> {
                if (taskTestShortChannelLink.getResult().getValue() != null) popup(mainMenuActivity, view, getString(R.string.create_channel_menu_error_busy_short_channel_link));
                else
                    myDB.child("specter").child("channels_number").get().addOnCompleteListener(taskId -> {
                        Integer id = Integer.parseInt(String.valueOf(taskId.getResult().getValue()));
                        List<Integer> subscribers = new ArrayList<>();
                        subscribers.add(authId);
                        myDB.child("specter").child("channels").child(String.valueOf(id)).setValue(new Channel(id, shortChannelLink, authId, title, "%CHANNEL_CREATED%", true, subscribers));
                        myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("id").setValue(id);
                        myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("type").setValue("channel");
                        myDB.child("specter").child("channels_number").setValue(id + 1);
                        mainMenuActivity.navController.navigate(R.id.nav_channels);
                    });
            });
    }

}