package com.a.goldtrack.Model;

import java.util.List;

public class GetUserDailyClosureRes {
    public String response;
    public boolean success;
    public List<DataList> dataList;

    public static class DataList {
        public String userName;
        public String companyName;
        public String companyBranchName;
        public String comments;
        public List<TransactionsForday> transactionsForday;
        public String companyId;
        public String userId;
        public String branchId;
        public String fundRecieved;
        public String expenses;
        public String expensesDesc;
        public String cashInHand;
        public String date;
        public String id;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;
    }

    public static class TransactionsForday {
        public String companyName;
        public String comments;
        public String totalAmount;
        public String userID;
        public String customerID;
        public String companyID;
        public String branchName;
        public List<ItemList> itemList;
        public String transID;
        public String commodity;
        public String branchID;
        public String presentDayCommodityRate;
        public String totalCommodityWeight;
        public String totalStoneWastage;
        public String totalOtherWastage;
        public String totalNettWeight;
        public String grossAmount;
        public String marginAmount;
        public String nettAmount;
        public String paidAmountForRelease;
        public String roundOffAmount;
        public String amountPayable;
        public String marginPercent;
        public String transValidOTP;
        public String nbfcReferenceNo;
        public String customerName;
        public String referencePicData;
        public String billNumber;
        public String customerMobNo;
        public String empName;
        public String referencePicPath;
        public String billPdfPath;
        public String uploadedImages;
        public String createdBy;
        public String updatedBy;
        public String createdDt;
        public String updatedDt;
    }

    public static class ItemList {
        public String itemName;
        public String itemID;
        public String amount;
        public String commodityWeight;
        public String stoneWastage;
        public String otherWastage;
        public String nettWeight;
        public String purity;

    }
}
