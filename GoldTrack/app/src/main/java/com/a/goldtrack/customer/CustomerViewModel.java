package com.a.goldtrack.customer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddCustomerReq;
import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.GetCustomerReq;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.UpdateCustomerReq;
import com.a.goldtrack.Model.UpdateCustomerRes;
import com.a.goldtrack.network.RestFullServices;

public class CustomerViewModel extends ViewModel implements ICustomerCallBacs {
    public String title = "Customers";
    ICustomerhandler view;
    MutableLiveData<GetCustomerRes> list;

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void onViewAvailable(ICustomerhandler view) {
        list = new MutableLiveData<>();
        this.view = view;
    }


    public void getCustomer(GetCustomerReq req) {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        RestFullServices.getCusomer(req, this);
    }

    public void addCustomer(AddCustomerReq req) {
        RestFullServices.addCusomer(req, this);
    }

    public void updateCustomer(UpdateCustomerReq req) {
        RestFullServices.updateCusomer(req, this);
    }

    @Override
    public void getCustomerSuccess(GetCustomerRes body) {
        list.postValue(body);
        view.getCustomerSuccess(body);
    }

    @Override
    public void onError(String s) {
        view.onErrorSpread(s);
    }

    @Override
    public void updateCustomerSuccess(UpdateCustomerRes body) {
        view.updateCustomerSuccess(body);
    }

    @Override
    public void onCompleteError(String s) {
        view.onErrorSpread(s);
    }

    @Override
    public void addCustomerSuccess(AddCustomerRes body) {
        view.addCustomerSuccess(body);
    }


}
