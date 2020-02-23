package com.a.goldtrack.Model;

import java.util.List;

public class GetItemsRes {
    public List<ResList> resList;
    public boolean success;
    public String response;

    public class ResList {

        public String companyID;
        public String id;
        public String commodity;
        public String itemName;
        public String itemDesc;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;

    }
}
