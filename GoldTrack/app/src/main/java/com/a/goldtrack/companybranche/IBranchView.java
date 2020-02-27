package com.a.goldtrack.companybranche;

import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.UpdateCompanyBranchesRes;

public interface IBranchView {
    void onSuccessGetBranch();

    void onSuccessAddBranch(AddCompanyBranchesRes branchesRes);

    void onSuccessUpdateBranch(UpdateCompanyBranchesRes res);

    void onErrorBranch(String msg);
}
