package com.a.goldtrack.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddUserForCompany;
import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.Model.UpdateUserDetails;
import com.a.goldtrack.network.RestFullServices;

public class UserForCompanyViewModel extends ViewModel implements IUserCallBacks {

    private MutableLiveData<GetUserForCompanyRes> list;
    private UserCompanyHandler view;

    public void onViewAvailable(UserCompanyHandler view) {
        this.view = view;
    }

    protected void onCleared() {
        super.onCleared();
    }

    public void addUser(AddUserForCompany req) {

        view.pbShow();
        RestFullServices.addUser(req, this);
    }

    public void updateUser(UpdateUserDetails req) {

        view.pbShow();
        RestFullServices.updateUser(req, this);
    }

    public void getUsers(GetUserForCompany req) {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        view.pbShow();
        RestFullServices.getUsers(req, this, null);
    }

    public LiveData<GetUserForCompanyRes> getList() {
        return list;
    }

    @Override
    public void addUserSuccess(AddUserForCompanyRes res) {
        view.pbHide();
        view.addUserSuccess(res);
    }

    @Override
    public void updateUserSuccess(AddUserForCompanyRes res) {
        view.pbHide();
        view.updateUserSuccess(res);
    }

    @Override
    public void getUsersSuccess(GetUserForCompanyRes res) {
        list.postValue(res);
        view.pbHide();
        view.getUsersSuccess(res);
    }

    @Override
    public void onError(String msg) {
        view.pbHide();
        view.onError(msg);
    }
}
