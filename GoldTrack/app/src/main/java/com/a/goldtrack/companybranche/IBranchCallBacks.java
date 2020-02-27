package com.a.goldtrack.companybranche;

import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.UpdateCompanyBranchesRes;

public interface IBranchCallBacks {
    void onSuccessGetBranch(GetCompanyBranchesRes list);

    void onSuccessAddBranch(AddCompanyBranchesRes branchesRes);

    void onSuccessUpdateBranch(UpdateCompanyBranchesRes res);

    void onError(String msg);
}
