package com.a.goldtrack.company;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddCompany;
import com.a.goldtrack.Model.AddCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.network.RestFullServices;

public class CompanyViewModel extends ViewModel implements ICallBacks {

    public MutableLiveData<GetCompanyRes> list = null;
    private ICompanyView view;
    public String title = "Company";


    public void getCompany(GetCompany model) {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        view.PbSHow();
        RestFullServices.getCompanyList(model, this);
    }

    public void onViewAvailable(ICompanyView view) {
        this.view = view;
    }

    public void addCompany(AddCompany model) {
        view.PbSHow();
        RestFullServices.addCompany(model, this);
    }

    public void updateCompany(UpdateCompanyDetails model) {
        view.PbSHow();
        RestFullServices.updateCompanyDetails(model, this);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void onSuccess(GetCompanyRes model) {
        list.postValue(model);
        view.onSuccessGetCompany(model);
    }

    @Override
    public void onSuccessAddCompany(AddCompanyRes model) {
        if (model.success) {
            view.addCompanyDetailes();
        }
    }

    @Override
    public void onSuccessUpdateCompany(UpdateCompanyDetailsRes model) {
        view.updateCompanyDetailes();
    }

    @Override
    public void onError(String msg) {
        Log.e("CompanyReqError", msg);
        view.onErrorSpread(msg);
    }
}
