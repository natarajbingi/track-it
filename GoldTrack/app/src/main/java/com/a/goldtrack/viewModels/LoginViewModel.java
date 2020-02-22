package com.a.goldtrack.viewModels;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Interfaces.LoginResultCallBacks;
import com.a.goldtrack.Model.User;

public class LoginViewModel extends ViewModel {
    private User user;
    private LoginResultCallBacks loginResultCallBacks;

    public LoginViewModel(LoginResultCallBacks loginResultCallBacks) {
        this.loginResultCallBacks = loginResultCallBacks;
        this.user = new User();
    }

    public TextWatcher getEmailWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                user.setEmail(editable.toString());
            }
        };
    }

    public TextWatcher getPasswordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                user.setPassword(editable.toString());
            }
        };
    }

    public void onLoginClicked(View view) {
        if (user.isValidData()) {
            loginResultCallBacks.onSuccess("Login Success");
        } else {
            loginResultCallBacks.onError("Login Failed");
        }
    }

}
