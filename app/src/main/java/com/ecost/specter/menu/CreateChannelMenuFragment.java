package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.models.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CreateChannelMenuFragment extends Fragment {

    EditText eTitle, eShortChannelLink, eDescription;
    Spinner sCategory;
    MainMenuActivity mainMenuActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_create_channel_menu, container, false);

        eTitle = inflaterView.findViewById(R.id.input_title);
        eShortChannelLink = inflaterView.findViewById(R.id.input_short_channel_link);
        sCategory = inflaterView.findViewById(R.id.spinner_chanel_сategory);
        eDescription = inflaterView.findViewById(R.id.input_channel_description);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.channel_settings_array_category, R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(categoryAdapter);
        sCategory.setOnItemSelectedListener(mainMenuActivity);

        eTitle.setFilters(new InputFilter[] {new InputFilter.LengthFilter(32)});
        eShortChannelLink.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                String j = String.valueOf(source.charAt(i));
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(j).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return j.toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsMenuFragment()).commit());

        eShortChannelLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) ((InputMethodManager) mainMenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        });

        eDescription.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) createChannel(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        inflaterView.findViewById(R.id.button_create_channel).setOnClickListener(this::createChannel);

        return inflaterView;
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
                        myDB.child("specter").child("channels").child(String.valueOf(id)).setValue(new Channel(id, shortChannelLink, authId, 0, title, mainMenuActivity.categoryId == 0 ? null : mainMenuActivity.categoryId, eDescription.getText().toString().equals("") ? null : eDescription.getText().toString(), "%CHANNEL_CREATED%", true, subscribers));
                        myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("id").setValue(id);
                        myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("type").setValue("channel");
                        myDB.child("specter").child("channels_number").setValue(id + 1);
                        mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsMenuFragment()).commit();
                    });
            });
    }

}