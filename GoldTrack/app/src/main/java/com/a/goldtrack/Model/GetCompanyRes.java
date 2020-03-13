package com.a.goldtrack.Model;

import java.util.List;

public class GetCompanyRes {

    public List<ResList> resList;
    public boolean success;
    public String response;

    public class ResList {

        public String desc; 
        public String mobileNo;
        public String website;
        public String address1;
        public String city;
        public String pin;
        public String address2;
        public String district;
        public String emailID;
        public String landline;
        public String logoImagePath;
        public String logoImageData;
        public String smsSenderID;
        public String gstNo;
        public String name;
        public String id;
        public String state;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;

    }
}
