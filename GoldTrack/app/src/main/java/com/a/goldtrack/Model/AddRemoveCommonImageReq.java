package com.a.goldtrack.Model;

import java.util.List;

public class AddRemoveCommonImageReq {
    public List<Data> data;

    public static class Data {
        public String id;
        public String commonID;
        public String companyID;
        public String imageTable;
        public String actionType;
        public String imageType;
        public String imagePath;
        public String createdBy;
    }
}
