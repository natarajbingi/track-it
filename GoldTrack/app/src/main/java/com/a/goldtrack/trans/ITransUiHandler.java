package com.a.goldtrack.trans;

import com.a.goldtrack.Model.CustomerWithOTPRes;

public interface ITransUiHandler {
    void onUiVerifyOtpSuccess(CustomerWithOTPRes body);

    void onError(String msg);

    void onErrorComplete(String msg);
}
