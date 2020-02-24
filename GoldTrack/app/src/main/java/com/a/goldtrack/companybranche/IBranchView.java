package com.a.goldtrack.companybranche;

import com.a.goldtrack.Model.AddCompanyBranchesRes;

public interface IBranchView {
    void onSuccessGetBranch();

    void onSuccessAddBranch(AddCompanyBranchesRes branchesRes);

    void onSuccessUpdateBranch();

    void onErrorBranch(String msg);
}
