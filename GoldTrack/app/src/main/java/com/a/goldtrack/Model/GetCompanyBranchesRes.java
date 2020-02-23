package com.a.goldtrack.Model;

import java.util.List;

public class GetCompanyBranchesRes {
    public List<ResList> resList;
    public boolean success;
    public String response;

    public class ResList {

        public String branchCode;
        public String branchName;
        public String branchDesc;
        public String branchAddress1;
        public String branchCity;
        public String branchPin;
        public String branchPhNumber;
        public String companyId;
        public String id;
        public String createdBy;
    }
}
