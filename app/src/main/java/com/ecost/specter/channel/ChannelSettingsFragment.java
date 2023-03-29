package com.ecost.specter.channel;

import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;

import java.util.Objects;
import java.util.regex.Pattern;

public class ChannelSettingsFragment extends Fragment {

    EditText eChannelTitle, eShortChannelLink, eDescription;
    Spinner sCategory;
    ImageButton bSaveChannelTitle, bSaveShortChannelLink, bSaveChannelDescription, bSaveChannelCategory;
    int categoryId = 0;
    int check = 0;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel_settings, container, false);

        eChannelTitle = inflaterView.findViewById(R.id.input_channel_title);
        eShortChannelLink = inflaterView.findViewById(R.id.input_short_channel_link);
        eDescription = inflaterView.findViewById(R.id.input_channel_description);
        sCategory = inflaterView.findViewById(R.id.spinner_chanel_сategory);
        bSaveChannelTitle = inflaterView.findViewById(R.id.button_save_channel_title);
        bSaveShortChannelLink = inflaterView.findViewById(R.id.button_save_short_channel_link);
        bSaveChannelCategory = inflaterView.findViewById(R.id.button_save_channel_сategory);
        bSaveChannelDescription = inflaterView.findViewById(R.id.button_save_channel_description);
        channelActivity = (ChannelActivity) requireActivity();

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(channelActivity, R.array.channel_settings_array_category, R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(categoryAdapter);
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (++check <= 2 && !Objects.equals(getResources().getStringArray(R.array.channel_settings_array_category)[channelActivity.categoryId], getResources().getStringArray(R.array.channel_settings_array_category)[0]) || check <= 1) {
                    if (!Objects.equals(getResources().getStringArray(R.array.channel_settings_array_category)[channelActivity.categoryId], getResources().getStringArray(R.array.channel_settings_array_category)[0])) {
                        adapterView.setSelection(channelActivity.categoryId);
                        categoryId = channelActivity.categoryId;
                    }
                    return;
                }
                bSaveChannelCategory.setVisibility(position == channelActivity.categoryId ? View.GONE : View.VISIBLE);
                categoryId = position;
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        eChannelTitle.setText(channelActivity.channelTitle);
        eShortChannelLink.setText(channelActivity.channelShortLink);
        eDescription.setText(channelActivity.channelDescription);

        eChannelTitle.setFilters(new InputFilter[] {new InputFilter.LengthFilter(32)});
        eShortChannelLink.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                String j = String.valueOf(source.charAt(i));
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(j).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return j.toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(16)});

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

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

        eDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bSaveChannelDescription.setVisibility(eDescription.getText().toString().trim().equals(channelActivity.channelDescription) ? View.GONE : View.VISIBLE);
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

        eDescription.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !eDescription.getText().toString().trim().equals(channelActivity.channelDescription)) saveChannelDescription(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        bSaveChannelTitle.setOnClickListener(this::saveChannelTitle);

        bSaveShortChannelLink.setOnClickListener(this::saveShortChannelLink);

        bSaveChannelDescription.setOnClickListener(this::saveChannelDescription);

        bSaveChannelCategory.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("categoryId").setValue(categoryId);
            channelActivity.categoryId = categoryId;
            bSaveChannelCategory.setVisibility(View.GONE);
        });

        inflaterView.findViewById(R.id.button_delete_channel).setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.agree_alert_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
            mDialogBuilder.setView(promptsView);

            TextView tHeader = promptsView.findViewById(R.id.header);
            TextView description = promptsView.findViewById(R.id.description);

            tHeader.setText(R.string.channel_settings_delete_channel_alert_dialog_text_header);
            description.setText(R.string.channel_settings_delete_channel_alert_dialog_text_description);

            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            promptsView.findViewById(R.id.button_yes).setOnClickListener(view1 -> {
                myDB.child("specter").child("uid").child(channelActivity.channelShortLink).setValue(null);
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).setValue(null);
                channelActivity.finish();
                alertDialog.cancel();
            });


            alertDialog.show();

            promptsView.findViewById(R.id.button_cancel).setOnClickListener(view1 -> alertDialog.cancel());
        });

        return inflaterView;
    }

    public void saveChannelTitle(View view) {
        String channelTitle = eChannelTitle.getText().toString();

        if (channelTitle.equals("")) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_not_channel_title));
        else {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("title").setValue(channelTitle);
            channelActivity.channelTitle = channelTitle;
            bSaveChannelTitle.setVisibility(View.GONE);
        }
    }

    public void saveShortChannelLink(View view) {
        String shortChannelLink = eShortChannelLink.getText().toString();

        if (shortChannelLink.equals("")) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_not_short_channel_title));
        else if (shortChannelLink.length() < 3) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_small_short_channel_link));
        else
            myDB.child("specter").child("uid").child(shortChannelLink.replace('.', '*')).child("id").get().addOnCompleteListener(taskTestShortChannelLink -> {
                if (taskTestShortChannelLink.getResult().getValue() != null) popup(channelActivity, view, 1, getString(R.string.channel_settings_error_busy_short_channel_link));
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

    public void saveChannelDescription(View view) {
        String channelDescription = eDescription.getText().toString().trim();

        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("description").setValue(channelDescription);
        channelActivity.channelDescription = channelDescription;
        bSaveChannelDescription.setVisibility(View.GONE);
    }

}