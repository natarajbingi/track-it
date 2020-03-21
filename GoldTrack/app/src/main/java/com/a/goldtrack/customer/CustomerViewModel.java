package com.a.goldtrack.customer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Model.AddCustomerReq;
import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.AddRemoveCommonImageReq;
import com.a.goldtrack.Model.AddRemoveCommonImageRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCustomerReq;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.UpdateCustomerReq;
import com.a.goldtrack.Model.UpdateCustomerRes;
import com.a.goldtrack.network.RestFullServices;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.List;

public class CustomerViewModel extends ViewModel implements ICustomerCallBacs , IDropdownDataCallBacks {
    public String title = "Customers";
    ICustomerhandler view;
    MutableLiveData<List<DropdownDataForCompanyRes.CustomerList>> list;

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
//        RestFullServices.getCusomer(req, this);
        GetCompany req1 = new GetCompany();
        req1.companyId = Sessions.getUserString(GTrackApplication.getInstance().getApplicationContext(), Constants.companyId);
        RestFullServices.getDropdownDataForCompanyHome(req1, this);
    }

    public void addCustomer(AddCustomerReq req) {
        RestFullServices.addCusomer(req, this);
    }

    public void updateCustomer(UpdateCustomerReq req) {
        RestFullServices.updateCusomer(req, this);
    }

    public void addRemoveCommonImageReq(AddRemoveCommonImageReq req) {
        RestFullServices.addRemoveCommonImage(req, null, this);
    }


    @Override
    public void getCustomerSuccess(GetCustomerRes body) {
       // list.postValue(body);
      //  view.getCustomerSuccess(body);
    }

    @Override
    public void onDropDownSuccess(DropdownDataForCompanyRes body) {
        list.postValue(body.customerList);
        view.onGetDrpSuccess(body);
    }

    @Override
    public void onError(String s) {
        view.onErrorSpread(s);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onErrorSpread(s);
    }

    @Override
    public void updateCustomerSuccess(UpdateCustomerRes body) {
        view.updateCustomerSuccess(body);
    }

    @Override
    public void onAddRemoveCommonImageSuccess(AddRemoveCommonImageRes res) {
        view.onAddRemoveCommonImageSuccess(res);
    }

    @Override
    public void addCustomerSuccess(AddCustomerRes body) {
        view.addCustomerSuccess(body);
    }


}
