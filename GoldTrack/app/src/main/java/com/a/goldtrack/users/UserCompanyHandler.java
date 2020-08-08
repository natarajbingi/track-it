package com.a.goldtrack.users;

import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.GetUserForCompanyRes;

public interface UserCompanyHandler {
    void getUsersSuccess(GetUserForCompanyRes res);

    void addUserSuccess(AddUserForCompanyRes res);

    void updateUserSuccess(AddUserForCompanyRes res);

    void onError(String msg);

    void pbShow();

    void pbHide();
}
