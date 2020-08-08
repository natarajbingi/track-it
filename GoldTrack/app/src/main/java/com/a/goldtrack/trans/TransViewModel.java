package com.a.goldtrack.trans;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddRemoveCommonImageReq;
import com.a.goldtrack.Model.AddRemoveCommonImageRes;
import com.a.goldtrack.Model.AddTransactionReq;
import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPReq;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.network.RestFullServices;

public class TransViewModel extends ViewModel implements ITransCallBacks, IDropdownDataCallBacks {

    // MutableLiveData<List<ItemsTrans>> list = new MutableLiveData<>();
    MutableLiveData<DropdownDataForCompanyRes> dropdownList;
    ITransUiHandler view;

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    public void onViewAvailable(ITransUiHandler view) {
        this.view = view;
        dropdownList = new MutableLiveData<>();
    }

    public void verifyOtp(CustomerWithOTPReq req) {

        view.onpbSHow();
        RestFullServices.getPTO(req, this);
    }

    public void addTransreq(AddTransactionReq req) {

        view.onpbSHow();
        RestFullServices.addTransaction(req, this);
    }

    public void getDropdowns(GetCompany req) {
        if (dropdownList == null) {
            dropdownList = new MutableLiveData<>();
        }
        view.onpbSHow();
        RestFullServices.getDropdownDataForCompanyHome(req, this);
    }

    public void addRemoveCommonImageReq(AddRemoveCommonImageReq req) {
        view.onpbSHow();
        RestFullServices.addRemoveCommonImage(req, this, null);
    }

    @Override
    public void onOtpSuccess(CustomerWithOTPRes body) {
        view.onUiVerifyOtpSuccess(body);
    }

    @Override
    public void onDropDownSuccess(DropdownDataForCompanyRes body) {
        dropdownList.postValue(body);
        view.onGetDropDownsSuccess(body);
    }

    @Override
    public void onAddTransSuccess(AddTransactionRes res) {
        view.onAddTransSuccess(res);
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        view.onGetTransSuccess(res);
    }

    @Override
    public void onAddRemoveCommonImageSuccess(AddRemoveCommonImageRes res) {
        view.onAddRemoveCommonImageSuccess(res);
    }

    @Override
    public void onError(String message) {
        view.onError(message);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onErrorComplete(s);
    }
}
