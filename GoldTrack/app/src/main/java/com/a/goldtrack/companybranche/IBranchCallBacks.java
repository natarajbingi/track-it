package com.a.goldtrack.companybranche;

import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyBranchesRes;

public interface IBranchCallBacks {
    void onSuccessGetBranch(GetCompanyBranchesRes list);

    void onSuccessAddBranch(AddCompanyBranchesRes branchesRes);

    void onSuccessUpdateBranch();

    void onError(String msg);
}
