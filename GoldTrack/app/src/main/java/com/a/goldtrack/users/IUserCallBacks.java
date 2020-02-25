package com.a.goldtrack.users;

import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.GetUserForCompanyRes;

public interface IUserCallBacks {
    void addUserSuccess(AddUserForCompanyRes body);

    void getUsersSuccess(GetUserForCompanyRes req);

    void onError(String msg);

    void updateUserSuccess(AddUserForCompanyRes req);
}
