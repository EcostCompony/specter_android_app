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
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
    private Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 31) setContentView(R.layout.splash_screen);
        else SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true);

        accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("ACCESS_TOKEN", null);
        putAppTheme(this, PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_THEME", 0));
        putAppLanguage(this, accessToken == null ? (Locale.getDefault().getLanguage().equals("ru") ? 1 : 0) : PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_LANGUAGE", 0));

        if (accessToken == null) startActivity(new Intent(this, AuthActivity.class));
        else {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    response = new API("http://213.219.214.94:3501/api/method/account.get?v=1.0", accessToken).call();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (response.getError() != null || response.getRes() == null) {
                            putAccessToken(this, null);
                            startActivity(new Intent(this, AuthActivity.class));
                        } else {
                            putUserName(this, response.getRes().getName());
                            putUserShortLink(this, response.getRes().getShortLink());
                            startActivity(new Intent(this, MainMenuActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
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

    /*public static String pluralForm(Integer i, String form1, String form2, String form3, Boolean declination) {
        if (i == 1) return i + " " + form1;
        else if (!declination) return i + " " + form2;
        i = i%100;

        if (i == 1) return i + " " + form1;
        else if (i == 0 || (i > 4 && i < 20)) return i + " " + form3;
        else return i + " " + form2;
    }*/

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

    /*public static String translateData(long unixDate, String pattern) {
        Date date = new Date(unixDate * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return simpleDateFormat.format(date);
    }

    public static void popupMenu(Activity activity, View view, int menu, PopupMenu.OnMenuItemClickListener onMenuItemClickListener, PopupMenu.OnDismissListener onDismissListener) {
        PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(activity, R.style.specter_PopupMenu), view);
        popupMenu.inflate(menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        popupMenu.setOnDismissListener(onDismissListener);
        popupMenu.show();
    }

    public static void AgreeAlertDialog(Activity activity, ViewGroup viewGroup, String title, String description, View.OnClickListener onClickListener) {
        View alertDialogView = LayoutInflater.from(activity).inflate(R.layout.agree_alert_dialog, viewGroup, false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(alertDialogView);
        ((TextView) alertDialogView.findViewById(R.id.header)).setText(title);
        ((TextView) alertDialogView.findViewById(R.id.description)).setText(description);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogView.findViewById(R.id.button_yes).setOnClickListener(onClickListener);
        alertDialogView.findViewById(R.id.button_yes).setOnClickListener(viewYes -> alertDialog.cancel());
        alertDialogView.findViewById(R.id.button_cancel).setOnClickListener(view -> alertDialog.cancel());
        alertDialog.show();
    }*/

}