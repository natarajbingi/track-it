package com.a.goldtrack.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.READ_PHONE_STATE;

public class Constants {
    public static final String BaseUrlTesting = "http://13.59.10.105:8080/tracking_services/api/";
    public static final String BaseUrlTT = "http://13.59.10.105:8080/campusquo_services/api/";
    public static final String BaseUrl = BaseUrlTesting;
    public static final String keepMeSignedStr = "keepMeSignedStr";
    public static final String appVersion = "1.0.1";
    private static final String IMAGE_DIRECTORY = "/GTrackImage";

    public static final String companyId = "companyId";
    public static final String userLogin = "userLogin";
    public static final String userName = "userName";
    public static final String userIdID = "userIdID"; // emailID
    public static final String userId = "userId"; // id
    public static final String pwdId = "pwdId";
    public static final String roles = "roles";
    public static final String dorpDownSession = "dorpDownSession";
    public static final String sesImgData = "sesImgData";

    /* Img add Strings
     * */
    public static final String imageTableTRANSACTION_IMAGE = "TRANSACTION_IMAGE";
    public static final String imageTableCUSTOMER_IMAGE = "CUSTOMER_IMAGE";
    public static final String imageTypeDL = "DL";
    public static final String imageTypeAADHAR = "AADHAR";
    public static final String imageTypeANYTHING = "ANYTHING";
    public static final String actionTypeADD = "ADD";
    public static final String actionTypeREMOVE = "REMOVE";


    public static final int error = 0;
    public static final int success = 1;
    public static final int info = 2;
    public static final int warning = 3;
    public static final int custom = 4;
    private static ProgressDialog pd;

    public static enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    // Methods

    public static boolean isConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) GTrackApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    // Showing the status in Snackbar
    public static void showSnack(boolean isConnected, View view) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static String priceWithDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("##,##,##,###.00");
        return formatter.format(price);
    }

    public static String priceWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("##,##,##,###.##");
        return formatter.format(price);
    }

    public static String priceToString(String amount) {
        // int i = Integer.parseInt();
        if (!amount.equals("")) {
            double price = Double.parseDouble(amount.replaceAll(",", ""));
            String toShow = priceWithoutDecimal(price);
            if (toShow.indexOf(".") > 0) {
                return priceWithDecimal(price);
            } else {
                return priceWithoutDecimal(price);
            }
        } else {
            return "";
        }
    }

    public static void setSpinners(Spinner spr, String[] array) {
        // -----------------------------------------------
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(GTrackApplication.getInstance().getApplicationContext(),
                R.layout.custom_spinner,
                array);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // The drop down view
        spr.setAdapter(spinnerArrayAdapter);
    }

    public static String isEmptyReturn0(String str) {
        return str == null ? "0" : str.isEmpty() ? "0" : str;
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String str = String.format("%06d", number);
        if (str.split("")[0].equals("0")) getRandomNumberString();
        // this will convert any number sequence into 6 character.
        return str;
    }

    public static String fileToStringOfBitmap(File mPhotoFile) {
        // File mSaveBit; // Your image file
        String filePath = mPhotoFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        // mImageView.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    public static Bitmap stringToBitmap(String imageString) {
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        //image.setImageBitmap(decodedImage);
        return decodedImage;
    }

    public static void Toasty(Context ctx, String Msg) {
        Toasty.info(ctx, Msg, Toast.LENGTH_LONG, true).show();
    }

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

    public static String getMiliToDateyyyymmmdd(String dte) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        long milliSeconds = Long.parseLong(dte);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String getDateNowyyyymmmdd() {
        Date date1 = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        return s.format(date1);
    }

    public static void setGilde(String logoImagePath, ImageView view) {

        Glide.with(GTrackApplication.getInstance())
                .load(logoImagePath)

                .apply(new RequestOptions()
                        //  .centerCrop()
                        //  .circleCrop()
                        .error(R.drawable.profile_icon_menu)
                        .placeholder(R.drawable.placeholder))
                .into(view);
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

    public static String saveImage(Context context, Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    /* Global Alertdialog for application*/
    public static AlertDialog alertDialogShowWithCancel(Context context,
                                                        String message,
                                                        final DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onClickListener.onClick(dialogInterface, i);
            }
        });
        AlertDialog alert = alertDialog.create();

        // show it
        alert.show();
        return alert;
    }

    public static AlertDialog alertDialogShowOneButton(Context context,
                                                       String message,
                                                       final DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onClickListener.onClick(dialogInterface, i);
            }
        });
        AlertDialog alert = alertDialog.create();

        // show it
        alert.show();
        return alert;
    }

    public static void datePicker(final Context ctx, final EditText text) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        text.setText(dateFormatter.format(newDate.getTime()));

                    }
                }, mYear, mMonth, mDay);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.DAY_OF_MONTH, mDay);
        maxDate.set(Calendar.MONTH, mMonth);
        maxDate.set(Calendar.YEAR, mYear);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();


    }


    public static void getCalender(Context context, final EditText editText) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DatePickerDialog fromDatePickerDialog;
        Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog finalFromDatePickerDialog;
        fromDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editText.setText(dateFormatter.format(newDate.getTime()));


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        newCalendar.add(Calendar.MONTH, 1);
        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        fromDatePickerDialog.show();

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

    public static void showProgress(Context ctx) {
        if (!((Activity) ctx).isFinishing()) {
            pd = new ProgressDialog(ctx);
            pd.setMessage("loading");
            pd.setCancelable(false);
            pd.show();
        }
    }


    public static void hideProgress(Context ctx) {
        if (pd != null) {
            if (!((Activity) ctx).isFinishing()) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }
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


    public static String listme = "{\n" +
            "  \"branchesList\": [\n" +
            "    {\n" +
            "      \"companyId\": 1,\n" +
            "      \"branchAddress1\": \"No.123, Vijaynagr, Gulbarga\",\n" +
            "      \"branchCity\": \"Gulbarga\",\n" +
            "      \"branchCode\": \"DYU_GUL_02\",\n" +
            "      \"branchDesc\": \"It is in Vijay Nagar, Gulbarga\",\n" +
            "      \"branchName\": \"Gulbarga_Branch\",\n" +
            "      \"branchPhNumber\": \"123456889\",\n" +
            "      \"branchPin\": \"5853555\",\n" +
            "      \"response\": null,\n" +
            "      \"success\": false,\n" +
            "      \"id\": 1,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"companyId\": 1,\n" +
            "      \"branchAddress1\": \"Bangalore\",\n" +
            "      \"branchCity\": \"Bangalore\",\n" +
            "      \"branchCode\": \"DYU_20200225123918\",\n" +
            "      \"branchDesc\": \"test generator branches\",\n" +
            "      \"branchName\": \"Bangalore branch\",\n" +
            "      \"branchPhNumber\": \"9988669966\",\n" +
            "      \"branchPin\": \"964664\",\n" +
            "      \"response\": null,\n" +
            "      \"success\": false,\n" +
            "      \"id\": 3,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"companyId\": 1,\n" +
            "      \"branchAddress1\": \"yugfvvv\",\n" +
            "      \"branchCity\": \"vhbh\",\n" +
            "      \"branchCode\": \"DYU_20200225124941\",\n" +
            "      \"branchDesc\": \"ghghhg\",\n" +
            "      \"branchName\": \"wahhh and\",\n" +
            "      \"branchPhNumber\": \"1226659889\",\n" +
            "      \"branchPin\": \"524488\",\n" +
            "      \"response\": null,\n" +
            "      \"success\": false,\n" +
            "      \"id\": 4,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    }\n" +
            "  ],\n" +
            "  \"customerList\": [\n" +
            "    {\n" +
            "      \"uniqueId\": \"2341\",\n" +
            "      \"firstName\": \"Test1\",\n" +
            "      \"lastName\": \"tt1\",\n" +
            "      \"mobileNum\": \"9980766166\",\n" +
            "      \"emailId\": \"test1@gmail.com\",\n" +
            "      \"address1\": \"Channasandra1\",\n" +
            "      \"address2\": \"Bangalore1\",\n" +
            "      \"pin\": \"578886\",\n" +
            "      \"id\": 1,\n" +
            "      \"state\": \"Karnataka1\",\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"uniqueId\": \"2158\",\n" +
            "      \"firstName\": \"one days\",\n" +
            "      \"lastName\": \"second hand\",\n" +
            "      \"mobileNum\": \"9980766166\",\n" +
            "      \"emailId\": \"oneday@gmail.com\",\n" +
            "      \"address1\": \"test generator\",\n" +
            "      \"address2\": \"best wishes\",\n" +
            "      \"pin\": \"986686\",\n" +
            "      \"id\": 2,\n" +
            "      \"state\": \"karnataka\",\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    }\n" +
            "  ],\n" +
            "  \"itemsList\": [\n" +
            "    {\n" +
            "      \"companyID\": 1,\n" +
            "      \"commodity\": \"GOLD\",\n" +
            "      \"itemName\": \"Chain\",\n" +
            "      \"itemDesc\": \"Gold chain longp\",\n" +
            "      \"id\": 1,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"companyID\": 1,\n" +
            "      \"commodity\": \"GOLD\",\n" +
            "      \"itemName\": \"Chain Long\",\n" +
            "      \"itemDesc\": \"Gold chain long long\",\n" +
            "      \"id\": 2,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"companyID\": 1,\n" +
            "      \"commodity\": \"GOLD\",\n" +
            "      \"itemName\": \"silevr chain \",\n" +
            "      \"itemDesc\": \"added chai\",\n" +
            "      \"id\": 3,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"companyID\": 1,\n" +
            "      \"commodity\": \"SILVER\",\n" +
            "      \"itemName\": \"chain KMC silver\",\n" +
            "      \"itemDesc\": \"silver ade\",\n" +
            "      \"id\": 4,\n" +
            "      \"createdBy\": null,\n" +
            "      \"updatedBy\": null,\n" +
            "      \"createdDt\": null,\n" +
            "      \"updatedDt\": null\n" +
            "    }\n" +
            "  ],\n" +
            "  \"response\": \"Data fetched successfully\",\n" +
            "  \"success\": true\n" +
            "}";
}
