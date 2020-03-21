package com.a.goldtrack.items;

import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.UpdateItemRes;

public interface IItemsCallBacks {
    void onAddItemSuccess(AddItemRes itemRes);

    void onGetItemsForCompany(GetItemsRes getItemsRes);

    void onUpdateItemDetails(UpdateItemRes updateItemRes);
    void onGetDrpSuccess(DropdownDataForCompanyRes res);

    void onError(String msg);
}
