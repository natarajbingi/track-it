package com.a.goldtrack.customer;

import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.UpdateCustomerRes;

public interface ICustomerhandler {

    void addCustomerSuccess(AddCustomerRes res);
    void updateCustomerSuccess(UpdateCustomerRes res);
    void getCustomerSuccess(GetCustomerRes res);
    void onErrorSpread(String msg);
}
