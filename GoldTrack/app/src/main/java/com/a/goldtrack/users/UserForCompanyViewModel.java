package com.a.goldtrack.users;

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

    MutableLiveData<GetUserForCompanyRes> list;
    UserCompanyHandler view;

    public void onViewAvailable(UserCompanyHandler view) {
        this.view = view;
    }

    protected void onCleared() {
        super.onCleared();
    }

    public void addUser(AddUserForCompany req) {
        RestFullServices.addUser(req, this);
    }

    public void updateUser(UpdateUserDetails req) {
        RestFullServices.updateUser(req, this);
    }

    public void getUsers(GetUserForCompany req) {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        RestFullServices.getUsers(req, this);
    }

    @Override
    public void addUserSuccess(AddUserForCompanyRes res) {
        view.addUserSuccess(res);
    }

    @Override
    public void updateUserSuccess(AddUserForCompanyRes res) {
        view.updateUserSuccess(res);
    }

    @Override
    public void getUsersSuccess(GetUserForCompanyRes res) {
        list.postValue(res);
        view.getUsersSuccess(res);
    }

    @Override
    public void onError(String msg) {
        view.onError(msg);
    }
}
