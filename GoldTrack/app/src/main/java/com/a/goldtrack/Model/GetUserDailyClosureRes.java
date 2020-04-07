package com.a.goldtrack.Model;

import java.util.List;

public class GetUserDailyClosureRes {
    public String response;
    public boolean success;
    public List<DataList> dataList;

    public class DataList {
        public String companyName;
        public String userName;
        public String date;
        public String userId;
        public String branchId;
        public String fundRecieved;
        public String companyBranchName;
        public String expenses;
        public String cashInHand;
        public String expensesDesc;
        public String companyId;
        public String comments;
        public String id;
        public String updatedDt;
        public String createdBy;
        public String createdDt;
        public String updatedBy;
    }
}
