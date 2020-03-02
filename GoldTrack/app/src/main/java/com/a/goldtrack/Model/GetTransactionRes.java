package com.a.goldtrack.Model;

import java.util.List;

public class GetTransactionRes {
    public String response;
    public boolean success;
    public List<DataList> dataList;

    public class DataList {
        public String transID;
        public String billNumber;
        public List<ItemList> itemList;
        public String companyName;
        public String customerName;
        public String empName;
        public String userID;
        public String customerID;
        public String branchName;
        public String referencePicData;
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
        public String roundOffAmount;
        public String amountPayable;
        public String marginPercent;
        public String transValidOTP;
        public String nbfcReferenceNo;
        public String comments;
        public String branchID;
        public String companyID;
        public String commodity;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;
    }

    public class ItemList {
        public String itemID;
        public String commodityWeight;
        public String stoneWastage;
        public String otherWastage;
        public String nettWeight;
        public String purity;
        public String amount;
        public String itemName;
    }
}
