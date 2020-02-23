package com.a.goldtrack.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.a.goldtrack.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.READ_PHONE_STATE;

public class Constants {
    public static final String BaseUrlTesting = "http://13.59.10.105:8080/tracking_services/api/";
    public static final String BaseUrlTT = "http://13.59.10.105:8080/campusquo_services/api/";
    public static final String BaseUrl = BaseUrlTesting;
    public static final String keepMeSignedStr = "keepMeSignedStr";
    public static final String appVersion = "1.0.1";

    public static final String companyId = "companyId";
    public static final String userLogin = "userLogin";
    public static final String userId = "userId";
    public static final String pwdId = "pwdId";
    public static final int error = 0;
    public static final int success = 1;
    public static final int info = 2;
    public static final int warning = 3;
    public static final int custom = 4;

    public static enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    // Methods


    public static void Toasty(Context ctx, String Msg, int type) {
        switch (type) {
            case error:
                Toasty.error(ctx, Msg, Toast.LENGTH_LONG, true).show();
                break;
            case success:
                Toasty.success(ctx, Msg, Toast.LENGTH_LONG, true).show();
                break;
            case info:
                Toasty.info(ctx, Msg, Toast.LENGTH_LONG, true).show();
                break;
            case warning:
                Toasty.warning(ctx, Msg, Toast.LENGTH_SHORT, true).show();
                break;
            case custom:
                /*Toasty.Config.getInstance()
                        .setToastTypeface(Typeface.createFromAsset(getAssets(), "PCap Terminal.otf"))
                        .allowQueue(false)
                        .apply();
                Toasty.custom(ctx, Msg, ctx.getResources().getDrawable(R.drawable.ic_menu_manage),
                        android.R.color.black, android.R.color.holo_green_light, Toast.LENGTH_SHORT, true, true).show();
                Toasty.Config.reset();*/ // Use this if you want to use the configuration above only once
                break;
        }
    }

    public static boolean checkSecurPermission(Context ctx) {

//        int result1 = ContextCompat.checkSelfPermission(ctx, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(ctx, READ_PHONE_STATE);
//        int result3 = ContextCompat.checkSelfPermission(ctx, READ_EXTERNAL_STORAGE);
//        int result4 = ContextCompat.checkSelfPermission(ctx, WRITE_EXTERNAL_STORAGE);

        return /*result4 == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&*/
                result2 == PackageManager.PERMISSION_GRANTED /*&& result3 == PackageManager.PERMISSION_GRANTED*/;

    }

    public static void logPrint(String call, Object req, Object res) {
        Gson g = new Gson();
        Log.d("Request-", call + "");
        Log.d("LogReq-", g.toJson(req));
        Log.d("LogRes-", g.toJson(res));
    }

    public static String getDateyyyymmmdd(String dte) {
        Date date1 = new Date(dte);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        return s.format(date1);
    }

    public static String getDateNowyyyymmmdd() {
        Date date1 = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        return s.format(date1);
    }

    public static String getDateNowAll() {
        Date date1 = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");

        return s.format(date1);
    }

    public static String getDateTab(String timeZone, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        s.setTimeZone(TimeZone.getTimeZone(timeZone));
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    /* Global Alertdialog for application*/
    public static void alertDialogShow(Context context, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle(context.getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();

        // show it
        alert.show();
    }

    /* Global Alertdialog for application*/
    public static AlertDialog alertDialogShow(Context context,
                                              String message,
                                              final DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onClickListener.onClick(dialog, which);
            }
        });
        AlertDialog alert = alertDialog.create();

        // show it
        alert.show();
        return alert;
    }


    public static Dialog popUpImg(Context context, Uri uri, String ImgCredit, String encodedImgORUrl, Bitmap bitmap, String Type) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.photo_popup);
        RelativeLayout photoId = (RelativeLayout) dialog.findViewById(R.id.photoId);

        //        RelativeLayout signId = (RelativeLayout) dialog.findViewById(R.id.signId);
        //        signId.setVisibility(View.GONE);
        photoId.setVisibility(View.VISIBLE);
        ImageView closeImgPopUp = (ImageView) dialog.findViewById(R.id.closeImgPopUp);
        ImageView imgPopup = (ImageView) dialog.findViewById(R.id.imgPopup);
        TextView headingText = (TextView) dialog.findViewById(R.id.headingText);
        headingText.setText(ImgCredit);

        closeImgPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        if (Type.equals("URI")) {
            imgPopup.setImageURI(uri);
        } else if (Type.equals("NOT_URL")) {
            byte[] bytes = Base64.decode(encodedImgORUrl, Base64.DEFAULT);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgPopup.setImageBitmap(bitmap1);
        } else if (Type.equals("bitMap")) {
            imgPopup.setImageBitmap(bitmap);
        } else {//if (!Type.equals("URL")) {
            try {
                // Loads given image
                //  int size = (int) Math.ceil(Math.sqrt(800 * 600));
                Picasso.get()
                        .load(encodedImgORUrl)
                        // .transform(new BitmapTransform(800, 600))
                        // .resize(size, size)
                        // .centerInside()
                        // .noPlaceholder()
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.load_failed)
                        .into(imgPopup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dialog.show();
        return dialog;
    }

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
}
