package com.a.goldtrack.Model;

import java.util.List;

public class UpdateCompanyDetails {
    // company/updateCompanyDetails
    public List<Data> data;

    public static class Data {
        public String companyId;
        public String name;
        public String desc;
        public String mobileNo;
        public String landline;
        public String emailID;
        public String website;
        public String address1;
        public String address2;
        public String city;
        public String district;
        public String state;
        public String pin;
        public String logoImageData;
        public String logoImagePath;
        public boolean delete;
        public boolean modify;
        public boolean add;
        public String smsSenderID;
        public String gstNo;
        public String createdDt;
        public String createdBy;
        public String updatedDt;
        public String updatedBy;
    }
}
