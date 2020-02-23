package com.a.goldtrack.Model;

import java.util.List;

/**
 * Created by nataraj-pc on 15-Mar-18.
 */

public class UserLoginRes {
    public Data data;
    public String response;
    public Boolean success;


    public class Data {
        public String id;
        public String userName;
        public String mobileNo;
        public String firstName;
        public String lastName;
        public String gender;
        public int loginAttempts;
        public String emailID;
        public String dob;
        public String companyId;
        public String user_UID;
        public String passWord;
        public String mobileRegId;
        public String mobileAppType;
        public String mobileAppVersion;
        public String mobileDevName;
        public String mobileIMEINo;
        public String profilePicUrl;
        public String sessionID;
        public List<String> roles;
    }

}
