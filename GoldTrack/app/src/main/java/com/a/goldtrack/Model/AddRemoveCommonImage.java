package com.a.goldtrack.Model;

import java.io.File;
import java.io.Serializable;

public class AddRemoveCommonImage implements Serializable {
    public String id;
    public String commonID;
    public String companyID;

    public String imageTable;
    public String actionType;
    public String imageType;

    public File imageData;
    public String  imagePath;
    public String createdBy;
}
