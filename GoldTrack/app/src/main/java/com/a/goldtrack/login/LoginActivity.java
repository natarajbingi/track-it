package com.a.goldtrack.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.a.goldtrack.HomeActivity;
import com.a.goldtrack.MainActivity;
import com.a.goldtrack.Model.TeacherLoginReq;
import com.a.goldtrack.Model.TeacherLoginRes;
import com.a.goldtrack.R;
import com.a.goldtrack.company.CompanyActivity;
import com.a.goldtrack.network.APIService;
import com.a.goldtrack.network.RetrofitClient;
import com.a.goldtrack.register.RegistrationActivity;
import com.a.goldtrack.databinding.ActivityLoginBinding;
import com.a.goldtrack.users.UserForCompanyActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements LoginDataHandler {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    LoginViewModel loginViewModel;
    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    Context context;
    boolean keepMeSignedStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.SetView(this);
        context = LoginActivity.this;
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
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

        /*binding.edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.email.set(s.toString());
            }
        });*/


        /*binding.edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.pwd.set(s.toString());
            }
        });*/

        /*binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });*/

//        _signupLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });
    }


    public void login() {
        Log.d(TAG, "Login");
        // TODO: Implement your own authentication logic here.

        TeacherLoginReq req = new TeacherLoginReq();
        req.appVersion = "1.0.1";
        req.deviceName = "Testing";
        req.imeiNumber = "1.0.1";
        req.registrationID = "1.0.1";
        req.password = "darshanraykar1@gmail.com";
        req.userName = "demo123";
        keepMeSignedStr = binding.keepMeSigned.isChecked();

        RetrofitClient retrofitSet = new RetrofitClient();
        Retrofit retrofit = retrofitSet.getClient(Constants.BaseUrlTT);
        APIService apiService = retrofit.create(APIService.class);
        Call<TeacherLoginRes> call = apiService.TEACHER_LOGIN_RES_CALL(req);


        progressDialog.show();
        call.enqueue(new Callback<TeacherLoginRes>() {
            @Override
            public void onResponse(Call<TeacherLoginRes> call, Response<TeacherLoginRes> response) {
                progressDialog.dismiss();
                Constants.logPrint(call.request().toString(), req, response.body());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().success) {
                            if (keepMeSignedStr) {
                                Sessions.setUserString(context, "TRUE", Constants.keepMeSignedStr);
                            } else {
                                Sessions.setUserString(context, "FALSE", Constants.keepMeSignedStr);
                            }
                            onLoginSuccess();
                        } else {
                            Constants.alertDialogShow(context, response.body().response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TeacherLoginRes> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Response:", "" + t);
                Constants.alertDialogShow(context, "Something went wrong, please try again");
                t.printStackTrace();
            }
        });

    }

    public void onLoginSuccess() {
        binding.btnLogin.setEnabled(true);

//        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//        Intent i = new Intent(LoginActivity.this, UserForCompanyActivity.class);
        Intent i = new Intent(LoginActivity.this, CompanyActivity.class);
        startActivity(i);
    }

    public void onLoginFailed() {
        Toasty.error(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        binding.btnLogin.setEnabled(true);
    }

    /*public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }*/

    @Override
    public void onClickTextView() {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onClickLoginBtn() {
//        login();
        onLoginSuccess();
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
}
