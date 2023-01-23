package com.ecost.specter;

import static com.ecost.specter.BuildConfig.VERSION_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Routing extends AppCompatActivity {

    public static final DatabaseReference myDB = FirebaseDatabase.getInstance().getReference();
    public static boolean auth;
    public static Integer authId, authEcostId;
    public static String authShortUserLink, authName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true );

        auth = PreferenceManager.getDefaultSharedPreferences(Routing.this).getBoolean("AUTH", false);
        authId = PreferenceManager.getDefaultSharedPreferences(Routing.this).getInt("UID", 0);
        authEcostId = PreferenceManager.getDefaultSharedPreferences(Routing.this).getInt("ECOST_ID", 0);
        authShortUserLink = PreferenceManager.getDefaultSharedPreferences(Routing.this).getString("LINK", "undefined");

        myDB.child("users").child(String.valueOf(authId)).child("name").get().addOnCompleteListener(task -> {
            Object name = task.getResult().getValue();
            if (name != null) pushPreferenceName(this, String.valueOf(name));
            if (PreferenceManager.getDefaultSharedPreferences(Routing.this).getString("NAME", null) != null) authName = PreferenceManager.getDefaultSharedPreferences(Routing.this).getString("NAME", "no");
            else authName = "user" + authId;
        });

        myDB.child("support_version").get().addOnCompleteListener(vTask ->
            myDB.child("specter").child("users").child(String.valueOf(authId)).get().addOnCompleteListener(uTask -> {
                if (Integer.parseInt(String.valueOf(vTask.getResult().getValue())) > VERSION_CODE) {
                    pushPreferenceAuth(this, false);
                    startActivity(new Intent(Routing.this, OldVersionActivity.class));
                } else if (uTask.getResult().getValue() == null || !auth) {
                    pushPreferenceAuth(this, false);
                    startActivity(new Intent(Routing.this, AuthActivity.class));
                } else startActivity(new Intent(Routing.this, MainMenuActivity.class));
                finish();
            })
        );
    }

    public static void pushPreferenceAuth(Context context, Boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("AUTH", value).apply();
        auth = value;
    }

    public static void pushPreferenceShortUserLink(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("LINK", value).apply();
        authShortUserLink = value;
    }

    public static void pushPreferenceId(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("UID", value).apply();
        authId = value;
    }

    public static void pushPreferenceEcostId(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("ECOST_ID", value).apply();
        authEcostId = value;
    }

    public static void pushPreferenceName(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("NAME", value).apply();
        authName = value;
    }

    public static void signOut(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        auth = false;
        authShortUserLink = null;
        authId = null;
        authEcostId = 0;
        authName = null;
    }

    public static String hash(String password) {
        String[] symbols = {".","M","%","$","Ш","V","p","T","#","М","и","i","N","U","д","ч","a","э","B","В","r","м","л","Г","7","L","Ц","4","z","я","c","v","б","ъ","Ю","Б","1","Ы","н","l","F","ц","Y","d","t","+","-","с","3","ё","Л","п","C","р","К","ю","w","О","Н","И","q","!","x","Е","y","С","@","Э","а","Ь","Я","I","й","f","n","m","G","Ж","ы","s","Ч","Щ","П","к","в","е","ь","K","2","P","S","0","D","Ъ","A","Q","e","J","h","ш","_","Ё","H","b","А","Z","Р","X","з","ф","о","R","6","g","г","9","Т","O","Д","у","u","ж","o","Й","щ","Ф","j","5","Х","8","W","З","У","х","т","k"};
        String[] arrayPassword = password.split("");

        if (password.equals("")) return "";

        for (int i = 0; i < arrayPassword.length; i++) {
            int index = String.join("", symbols).indexOf(arrayPassword[i]);
            if (index+34 > 136) arrayPassword[i] = symbols[index-57];
            else arrayPassword[i] = symbols[index+34];
            arrayPassword[i] = Integer.toHexString(index);
        }

        return String.join("", arrayPassword);
    }

    public static String declension(Integer i, String nominative, String genitive, String plural, Boolean notDeclension) {
        String[] arrNum = i.toString().split("");
        int j = Integer.parseInt(arrNum[arrNum.length-1]);

        if (i == 1) return i + " " + nominative;
        else if (notDeclension) return i + " " + genitive;
        else if (i == 0 || (i > 4 && i < 20)) return i + " " + plural;
        else if (j == 1) return i + " " + nominative;
        else if (j > 1 && j < 5) return i + " " + genitive;
        else if (j == 0 || (j > 4 && j < 10)) return i + " " + plural;

        return i + " " + nominative;
    }

    public static void popup(Activity activity, View view, String context) {
        @SuppressLint("InflateParams") View popupView =  activity.getLayoutInflater().inflate(R.layout.popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

        ((TextView) popupView.findViewById(R.id.text_error)).setText(context);

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

}