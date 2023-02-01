package com.ecost.specter.menu;

import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    TextView tUserName, tShortUserLink;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainMenuBinding binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_channels, R.id.nav_create_channel, R.id.nav_settings, R.id.nav_channels_search).setOpenableLayout(binding.drawerLayout).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu);
        tUserName = binding.navView.getHeaderView(0).findViewById(R.id.user_name);
        tShortUserLink = binding.navView.getHeaderView(0).findViewById(R.id.short_user_link);

        setSupportActionBar(binding.toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Objects.requireNonNull(getSupportActionBar()).hide();

        tUserName.setText(authUserName);
        tShortUserLink.setText(getString(R.string.symbol_at) + authShortUserLink);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            navController.navigate(R.id.nav_channels_search);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}