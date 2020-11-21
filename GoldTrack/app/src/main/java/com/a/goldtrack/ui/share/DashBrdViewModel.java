package com.a.goldtrack.ui.share;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.network.RestFullServices;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.ui.home.IHomeFragCallbacks;
import com.a.goldtrack.ui.home.IHomeUiView;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

public class DashBrdViewModel extends AndroidViewModel implements IHomeFragCallbacks, IDropdownDataCallBacks {

    private MutableLiveData<String> mText;
    IHomeUiView view;

    public DashBrdViewModel(@NonNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        String str = Sessions.getUserString(application, Constants.userName);
        mText.setValue("Welcome, " + str);

    }
    public void onViewAvailable(IHomeUiView view) {
        this.view = view;
    }


    public void getUsers(GetUserForCompany req) {
        RestFullServices.getUsers(req, null,this);
    }
    public void getDropdown(GetCompany req) {
        RestFullServices.getDropdownDataForCompanyHome(req, this);
    }


    public LiveData<String> getText() {
        return mText;
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {

    }

    @Override
    public void getUsersSuccess(GetUserForCompanyRes res) {
        view.getUsersSuccess(res);
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