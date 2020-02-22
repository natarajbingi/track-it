package com.a.goldtrack.login;

public interface LoginDataHandler {

    void onClickTextView();
    void onClickLoginBtn();
    void onClickLoginFailed();
    void onSetEmailError(boolean bool);
    void onSetPwdError(boolean bool);
}
