package com.a.goldtrack.customer;

import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.AddRemoveCommonImageRes;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.UpdateCustomerRes;

public interface ICustomerCallBacs {
    void getCustomerSuccess(GetCustomerRes body);

    void onError(String s);

    void updateCustomerSuccess(UpdateCustomerRes body);

    void onAddRemoveCommonImageSuccess(AddRemoveCommonImageRes res);

    void onErrorComplete(String s);

    void addCustomerSuccess(AddCustomerRes body);
}
