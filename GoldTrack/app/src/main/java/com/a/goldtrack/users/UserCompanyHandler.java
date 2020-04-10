package com.a.goldtrack.users;

import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.Model.UpdateUserDetails;

public interface UserCompanyHandler {
    void getUsersSuccess(GetUserForCompanyRes res);

    void addUserSuccess(AddUserForCompanyRes res);

    void updateUserSuccess(AddUserForCompanyRes res);

    void onError(String msg);

    void pbShow();
}
