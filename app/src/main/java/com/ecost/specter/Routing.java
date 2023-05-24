package com.ecost.specter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.ecost.specter.auth.AuthActivity;
import com.ecost.specter.menu.MainMenuActivity;

public class Routing extends AppCompatActivity {

    public static String accessToken;
    /*public static boolean auth, authAdmin;
    public static Integer authId, authEcostId, settingsSection, appLanguage, appTheme;
    public static String authUserName, authShortUserLink;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*appTheme = PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_THEME", 0);
        if (appTheme == 1) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (appTheme == 2) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);*/
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 31) setContentView(R.layout.splash_screen);
        else SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true);

        accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("ACCESS_TOKEN", null);
        /*auth = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("AUTH", false);
        authId = PreferenceManager.getDefaultSharedPreferences(this).getInt("SPECTER_ID", 0);
        authEcostId = PreferenceManager.getDefaultSharedPreferences(this).getInt("ECOST_ID", 0);
        authUserName = PreferenceManager.getDefaultSharedPreferences(this).getString("USER_NAME", null);
        authShortUserLink = PreferenceManager.getDefaultSharedPreferences(this).getString("SHORT_USER_LINK", null);
        appLanguage = PreferenceManager.getDefaultSharedPreferences(this).getInt("APP_LANGUAGE", 0);
        settingsSection = PreferenceManager.getDefaultSharedPreferences(this).getInt("SETTINGS_SECTION", 0);

        if (!auth) pushPreferenceLanguage(this, Locale.getDefault().getLanguage().equals("ru") ? 0 : 1);
        if (appLanguage == 0) changeLocale(this, new Locale("ru"));
        else changeLocale(this, new Locale("en"));
        if (Integer.parseInt(String.valueOf(taskSupportVersion.getResult().getValue())) > VERSION_CODE) startActivity(new Intent(this, HardUpdateActivity.class));*/
        if (accessToken == null) startActivity(new Intent(this, AuthActivity.class));
        else startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    public static void putAccessToken(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ACCESS_TOKEN", value).apply();
        accessToken = value;
    }

    /*public static void pushPreferenceAuth(Context context, Boolean value) {
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

    /*public static void changeLocale(Activity activity, Locale locale) {
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
    }*/

}