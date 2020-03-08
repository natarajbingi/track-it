package com.a.goldtrack.Model;

import java.util.List;

public class UpdateUserDailyClosureReq {

    public List<Data> data;

    public static class Data {

        public String id;
        public String userId;
        public String companyId;
        public String branchId;
        public String date;
        public String fundRecieved;
        public String expenses;
        public String cashInHand;
        public String expensesDesc;
        public String comments;
        public String createdBy;
        public String updatedBy;
        public boolean delete;
    }

}
