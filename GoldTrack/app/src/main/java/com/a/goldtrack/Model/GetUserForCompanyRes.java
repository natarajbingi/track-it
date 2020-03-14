package com.a.goldtrack.Model;

import java.util.List;

public class GetUserForCompanyRes {
    public List<ResList> resList;
    public boolean success;
    public String response;

    public class ResList {

        public String userName;
        public String mobileNo;
        public String firstName;
        public String lastName;
        public String gender;
        public String loginAttempts;
        public String emailID;
        public String dob;
        public String companyId;
        public String user_UID;
        public String mobileRegId;
        public String passWord;
        public String mobileAppType;
        public String mobileAppVersion;
        public String mobileDevName;
        public String mobileIMEINo;
        public String profilePicUrl;
        public String profilePicData;
        public List<String> roles;
        public String id;
        public boolean modify;
        public boolean add;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;
        public boolean delete;
    }
}
