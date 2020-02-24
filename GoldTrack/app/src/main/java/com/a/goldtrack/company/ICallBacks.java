package com.a.goldtrack.company;

import com.a.goldtrack.Model.AddCompanyRes;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;

public interface ICallBacks {

    void onSuccessAddCompany(AddCompanyRes model);
    void onSuccessUpdateCompany(UpdateCompanyDetailsRes model);
    void onSuccess(GetCompanyRes model);
    void onError(String msg);
}
