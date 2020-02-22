package com.a.goldtrack.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a.goldtrack.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

/**
 * Created by Nataraj on 07-01-2018.
 */

public class Sessions {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int private_mode = 0;
    private static final String PREF_NAME = "Kidzogram";

    public Sessions(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, private_mode);
        editor = pref.edit();
    }

    public static void setUserString(Context c, String userObject, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, userObject);
        editor.commit();
    }

    public static String getUserString(Context ctx, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String userObject = pref.getString(key, null);
        return userObject;
    }

    public static void removeUserKey(Context ctx, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        settings.edit().remove(key).commit();
    }
    // --------------------------------------------------------------------------------------

    public static void setUserObj(Context c, Object userObject, String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = mPrefs.edit();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userObject);
        prefsEditor.putString(key, json);
        prefsEditor.commit();

    }

    public static Object getUserObj(Context ctx, String key, Class type) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Gson gson = new Gson();
        String json = mPrefs.getString(key, "");
        return gson.fromJson(json, type);
    }


}
