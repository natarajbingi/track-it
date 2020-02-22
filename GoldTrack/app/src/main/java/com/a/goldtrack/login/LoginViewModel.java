package com.a.goldtrack.login;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    ObservableField<String> email = null;
    ObservableField<String> pwd = null;
    private LoginDataHandler view;

    void SetView(LoginDataHandler view) {
        email = new ObservableField<>();
        pwd = new ObservableField<>();
        this.view = view;
    }


    public void loginMe() {
        view.onClickTextView();
    }

    public void loginBtn() {
        boolean valid = true;
        if (email.get() == null || (email.get().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.get()).matches())) {
            view.onSetEmailError(true);
            valid = false;
        } else {
            view.onSetEmailError(false);
            // _emailText.setError(null);
        }

        if (pwd.get() == null || (pwd.get().isEmpty() || pwd.get().length() < 4 || pwd.get().length() > 10)) {
            view.onSetPwdError(true);
            valid = false;
        } else {
            view.onSetPwdError(false);
        }

        if (valid) {
            view.onClickLoginBtn();
        } else {
            view.onClickLoginFailed();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public TextWatcher emailWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.set(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email.set(editable.toString());
            }
        };
    }

    public TextWatcher pwdWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pwd.set(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                Log.d("logId", view.getId() + "");
                pwd.set(editable.toString());
            }
        };
    }

}
