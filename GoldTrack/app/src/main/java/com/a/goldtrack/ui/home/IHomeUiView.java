package com.a.goldtrack.ui.home;

import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetTransactionRes;

public interface IHomeUiView {
    void onGetTransSuccess(GetTransactionRes res);
    void onGetDrpSuccess(DropdownDataForCompanyRes res);

    void onError(String message);

    void onErrorComplete(String s);
}
