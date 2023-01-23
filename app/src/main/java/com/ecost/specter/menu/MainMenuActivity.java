package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authName;
import static com.ecost.specter.Routing.authShortUserLink;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ecost.specter.R;
import com.ecost.specter.databinding.ActivityMainMenuBinding;

import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity {

    AppBarConfiguration mAppBarConfiguration;
    NavController navController;
    TextView tName, tShortUserLink;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainMenuBinding binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_channels, R.id.nav_create_channel, R.id.nav_settings).setOpenableLayout(binding.drawerLayout).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu);
        tName = binding.navView.getHeaderView(0).findViewById(R.id.name);
        tShortUserLink = binding.navView.getHeaderView(0).findViewById(R.id.short_user_link);

        setSupportActionBar(binding.toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ava_nav);
        getSupportActionBar().hide();

        tName.setText(authName);
        tShortUserLink.setText(getString(R.string.symbol_at) + authShortUserLink);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

}