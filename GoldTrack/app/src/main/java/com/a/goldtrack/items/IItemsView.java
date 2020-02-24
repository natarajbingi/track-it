package com.a.goldtrack.items;

import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.UpdateItemRes;

public interface IItemsView {
    void onItemGetSuccess(GetItemsRes companyRes);

    void onItemAddSuccess(AddItemRes itemRes);

    void onItemUpdateSuccess(UpdateItemRes itemRes);

    void onError(String msg);
}
