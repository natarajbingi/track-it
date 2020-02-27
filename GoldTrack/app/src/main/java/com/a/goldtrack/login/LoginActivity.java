package com.a.goldtrack.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.a.goldtrack.HomeActivity;
import com.a.goldtrack.Model.UserLoginReq;
import com.a.goldtrack.Model.UserLoginRes;
import com.a.goldtrack.R;
import com.a.goldtrack.customer.CustomerActivity;
import com.a.goldtrack.register.RegistrationActivity;
import com.a.goldtrack.databinding.ActivityLoginBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements LoginDataHandler {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    LoginViewModel loginViewModel;
    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    Context context;
    boolean keepMeSignedStr;

    String deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    String imeiNumber = UUID.randomUUID().toString();
    String appVersion = Constants.appVersion;
    String registrationID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = LoginActivity.this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.SetView(this);
        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        binding.setViewModel(loginViewModel);

        loginViewModel.email.set(binding.edEmail.getText().toString());
        loginViewModel.pwd.set(binding.edPassword.getText().toString());

        String LogInDirect = Sessions.getUserString(context, Constants.keepMeSignedStr);
        if (LogInDirect != null) {
            if (LogInDirect.equals("TRUE")) {
                onLoginSuccess();
            }
        }
    }


    public void onLoginSuccess() {
        binding.btnLogin.setEnabled(true);

//        Intent i = new Intent(LoginActivity.this, CustomerActivity.class);
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        //        Intent i = new Intent(LoginActivity.this, CompanyActivity.class);
        //        Intent i = new Intent(LoginActivity.this, UserForCompanyActivity.class);
        //        Intent i = new Intent(LoginActivity.this, CompanyBranchesActivity.class);
        //        Intent i = new Intent(LoginActivity.this, ItemsActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed() {
        Toasty.error(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        binding.btnLogin.setEnabled(true);
    }

    @Override
    public void onClickTextView() {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onClickLoginBtn() {

        UserLoginReq req = new UserLoginReq();
        req.appVersion = appVersion;
        req.deviceName = deviceName;
        req.imeiNumber = imeiNumber;
        req.registrationID = registrationID;
        req.userName = binding.edEmail.getText().toString().trim();
        req.password = binding.edPassword.getText().toString().trim();
        keepMeSignedStr = binding.keepMeSigned.isChecked();

        progressDialog.show();
        loginViewModel.loginCall(req);
    }

    @Override
    public void onClickLoginFailed() {
        onLoginFailed();
    }

    @Override
    public void onSetEmailError(boolean bool) {
        if (bool)
            binding.edEmail.setError("enter a valid email address");
        else
            binding.edEmail.setError(null);

    }

    @Override
    public void onSetPwdError(boolean bool) {
        if (bool)
            binding.edPassword.setError("between 4 and 10 alphanumeric characters");
        else
            binding.edPassword.setError(null);

    }

    @Override
    public void onLoginCallSuccess(UserLoginRes loginRes) {
        progressDialog.dismiss();
        if (loginRes.success) {
            if (keepMeSignedStr) {
                Sessions.setUserString(context, "TRUE", Constants.keepMeSignedStr);
            } else {
                Sessions.setUserString(context, "FALSE", Constants.keepMeSignedStr);
            }
            Sessions.setUserObj(context, loginRes.data, Constants.userLogin);
            Sessions.setUserString(context, loginRes.data.companyId, Constants.companyId);
            Sessions.setUserString(context, loginRes.data.userName, Constants.userId);
            Sessions.setUserString(context, loginRes.data.firstName + " " + loginRes.data.lastName, Constants.userName);
            Sessions.setUserString(context, binding.edPassword.getText().toString(), Constants.pwdId);
            onLoginSuccess();
        } else {
            Constants.alertDialogShow(context, loginRes.response);
        }
    }

    @Override
    public void onLoginError(String msg) {
        progressDialog.dismiss();
        Log.e(TAG, msg);
        Constants.alertDialogShow(context, "Something went wrong, please try again");
    }

}
