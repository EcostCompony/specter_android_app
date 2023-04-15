package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.models.Channel;
import com.ecost.specter.models.User;

import java.util.regex.Pattern;

public class CreateChannelMenuFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_create_channel_menu, container, false);

        EditText etChannelTitle = inflaterView.findViewById(R.id.input_title);
        EditText etShortChannelLink = inflaterView.findViewById(R.id.input_short_channel_link);
        Spinner sChannelCategory = inflaterView.findViewById(R.id.spinner_chanel_сategory);
        EditText etChannelDescription = inflaterView.findViewById(R.id.input_channel_description);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        etChannelTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        etShortChannelLink.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.channel_settings_array_category, R.layout.spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sChannelCategory.setAdapter(categoriesAdapter);
        sChannelCategory.setSelection(0,false);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        inflaterView.findViewById(R.id.button_create_channel).setOnClickListener(view -> {
            String title = etChannelTitle.getText().toString().trim();
            String shortChannelLink = etShortChannelLink.getText().toString();

            if (title.equals("")) popup(mainMenuActivity, view, 1, getString(R.string.create_channel_menu_error_not_title));
            else if (shortChannelLink.equals("")) popup(mainMenuActivity, view, 1, getString(R.string.create_channel_menu_error_not_short_channel_link));
            else if (shortChannelLink.length() < 3) popup(mainMenuActivity, view, 1, getString(R.string.create_channel_menu_error_small_short_channel_link));
            else
                myDB.child("specter").child("uid").child(shortChannelLink.replace(".", "*")).child("id").get().addOnCompleteListener(task -> {
                    if (task.getResult().getValue() != null) popup(mainMenuActivity, view, 1, getString(R.string.create_channel_menu_error_busy_short_channel_link));
                    else
                        myDB.child("specter").child("channels_number").get().addOnCompleteListener(taskId -> {
                            Integer id = Integer.parseInt(String.valueOf(taskId.getResult().getValue()));
                            Channel channel = new Channel(id, shortChannelLink, title, sChannelCategory.getSelectedItemPosition() == 0 ? null : sChannelCategory.getSelectedItemPosition(), etChannelDescription.getText().toString().trim().equals("") ? null : etChannelDescription.getText().toString().trim(), "%CHANNEL_CREATED%", true);
                            myDB.child("specter").child("channels").child(String.valueOf(id)).setValue(channel);
                            myDB.child("specter").child("channels").child(String.valueOf(id)).child("subscribers").push().setValue(new User(authId, true, authEcostId, authUserName, authShortUserLink));
                            myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("id").setValue(id);
                            myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("type").setValue("channel");
                            myDB.child("specter").child("channels_number").setValue(id+1);
                            mainMenuActivity.startChannel(channel);
                            mainMenuActivity.getSupportFragmentManager().popBackStack();
                        });
                });
        });

        return inflaterView;
    }

}