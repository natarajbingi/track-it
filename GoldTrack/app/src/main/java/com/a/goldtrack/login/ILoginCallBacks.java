package com.a.goldtrack.login;

import com.a.goldtrack.Model.UserLoginRes;

public interface ILoginCallBacks {
    void onSuccess(UserLoginRes loginRes);

    void onError(String msg);
}
