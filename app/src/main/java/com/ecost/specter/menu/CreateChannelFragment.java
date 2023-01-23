package com.ecost.specter.menu;

import static android.app.Activity.RESULT_OK;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.databinding.FragmentCreateChannelBinding;
import com.ecost.specter.models.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CreateChannelFragment extends Fragment {

    CardView cChannelPhoto;
    EditText eTitle, eShortLink;
    ImageView iChannelPhoto;
    MainMenuActivity mainMenuActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentCreateChannelBinding binding = FragmentCreateChannelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cChannelPhoto = binding.channelPhoto;
        eTitle = binding.inputTitle;
        eShortLink = binding.inputShortLink;
        iChannelPhoto = binding.imageChannelPhoto;
        mainMenuActivity = (MainMenuActivity) requireActivity();

        InputFilter filterShortLink = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        };
        eTitle.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });
        eShortLink.setFilters(new InputFilter[] { filterShortLink, new InputFilter.LengthFilter(16) });

        Objects.requireNonNull(mainMenuActivity.getSupportActionBar()).hide();

        binding.buttonClose.setOnClickListener(view -> mainMenuActivity.navController.navigate(R.id.nav_channels));

        binding.buttonCreateChannel.setOnClickListener(view -> {
            String title = eTitle.getText().toString();
            String shortLink = eShortLink.getText().toString();

            if (title.equals("")) popup(mainMenuActivity, view, "error");
            else if (shortLink.equals("")) popup(mainMenuActivity, view, "error");
            else if (shortLink.length() < 3) popup(mainMenuActivity, view, "ссылка маленькая");
            else
                myDB.child("specter").child("uid").child(shortLink.replace(".", "*")).child("id").get().addOnCompleteListener(taskTestShortLink -> {
                    if (taskTestShortLink.getResult().getValue() != null) popup(mainMenuActivity, view, "ссылка занята");
                    else
                        myDB.child("specter").child("channels_number").get().addOnCompleteListener(taskId -> {
                            Integer uid = Integer.parseInt(String.valueOf(taskId.getResult().getValue()));
                            List<Integer> subscribers = new ArrayList<>();
                            subscribers.add(authId);
                            myDB.child("specter").child("channels").child(String.valueOf(uid)).setValue(new Channel(uid, shortLink, authId, title, "%CHANNEL_CREATED%", true, subscribers));
                            myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("id").setValue(uid);
                            myDB.child("specter").child("uid").child(shortLink.replace('.', '*')).child("type").setValue("channel");
                            myDB.child("specter").child("channels_number").setValue(uid + 1);
                            mainMenuActivity.navController.navigate(R.id.nav_channels);
                        });
                });
        });

        return root;
    }

}