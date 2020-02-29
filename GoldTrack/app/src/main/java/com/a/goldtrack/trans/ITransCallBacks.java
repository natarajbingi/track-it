package com.a.goldtrack.trans;

import com.a.goldtrack.Model.CustomerWithOTPRes;

public interface ITransCallBacks {
    void onOtpSuccess(CustomerWithOTPRes body);

    void onError(String message);

    void onErrorComplete(String s);
}
