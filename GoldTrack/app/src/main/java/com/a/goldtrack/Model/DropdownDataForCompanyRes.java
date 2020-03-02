package com.a.goldtrack.Model;

import java.util.List;

public class DropdownDataForCompanyRes {
    public String response;
    public boolean success;
    public List<BranchesList> branchesList;
    public List<CustomerList> customerList;
    public List<ItemsList> itemsList;

    public class BranchesList {

        public String companyId;
        public String branchAddress1;
        public String branchCity;
        public String branchCode;
        public String branchDesc;
        public String branchName;
        public String branchPhNumber;
        public String branchPin;
        public String response;
        public boolean success;
        public String id;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;

    }

    public class CustomerList {

        public String uniqueId;
        public String firstName;
        public String lastName;
        public String mobileNum;
        public String emailId;
        public String address1;
        public String address2;
        public String pin;
        public String id;
        public String state;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;

    }

    public class ItemsList {
        public String companyID;
        public String commodity;
        public String itemName;
        public String itemDesc;
        public String id;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;

    }
}
