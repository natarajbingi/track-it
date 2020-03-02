package com.a.goldtrack.Model;

import java.util.List;

public class AddTransactionReq {
    public String transValidOTP;
    public String companyID;
    public String userID;
    public String customerID;
    public String branchID;
    public String commodity;
    public String presentDayCommodityRate;
    public String totalCommodityWeight;
    public String totalStoneWastage;
    public String totalOtherWastage;
    public String totalNettWeight;
    public String totalAmount;
    public String grossAmount;
    public String marginAmount;
    public String nettAmount;
    public String paidAmountForRelease;
    public String marginPercent;
    public String referencePicData;
    public String roundOffAmount;
    public String createdBy;
    public String nbfcReferenceNo;
    public String comments;
    public List<ItemList> itemList;

    public class ItemList {

        public String itemID;
        public String commodityWeight;
        public String purity;
        public String stoneWastage;
        public String otherWastage;
        public String nettWeight;
        public String amount;

    }
}
