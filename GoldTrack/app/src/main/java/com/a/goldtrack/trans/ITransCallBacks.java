package com.a.goldtrack.trans;

import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPRes;

public interface ITransCallBacks {
    void onOtpSuccess(CustomerWithOTPRes res);

    void onAddTransSuccess(AddTransactionRes res);

    void onError(String message);

    void onErrorComplete(String s);
}
