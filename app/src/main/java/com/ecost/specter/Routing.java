package com.ecost.specter;

import static com.ecost.specter.BuildConfig.VERSION_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import com.ecost.specter.auth.AuthActivity;
import com.ecost.specter.menu.MainMenuActivity;
import com.ecost.specter.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Routing extends AppCompatActivity {

    public static final DatabaseReference myDB = FirebaseDatabase.getInstance().getReference();
    public static boolean auth, authAdmin;
    public static Integer authId, authEcostId, settingsSection, appLanguage, appTheme;
    public static String authUserName, authShortUserLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appTheme = PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_THEME", 0);
        if (appTheme == 1) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (appTheme == 2) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 31) {
            setContentView(R.layout.splash_screen);
        } else {
            SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true);
        }

        auth = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("AUTH", false);
        authId = PreferenceManager.getDefaultSharedPreferences(this).getInt("SPECTER_ID", 0);
        authEcostId = PreferenceManager.getDefaultSharedPreferences(this).getInt("ECOST_ID", 0);
        authUserName = PreferenceManager.getDefaultSharedPreferences(this).getString("USER_NAME", null);
        authShortUserLink = PreferenceManager.getDefaultSharedPreferences(this).getString("SHORT_USER_LINK", null);
        appLanguage = PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_LANGUAGE", 0);
        settingsSection = PreferenceManager.getDefaultSharedPreferences(this).getInt("SETTINGS_SECTION", 0);

        myDB.child("specter").child("support_version").get().addOnCompleteListener(taskSupportVersion ->
            myDB.child("specter").child("users").child(String.valueOf(authId)).get().addOnCompleteListener(taskTestUser ->
                myDB.child("specter").child("users").child(String.valueOf(authId)).child("app_version").get().addOnCompleteListener(taskUserVersion -> {
                    if (!auth) pushPreferenceLanguage(this, Locale.getDefault().getLanguage().equals("ru") ? 0 : 1);
                    if (appLanguage == 0) changeLocale(this, new Locale("ru"));
                    else changeLocale(this, new Locale("en"));
                    if (Integer.parseInt(String.valueOf(taskSupportVersion.getResult().getValue())) > VERSION_CODE) startActivity(new Intent(this, HardUpdateActivity.class));
                    else if (auth && taskTestUser.getResult().getValue() != null) {
                        myDB.child("specter").child("users").child(authId.toString()).child("admin").get().addOnCompleteListener(task -> {
                            authAdmin = Boolean.TRUE.equals(task.getResult().getValue(Boolean.class));
                            if (!String.valueOf(taskUserVersion.getResult().getValue()).equals(String.valueOf(VERSION_CODE))) myDB.child("specter").child("users").child(String.valueOf(authId)).child("app_version").setValue(VERSION_CODE);
                            Intent intent = new Intent(this, MainMenuActivity.class);
                            intent.putExtra("CREATE", true);
                            startActivity(intent);
                        });
                    } else {
                        signOut(this);
                        startActivity(new Intent(this, AuthActivity.class));
                    }
                    finish();
                })
            )
        );
    }

    public static void pushPreferenceAuth(Context context, Boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("AUTH", value).apply();
        auth = value;
    }

    public static void pushPreferenceId(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("SPECTER_ID", value).apply();
        authId = value;
    }

    public static void pushPreferenceEcostId(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("ECOST_ID", value).apply();
        authEcostId = value;
    }

    public static void pushPreferenceUserName(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("USER_NAME", value).apply();
        authUserName = value;
    }

    public static void pushPreferenceShortUserLink(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SHORT_USER_LINK", value).apply();
        authShortUserLink = value;
    }

    public static void pushPreferenceLanguage(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("APP_LANGUAGE", value).apply();
        appLanguage = value;
    }

    public static void pushPreferenceTheme(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("APP_THEME", value).apply();
        appTheme = value;
    }

    public static void pushPreferenceSettingsSection(Context context, Integer value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("SETTINGS_SECTION", value).apply();
        settingsSection = value;
    }

    public static void signOut(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        auth = false;
        authId = 0;
        authEcostId = 0;
        authUserName = null;
        authShortUserLink = null;
    }

    public static String hash(String password) {
        String[] symbols = {".","M","%","$","Ш","V","p","T","#","М","и","i","N","U","д","ч","a","э","B","В","r","м","л","Г","7","L","Ц","4","z","я","c","v","б","ъ","Ю","Б","1","Ы","н","l","F","ц","Y","d","t","+","-","с","3","ё","Л","п","C","р","К","ю","w","О","Н","И","q","!","x","Е","y","С","@","Э","а","Ь","Я","I","й","f","n","m","G","Ж","ы","s","Ч","Щ","П","к","в","е","ь","K","2","P","S","0","D","Ъ","A","Q","e","J","h","ш","_","Ё","H","b","А","Z","Р","X","з","ф","о","R","6","g","г","9","Т","O","Д","у","u","ж","o","Й","щ","Ф","j","5","Х","8","W","З","У","х","т","k"};
        String[] arrayPassword = password.split("");

        if (password.equals("")) return "";

        for (int i = 0; i < arrayPassword.length; i++) arrayPassword[i] = Integer.toHexString(String.join("", symbols).indexOf(arrayPassword[i]));

        return String.join("", arrayPassword);
    }

    public static String pluralForm(Integer i, String form1, String form2, String form3, Boolean declination) {
        if (i == 1) return i + " " + form1;
        else if (!declination) return i + " " + form2;
        i = i%100;

        if (i == 1) return i + " " + form1;
        else if (i == 0 || (i > 4 && i < 20)) return i + " " + form3;
        else return i + " " + form2;
    }

    public static void popup(Activity activity, View view, int type, String text) {
        @SuppressLint("InflateParams") View popupView =  activity.getLayoutInflater().inflate(type == 2 ? R.layout.popup_window : R.layout.error_popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

        ((TextView) popupView.findViewById(R.id.text_error)).setText(text);
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                activity.runOnUiThread(popupWindow::dismiss);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void changeLocale(Activity activity, Locale locale) {
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        activity.getBaseContext().getResources().updateConfiguration(configuration, activity.getBaseContext().getResources().getDisplayMetrics());
    }

    public static String translateData(long unixDate, String pattern) {
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
    }

}