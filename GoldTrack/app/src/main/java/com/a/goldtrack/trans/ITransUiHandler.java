package com.a.goldtrack.trans;

import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPRes;

public interface ITransUiHandler {
    void onUiVerifyOtpSuccess(CustomerWithOTPRes body);

    void onAddTransSuccess(AddTransactionRes res);
    void onError(String msg);

    void onErrorComplete(String msg);
}
