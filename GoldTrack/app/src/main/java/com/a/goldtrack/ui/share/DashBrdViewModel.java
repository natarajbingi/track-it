package com.a.goldtrack.ui.share;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.network.RestFullServices;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.ui.home.IHomeFragCallbacks;
import com.a.goldtrack.ui.home.IHomeUiView;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

public class DashBrdViewModel extends AndroidViewModel implements IHomeFragCallbacks, IDropdownDataCallBacks, IDailyClosureDashCallBacks {

    private MutableLiveData<String> mText;
    IHomeUiView view;
    IDailyClosureDashCallBacks viewDash;

    public DashBrdViewModel(@NonNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        String str = Sessions.getUserString(application, Constants.userName);
        mText.setValue("Welcome, " + str);

    }

    public void onViewAvailable(IHomeUiView view, IDailyClosureDashCallBacks viewDash) {
        this.view = view;
        this.viewDash = viewDash;
    }


    public void getUsers(GetUserForCompany req) {
        RestFullServices.getUsers(req, null, this);
    }

    public void getDropdown(GetCompany req) {
        RestFullServices.getDropdownDataForCompanyHome(req, this);
    }

    public void getDailyClosures(GetUserDailyClosureReq req) {
        RestFullServices.getDailyClosures(req, this);
    }

    public void getDateTransactions(GetTransactionReq req) {
        RestFullServices.getTransaction(req, null, this, null);
    }


    public LiveData<String> getText() {
        return mText;
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        view.onGetTransSuccess(res);
    }

    @Override
    public void getUsersSuccess(GetUserForCompanyRes res) {
        view.getUsersSuccess(res);
    }


    @Override
    public void onGetDailyClosureSuccess(GetUserDailyClosureRes res) {
        viewDash.onGetDailyClosureSuccess(res);
    }

    @Override
    public void onDropDownSuccess(DropdownDataForCompanyRes body) {
        view.onGetDrpSuccess(body);
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