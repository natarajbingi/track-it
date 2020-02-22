package com.a.goldtrack.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityRegistrationBinding;
import com.a.goldtrack.login.LoginActivity;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity implements RegisterDataHandler {

    private static final String TAG = "RegistrationActivity";

    EditText _nameText;
    EditText _addressText;
    EditText _emailText;
    EditText _mobileText;
    EditText _passwordText;
    /* @BindView(R.id.input_reEnterPassword)
     EditText _reEnterPasswordText;*/
    Button btn_register;
    TextView _loginLink;

    RegisterViewModel registerViewModel;
    ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registration);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        registerViewModel.SetView(this);
        binding.setRegModel(registerViewModel);
//        ButterKnife.bind(this);
//
//        btn_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signup();
//            }
//        });
//
//        _loginLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Finish the registration screen and return to the Login activity
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });
    }


//    public void signup() {
//        Log.d(TAG, "Signup");
//
//        if (!validate()) {
//            onSignupFailed();
//            return;
//        }
//
//        btn_register.setEnabled(false);
//
//        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();
//
//        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
//        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
//        String password = _passwordText.getText().toString();
//        //  String reEnterPassword = _reEnterPasswordText.getText().toString();
//
//        // TODO: Implement your own signup logic here.
//
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
//    }


    public void onSignupSuccess() {
//        btn_register.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toasty.error(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

//        btn_register.setEnabled(true);
    }

    @Override
    public void registerMe() {

    }

    @Override
    public void onClickTextViewLogin() {

        // Finish the registration screen and return to the Login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

//    public boolean validate() {
//        boolean valid = true;
//
//        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
//        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
//        String password = _passwordText.getText().toString();
//        // String reEnterPassword = _reEnterPasswordText.getText().toString();
//
//        if (name.isEmpty() || name.length() < 3) {
//            _nameText.setError("at least 3 characters");
//            valid = false;
//        } else {
//            _nameText.setError(null);
//        }
//
//        if (address.isEmpty()) {
//            _addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            _addressText.setError(null);
//        }
//
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }
//
//        if (mobile.isEmpty() || mobile.length() != 10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//            _passwordText.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        } else {
//            _passwordText.setError(null);
//        }
//
//       /* if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
//            _reEnterPasswordText.setError("Password Do not match");
//            valid = false;
//        } else {
//            _reEnterPasswordText.setError(null);
//        }*/
//
//        return valid;
//    }
}
