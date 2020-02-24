package com.a.goldtrack.companybranche;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddCompanyBranchesReq;
import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyBranches;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.network.RestFullServices;

public class CompanyBranchesViewModel extends ViewModel implements IBranchCallBacks {

    MutableLiveData<GetCompanyBranchesRes> list;
    IBranchView view;

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void onViewAvailable(IBranchView view) {
        this.view = view;
    }


    void onGetBranch(GetCompanyBranches req) {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        RestFullServices.getBranches(req, this);
    }

    void onAddBranch(AddCompanyBranchesReq req) {
        RestFullServices.addBranch(req, this);
    }

    void onUpdateBranch(UpdateCompanyDetails req) {
        RestFullServices.updateBranch(req, this);
    }


    @Override
    public void onSuccessGetBranch(GetCompanyBranchesRes list) {
        this.list.postValue(list);
        view.onSuccessGetBranch();
    }

    @Override
    public void onSuccessAddBranch(AddCompanyBranchesRes branchesRes) {
        view.onSuccessAddBranch(branchesRes);
    }

    @Override
    public void onSuccessUpdateBranch() {
        view.onSuccessUpdateBranch();
    }

    @Override
    public void onError(String msg) {
        view.onErrorBranch(msg);
    }
}
