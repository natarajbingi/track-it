package com.a.goldtrack.Model;

import java.util.List;

public class GetCustomerRes {
    public List<ResList> resList;
    public boolean success;
    public String response;

    public class ResList {

        public String id;
        public String uniqueId;
        public String firstName;
        public String lastName;
        public String mobileNum;
        public String emailId;
        public String address1;
        public String address2;
        public String pin;
        public String state;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;
    }
}
