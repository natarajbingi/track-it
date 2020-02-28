package com.a.goldtrack.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityOtpBinding;
import com.a.goldtrack.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class OtpActivity extends AppCompatActivity {

    OtpVIewModel vIewModel;
    ActivityOtpBinding binding;

    // PrefaranceData data;
    //  LoginDatum loginData;
    String otp;
    View display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        vIewModel = ViewModelProviders.of(this).get(OtpVIewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);
        binding.setOptModel(vIewModel);
        binding.resend.setEnabled(false);
        // requestSmsPermission();
        // setUpBrodcast();


        display = getWindow().getDecorView().getRootView();
        //  data=new PrefaranceData(this);
        //  loginData = new Gson().fromJson(data.getUserData(), LoginDatum.class);
        binding.numbver.setText("Verify +91 9980766166");


        new CountDownTimer(50000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.timer.setText("Resend Code in " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                binding.resend.setEnabled(true);
                binding.resend.setVisibility(View.VISIBLE);
                binding.timer.setText("Tap on resend");
            }

        }.start();

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = binding.otpedit.getText().toString();
                if (otp.length() < 4) {
//                    AppUtils.showSneak(display,"Invalid OTP");
                    Constants.Toasty(getApplicationContext(), "Invalid OTP", Constants.warning);
                } else {
                    verify(otp);
                }
            }
        });
        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.resend.setEnabled(false);

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                try {
//                    jsonObject.put("api_type", Constents.Api_type);
//                    jsonObject.put("Version", Constents.Version1_1);
//                    jsonObject.put("userID", data.getUserID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);

//                RestApiServices services=new RestApiServices(this);
//                services.resend_otp(this,jsonArray.toString());
            }
        });
    }

    public void setUpBrodcast() {
        LocalBroadcastManager.getInstance(OtpActivity.this).registerReceiver(receiver, new IntentFilter("otp"));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                binding.otpedit.setText(message);
                binding.submit.performClick();
            }
        }
    };


    public void verify(String otp) {
        binding.submit.setEnabled(false);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userOTP", Integer.parseInt(otp));
//            jsonObject.put("userID", data.getUserID());
//            jsonObject.put("api_type", Constents.Api_type);
//            jsonObject.put("mobile",loginData.getUserMobileNo());
//            jsonObject.put("Version", Constents.Version1_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);
//        RestApiServices services=new RestApiServices(this);
//        services.guestotp(this,jsonArray.toString());
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to cancel the registration process?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OtpActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
    }
}
