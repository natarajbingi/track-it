package com.a.goldtrack.trans;

import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;

public interface IDropdownDataCallBacks {
    void onDropDownSuccess(DropdownDataForCompanyRes body);

    void onError(String message);

    void onErrorComplete(String s);
}
