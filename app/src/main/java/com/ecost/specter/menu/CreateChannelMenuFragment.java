package com.ecost.specter.menu;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class CreateChannelMenuFragment extends Fragment {

    private Response response;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_create_channel_menu, container, false);

        EditText etTitle = inflaterView.findViewById(R.id.input_title);
        EditText etShortLink = inflaterView.findViewById(R.id.input_short_link);
        Spinner sCategory = inflaterView.findViewById(R.id.spinner_category);
        EditText etDescription = inflaterView.findViewById(R.id.input_description);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        etTitle.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(64) });
        etShortLink.setFilters(new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Pattern.compile("^[a-z][a-z\\d_.]{2,30}[a-z\\d]$", Pattern.CASE_INSENSITIVE).matcher(String.valueOf(source.charAt(i))).find()) return "";
                if (Character.isUpperCase(source.charAt(i))) return String.valueOf(source.charAt(i)).toLowerCase();
            }
            return null;
        }, new InputFilter.LengthFilter(32) });
        etDescription.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(256) });

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(mainMenuActivity, R.array.categories_array, R.layout.spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(categoriesAdapter);
        sCategory.setSelection(0, false);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        inflaterView.findViewById(R.id.button_create_channel).setOnClickListener(view -> {
            String title = etTitle.getText().toString().trim();
            String shortLink = etShortLink.getText().toString();
            int category = sCategory.getSelectedItemPosition();
            String description = etDescription.getText().toString().trim();

            if (title.equals("")) showToastMessage(mainMenuActivity, view, 2, getString(R.string.create_channel_menu_error_not_title));
            else if (shortLink.equals("")) showToastMessage(mainMenuActivity, view, 2, getString(R.string.create_channel_menu_error_not_short_link));
            else if (shortLink.length() < 4) showToastMessage(mainMenuActivity, view, 2, getString(R.string.create_channel_menu_error_small_short_link));
            else Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    response = new API("http://thespecterlife.com:3501/api/method/channels.create?v=1.0&title=" + title + "&short_link=" + shortLink + (category != 0 ? "&category=" + category : "") + (description.length() != 0 ? "&description=" + description : ""), accessToken).call();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (response.getError() != null) {
                            if (response.getError().getErrorCode() == 51) showToastMessage(mainMenuActivity, view, 2, getString(R.string.create_channel_menu_error_already_in_use));
                            else showToastMessage(mainMenuActivity, view, 2, getString(R.string.unknown_error));
                        } else {
                            mainMenuActivity.openChannel(response.getChannelRes());
                            mainMenuActivity.getSupportFragmentManager().popBackStack();
                        }
                    });
                }
            });
        });

        return inflaterView;
    }

}