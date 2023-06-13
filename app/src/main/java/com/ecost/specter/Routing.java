package com.ecost.specter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;
import com.ecost.specter.auth.AuthActivity;
import com.ecost.specter.menu.MainMenuActivity;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;

public class Routing extends AppCompatActivity {

    public static String accessToken, userName, userShortLink;
    public static int sectionPosition, appTheme, appLanguage;
    private Response response, response1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 31) setContentView(R.layout.splash_screen);
        else SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true);

        accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("ACCESS_TOKEN", null);
        putAppTheme(this, PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_THEME", 0));
        putAppLanguage(this, accessToken == null ? (Locale.getDefault().getLanguage().equals("ru") ? 1 : 0) : PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_LANGUAGE", 0));

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://thespecterlife.com:3501/api/method/utils.getAndroidAppMinimumSupportedVersionCode?v=1.0").call();
                response1 = new API("http://thespecterlife.com:3501/api/method/account.get?v=1.0", accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (Integer.parseInt(response.getRes().getValue()) > BuildConfig.VERSION_CODE) startActivity(new Intent(this, HardUpdateActivity.class));
                    else if (accessToken == null) startActivity(new Intent(this, AuthActivity.class));
                    else if (response1.getError() != null || response1.getRes() == null) {
                        putAccessToken(this, null);
                        startActivity(new Intent(this, AuthActivity.class));
                    } else {
                        putUserName(this, response1.getRes().getName());
                        putUserShortLink(this, response1.getRes().getShortLink());
                        startActivity(new Intent(this, MainMenuActivity.class));
                    }
                });
            }
        });
    }

    public static void putAccessToken(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ACCESS_TOKEN", value).apply();
        accessToken = value;
    }

    public static void putUserName(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("USER_NAME", value).apply();
        userName = value;
    }

    public static void putUserShortLink(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("USER_SHORT_LINK", value).apply();
        userShortLink = value;
    }

    public static void putAppLanguage(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("APP_LANGUAGE", value).apply();
        appLanguage = value;

        Locale[] locales = { new Locale("en"), new Locale("ru") };
        Configuration configuration = new Configuration();
        configuration.setLocale(locales[value]);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static void putAppTheme(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("APP_THEME", value).apply();
        appTheme = value;

        if (appTheme == 0) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if (appTheme == 1) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (appTheme == 2) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static String pluralForm(Integer i, String[] words) {
        if (!Locale.getDefault().getLanguage().equals("ru")) return i + " " + words[i == 1 ? 0 : 1];
        return i + " " + words[(i % 100 > 4 && i % 100 < 20) ? 2 : new int[]{ 2, 0, 1, 1, 1, 2 }[(i % 10 < 5) ? Math.abs(i) % 10 : 5]];
    }

    public static void showToastMessage(Activity activity, View view, int type, String text) {
        @SuppressLint("InflateParams") View vToastMessage =  activity.getLayoutInflater().inflate(R.layout.window_toast_message, null);
        PopupWindow pwToastMessage = new PopupWindow(vToastMessage, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

        ((TextView) vToastMessage.findViewById(R.id.message)).setText(text);
        if (type == 2) vToastMessage.findViewById(R.id.icon_error).setVisibility(View.VISIBLE);

        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);

        pwToastMessage.setOutsideTouchable(true);
        pwToastMessage.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                activity.runOnUiThread(pwToastMessage::dismiss);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void showPopupMenu(Activity activity, View view, int menu, PopupMenu.OnMenuItemClickListener onMenuItemClickListener, PopupMenu.OnDismissListener onDismissListener) {
        PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(activity, R.style.specter_PopupMenu), view);
        popupMenu.inflate(menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        popupMenu.setOnDismissListener(onDismissListener);
        popupMenu.show();
    }

}