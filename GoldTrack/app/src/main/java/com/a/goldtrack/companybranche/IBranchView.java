package com.a.goldtrack.companybranche;

import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyBranchesRes;

public interface IBranchView {
    void onSuccessGetBranch(DropdownDataForCompanyRes res);

    void onSuccessAddBranch(AddCompanyBranchesRes branchesRes);

    void onSuccessUpdateBranch(UpdateCompanyBranchesRes res);

    void onErrorBranch(String msg);
}
