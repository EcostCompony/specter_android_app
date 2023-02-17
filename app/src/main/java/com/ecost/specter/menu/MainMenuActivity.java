package com.ecost.specter.menu;

import static com.ecost.specter.Routing.appLanguage;
import static com.ecost.specter.Routing.changeLocale;
import static com.ecost.specter.Routing.pushPreferenceLanguage;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;

import java.util.Locale;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (getIntent().getBooleanExtra("CREATE", false)) {
            getIntent().putExtra("CREATE", false);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsMenuFragment()).commit();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (++check <= 2 && !Objects.equals(appLanguage, getResources().getStringArray(R.array.setting_array_language)[0]) || check <= 1) {
            if (Objects.equals(appLanguage, getResources().getStringArray(R.array.setting_array_language)[1])) adapterView.setSelection(1);
            return;
        }
        changeLocale(this, new Locale(adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_language)[0]) ? "ru" : "en"));
        pushPreferenceLanguage(this, getResources().getStringArray(R.array.setting_array_language)[adapterView.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.setting_array_language)[0]) ? 0 : 1]);
        recreate();
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}

}