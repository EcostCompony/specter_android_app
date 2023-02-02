package com.ecost.specter.channel;

import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class ChannelSettingsFragment extends Fragment {

    EditText eChannelTitle, eShortChannelLink;
    ImageButton bSaveChannelTitle, bSaveShortChannelLink;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_settings, container, false);

        eChannelTitle = inflaterView.findViewById(R.id.input_channel_title);
        eShortChannelLink = inflaterView.findViewById(R.id.input_short_channel_link);
        bSaveChannelTitle = inflaterView.findViewById(R.id.button_save_channel_title);
        bSaveShortChannelLink = inflaterView.findViewById(R.id.button_save_short_channel_link);
        channelActivity = (ChannelActivity) requireActivity();

        eChannelTitle.setText(channelActivity.channelTitle);
        eShortChannelLink.setText(channelActivity.channelShortLink);

        eChannelTitle.setFilters(new InputFilter[] {new InputFilter.LengthFilter(32)});
        eShortChannelLink.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                String j = String.valueOf(source.charAt(i));
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(j).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return j.toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit());

        eChannelTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveChannelTitle.setVisibility(eChannelTitle.getText().toString().equals(channelActivity.channelTitle) ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        eShortChannelLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveShortChannelLink.setVisibility(eShortChannelLink.getText().toString().equals(channelActivity.channelShortLink) ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        eChannelTitle.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !eChannelTitle.getText().toString().equals(channelActivity.channelTitle)) saveChannelTitle(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        eShortChannelLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !eShortChannelLink.getText().toString().equals(channelActivity.channelShortLink)) saveShortChannelLink(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        bSaveChannelTitle.setOnClickListener(this::saveChannelTitle);

        bSaveShortChannelLink.setOnClickListener(this::saveShortChannelLink);

        inflaterView.findViewById(R.id.button_delete_channel).setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).setValue(null);
            channelActivity.finish();
        });

        return inflaterView;
    }

    public void saveChannelTitle(View view) {
        String channelTitle = eChannelTitle.getText().toString();

        if (channelTitle.equals("")) popup(channelActivity, view, getString(R.string.channel_settings_error_not_channel_title));
        else {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("title").setValue(channelTitle);
            channelActivity.channelTitle = channelTitle;
            bSaveChannelTitle.setVisibility(View.GONE);
        }
    }

    public void saveShortChannelLink(View view) {
        String shortChannelLink = eShortChannelLink.getText().toString();

        if (shortChannelLink.equals("")) popup(channelActivity, view, getString(R.string.channel_settings_error_not_short_channel_title));
        else if (shortChannelLink.length() < 3) popup(channelActivity, view, getString(R.string.channel_settings_error_small_short_channel_link));
        else
            myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortChannelLink -> {
                if (taskTestShortChannelLink.getResult().getValue() != null) popup(channelActivity, view, getString(R.string.channel_settings_error_busy_short_channel_link));
                else {
                    myDB.child("specter").child("uid").child(channelActivity.channelShortLink.replace(".", "*")).setValue(null);
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("shortLink").setValue(shortChannelLink);
                    myDB.child("specter").child("uid").child(channelActivity.channelShortLink.replace('.', '*')).child("id").setValue(channelActivity.channelId);
                    myDB.child("specter").child("uid").child(channelActivity.channelShortLink.replace('.', '*')).child("type").setValue("channel");
                    channelActivity.channelShortLink = shortChannelLink;
                    bSaveShortChannelLink.setVisibility(View.GONE);
                }
            });
    }

}