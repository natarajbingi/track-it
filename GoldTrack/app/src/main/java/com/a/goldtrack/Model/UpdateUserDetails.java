package com.a.goldtrack.Model;

import java.util.List;

public class UpdateUserDetails {
    //    user/updateUserDetails
    public List<Data> data;

    public static class Data {
        public String id;
        public String user_UID;
        public String companyId;
        public String userName;
        public String passWord;
        public String firstName;
        public String lastName;
        public String gender;
        public String mobileNo;
        public String dob;
        public String emailID;
        public String mobileRegId;
        public String mobileIMEINo;
        public String mobileDevName;
        public String mobileAppType;
        public String mobileAppVersion;
        public String loginAttempts;
        public List<String> roles;
        public String profilePicUrl;
        public boolean delete;
        public boolean modify;
        public boolean add;
        public String createdDt;
        public String createdBy;
        public String updatedDt;
        public String updatedBy;
    }
}
