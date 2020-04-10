package com.a.goldtrack.trans;

import com.a.goldtrack.Model.AddRemoveCommonImageRes;
import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetTransactionRes;

public interface ITransUiHandler {
    void onUiVerifyOtpSuccess(CustomerWithOTPRes body);

    void onAddTransSuccess(AddTransactionRes res);

    void onGetTransSuccess(GetTransactionRes res);

    void onGetDropDownsSuccess(DropdownDataForCompanyRes res);

    void onAddRemoveCommonImageSuccess(AddRemoveCommonImageRes res);

    void onError(String msg);

    void onErrorComplete(String msg);

    void onpbSHow();
}
