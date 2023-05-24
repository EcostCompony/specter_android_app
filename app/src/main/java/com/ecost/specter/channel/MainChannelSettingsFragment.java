/*package com.ecost.specter.channel;

import static com.ecost.specter.Routing.AgreeAlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.ecost.specter.R;

import java.util.regex.Pattern;

public class MainChannelSettingsFragment extends Fragment {

    EditText etChannelTitle, etShortChannelLink, etChannelDescription;
    ImageButton bSaveChannelTitle, bSaveShortChannelLink, bSaveChannelDescription;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_main_channel_settings, container, false);

        etChannelTitle = inflaterView.findViewById(R.id.input_channel_title);
        bSaveChannelTitle = inflaterView.findViewById(R.id.button_save_channel_title);
        etShortChannelLink = inflaterView.findViewById(R.id.input_short_channel_link);
        bSaveShortChannelLink = inflaterView.findViewById(R.id.button_save_short_channel_link);
        Spinner sChannelCategory = inflaterView.findViewById(R.id.spinner_chanel_сategory);
        ImageButton bSaveChannelCategory = inflaterView.findViewById(R.id.button_save_channel_сategory);
        etChannelDescription = inflaterView.findViewById(R.id.input_channel_description);
        bSaveChannelDescription = inflaterView.findViewById(R.id.button_save_channel_description);
        channelActivity = (ChannelActivity) requireActivity();

        etChannelTitle.setText(channelActivity.channelTitle);
        etShortChannelLink.setText(channelActivity.shortChannelLink);
        etChannelDescription.setText(channelActivity.channelDescription);

        etChannelTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        etShortChannelLink.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(channelActivity, R.array.channel_settings_array_category, R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sChannelCategory.setAdapter(categoryAdapter);
        sChannelCategory.setSelection(channelActivity.channelCategoryId,false);
        sChannelCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                bSaveChannelCategory.setVisibility(position == channelActivity.channelCategoryId ? View.GONE : View.VISIBLE);
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveChannelTitle.setVisibility(etChannelTitle.getText().toString().equals(channelActivity.channelTitle) ? View.GONE : View.VISIBLE);
                bSaveShortChannelLink.setVisibility(etShortChannelLink.getText().toString().equals(channelActivity.shortChannelLink) ? View.GONE : View.VISIBLE);
                bSaveChannelDescription.setVisibility(etChannelDescription.getText().toString().trim().equals(channelActivity.channelDescription) ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        etChannelTitle.addTextChangedListener(textWatcher);
        etShortChannelLink.addTextChangedListener(textWatcher);
        etChannelDescription.addTextChangedListener(textWatcher);

        View.OnKeyListener onKeyListener = (view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !(view == etChannelTitle ? etChannelTitle : (view == etShortChannelLink ? etShortChannelLink : etChannelDescription)).getText().toString().equals(channelActivity.channelTitle)) {
                if (view == etChannelTitle) saveChannelTitle(view);
                else if (view == etShortChannelLink) saveShortChannelLink(view);
                else saveChannelDescription(view);
            }
            return keyCode == KeyEvent.KEYCODE_ENTER;
        };
        etChannelTitle.setOnKeyListener(onKeyListener);
        etShortChannelLink.setOnKeyListener(onKeyListener);
        etChannelDescription.setOnKeyListener(onKeyListener);

        bSaveChannelTitle.setOnClickListener(this::saveChannelTitle);
        bSaveShortChannelLink.setOnClickListener(this::saveShortChannelLink);
        bSaveChannelDescription.setOnClickListener(this::saveChannelDescription);
        bSaveChannelCategory.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("categoryId").setValue(sChannelCategory.getSelectedItemPosition());
            channelActivity.channelCategoryId = sChannelCategory.getSelectedItemPosition();
            bSaveChannelCategory.setVisibility(View.GONE);
        });

        inflaterView.findViewById(R.id.button_delete_channel).setOnClickListener(view -> AgreeAlertDialog(channelActivity, container, getString(R.string.channel_settings_delete_channel_alert_dialog_text_header), getString(R.string.channel_settings_delete_channel_alert_dialog_text_description), viewYes -> {
            myDB.child("specter").child("uid").child(channelActivity.shortChannelLink).setValue(null);
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).setValue(null);
            channelActivity.finish();
        }));

        return inflaterView;
    }

    public void saveChannelTitle(View view) {
        String channelTitle = etChannelTitle.getText().toString();
        if (channelTitle.equals("")) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_not_channel_title));
        else {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("title").setValue(channelTitle);
            channelActivity.channelTitle = channelTitle;
            bSaveChannelTitle.setVisibility(View.GONE);
        }
    }

    public void saveShortChannelLink(View view) {
        String shortChannelLink = etShortChannelLink.getText().toString();
        if (shortChannelLink.equals("")) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_not_short_channel_title));
        else if (shortChannelLink.length() < 3) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_small_short_channel_link));
        else
            myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("id").get().addOnCompleteListener(task -> {
                if (task.getResult().getValue() != null) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_busy_short_channel_link));
                else {
                    myDB.child("specter").child("uid").child(channelActivity.shortChannelLink.replace(".", "*")).setValue(null);
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("shortLink").setValue(shortChannelLink);
                    myDB.child("specter").child("uid").child(channelActivity.shortChannelLink.replace('.', '*')).child("id").setValue(channelActivity.channelId);
                    myDB.child("specter").child("uid").child(channelActivity.shortChannelLink.replace('.', '*')).child("type").setValue("channel");
                    channelActivity.shortChannelLink = shortChannelLink;
                    bSaveShortChannelLink.setVisibility(View.GONE);
                }
            });
    }

    public void saveChannelDescription(View view) {
        String channelDescription = etChannelDescription.getText().toString().trim();
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("description").setValue(channelDescription);
        channelActivity.channelDescription = channelDescription;
        bSaveChannelDescription.setVisibility(View.GONE);
    }

}*/