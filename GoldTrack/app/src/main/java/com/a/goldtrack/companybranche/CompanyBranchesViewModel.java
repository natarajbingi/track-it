package com.a.goldtrack.companybranche;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Model.AddCompanyBranchesReq;
import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyBranches;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.UpdateCompanyBranchesReq;
import com.a.goldtrack.Model.UpdateCompanyBranchesRes;
import com.a.goldtrack.network.RestFullServices;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.List;

public class CompanyBranchesViewModel extends ViewModel implements IBranchCallBacks, IDropdownDataCallBacks {

    MutableLiveData<List<DropdownDataForCompanyRes.BranchesList>> list;
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
        GetCompany req1 = new GetCompany();
        req1.companyId = Sessions.getUserString(GTrackApplication.getInstance().getApplicationContext(), Constants.companyId);
        view.PbShow();
        RestFullServices.getDropdownDataForCompanyHome(req1, this);
    }

    void onAddBranch(AddCompanyBranchesReq req) {
        view.PbShow();
        RestFullServices.addBranch(req, this);
    }

    void onUpdateBranch(UpdateCompanyBranchesReq req) {
        view.PbShow();
        RestFullServices.updateBranch(req, this);
    }


    @Override
    public void onSuccessGetBranch(GetCompanyBranchesRes list) {
    }

    @Override
    public void onSuccessAddBranch(AddCompanyBranchesRes branchesRes) {
        view.onSuccessAddBranch(branchesRes);
    }

    @Override
    public void onSuccessUpdateBranch(UpdateCompanyBranchesRes res) {
        view.onSuccessUpdateBranch(res);
    }

    @Override
    public void onDropDownSuccess(DropdownDataForCompanyRes body) {
        this.list.postValue(body.branchesList);
        view.onSuccessGetBranch(body);
    }

    @Override
    public void onError(String msg) {
        view.onErrorBranch(msg);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onErrorBranch(s);
    }
}
