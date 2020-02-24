package com.a.goldtrack.login;

import com.a.goldtrack.Model.UserLoginRes;

public interface LoginDataHandler {

    void onClickTextView();

    void onClickLoginBtn();

    void onClickLoginFailed();

    void onSetEmailError(boolean bool);

    void onSetPwdError(boolean bool);

    void onLoginCallSuccess(UserLoginRes loginRes);

    void onLoginError(String msg);
}
