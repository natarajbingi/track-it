package com.a.goldtrack.trans;

import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;

public interface ITransCallBacks {
    void onOtpSuccess(CustomerWithOTPRes res);

    void onDropDownSuccess(DropdownDataForCompanyRes body);

    void onAddTransSuccess(AddTransactionRes res);

    void onError(String message);

    void onErrorComplete(String s);
}
