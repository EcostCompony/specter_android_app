package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class MainChannelSettingsFragment extends Fragment {

    private EditText etTitle, etShortLink, etDescription;
    private Spinner sCategory;
    private ImageButton ibSaveTitle, ibSaveShortLink, ibSaveCategory, ibSaveDescription;
    private ChannelActivity channelActivity;
    private Response response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_main_channel_settings, container, false);

        etTitle = inflaterView.findViewById(R.id.input_title);
        ibSaveTitle = inflaterView.findViewById(R.id.button_save_title);
        etShortLink = inflaterView.findViewById(R.id.input_short_link);
        ibSaveShortLink = inflaterView.findViewById(R.id.button_save_short_link);
        sCategory = inflaterView.findViewById(R.id.spinner_category);
        ibSaveCategory = inflaterView.findViewById(R.id.button_save_category);
        etDescription = inflaterView.findViewById(R.id.input_description);
        ibSaveDescription = inflaterView.findViewById(R.id.button_save_description);
        channelActivity = (ChannelActivity) requireActivity();

        etTitle.setText(channelActivity.channelTitle);
        etShortLink.setText(channelActivity.channelShortLink);
        etDescription.setText(channelActivity.channelDescription);

        etTitle.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(32) });
        etShortLink.setFilters(new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[A-Z\\d_.]+$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(32) });
        etDescription.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(256) });

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(channelActivity, R.array.categories_array, R.layout.spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(categoriesAdapter);
        sCategory.setSelection(channelActivity.channelCategory, false);
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ibSaveCategory.setVisibility(channelActivity.channelCategory == position ? View.GONE : View.VISIBLE);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                ibSaveTitle.setVisibility(etTitle.getText().toString().trim().equals(channelActivity.channelTitle) ? View.GONE : View.VISIBLE);
                ibSaveShortLink.setVisibility(etShortLink.getText().toString().equals(channelActivity.channelShortLink) ? View.GONE : View.VISIBLE);
                ibSaveDescription.setVisibility(etDescription.getText().toString().trim().equals(channelActivity.channelDescription) || etDescription.getText().toString().trim().equals("") ? View.GONE : View.VISIBLE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        etTitle.addTextChangedListener(textWatcher);
        etShortLink.addTextChangedListener(textWatcher);
        etDescription.addTextChangedListener(textWatcher);

        etTitle.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etTitle.getText().toString().trim().equals(channelActivity.channelTitle)) saveTitle(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        etShortLink.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etShortLink.getText().toString().equals(channelActivity.channelShortLink)) saveShortLink(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        etDescription.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && !etDescription.getText().toString().trim().equals(channelActivity.channelDescription)) saveDescription(view);
            return keyCode == KeyEvent.KEYCODE_ENTER;
        });

        ibSaveTitle.setOnClickListener(this::saveTitle);
        ibSaveShortLink.setOnClickListener(this::saveShortLink);
        ibSaveDescription.setOnClickListener(this::saveDescription);
        ibSaveCategory.setOnClickListener(view -> Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/channels.edit?v=1.0&channel_id=" + channelActivity.channelId + "&category=" + sCategory.getSelectedItemPosition(), accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        channelActivity.channelCategory = sCategory.getSelectedItemPosition();
                        ibSaveCategory.setVisibility(View.GONE);
                    }
                });
            }
        }));

        inflaterView.findViewById(R.id.button_delete_channel).setOnClickListener(view -> Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/channels.delete?v=1.0&channel_id=" + channelActivity.channelId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> channelActivity.finish());
            }
        }));

        return inflaterView;
    }

    private void saveTitle(View view) {
        String title = etTitle.getText().toString().trim();

        if (title.equals("")) showToastMessage(channelActivity, view, 2, getString(R.string.main_channel_settings_error_not_title));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/channels.edit?v=1.0&channel_id=" + channelActivity.channelId + "&title=" + title, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        channelActivity.channelTitle = title;
                        ibSaveTitle.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void saveShortLink(View view) {
        String shortLink = etShortLink.getText().toString();

        if (shortLink.equals("")) showToastMessage(channelActivity, view, 2, getString(R.string.main_channel_settings_error_not_short_link));
        else if (shortLink.length() < 3) showToastMessage(channelActivity, view, 2, getString(R.string.main_channel_settings_error_small_short_link));
        else Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/channels.edit?v=1.0&channel_id=" + channelActivity.channelId + "&short_link=" + shortLink, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        if (response.getError().getErrorCode() == 51) showToastMessage(channelActivity, view, 2, getString(R.string.main_channel_settings_error_already_in_use));
                        else showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    } else {
                        channelActivity.channelShortLink = shortLink;
                        ibSaveShortLink.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public void saveDescription(View view) {
        String description = etDescription.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/channels.edit?v=1.0&channel_id=" + channelActivity.channelId + "&description=" + description, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        channelActivity.channelDescription = description;
                        ibSaveDescription.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

}