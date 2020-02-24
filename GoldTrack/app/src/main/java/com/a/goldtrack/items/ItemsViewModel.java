package com.a.goldtrack.items;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddItemReq;
import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.GetItemsReq;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.UpdateItemReq;
import com.a.goldtrack.Model.UpdateItemRes;
import com.a.goldtrack.network.RestFullServices;

public class ItemsViewModel extends ViewModel implements IItemsCallBacks {

    MutableLiveData<GetItemsRes> list;
    IItemsView view;

    void onViewAvailable(IItemsView view) {
        this.view = view;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    public void onAddItem(AddItemReq itemReq) {

        RestFullServices.addItem(itemReq, this);
    }

    public void onGetItemsForCompany(GetItemsReq itemsReq) {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        RestFullServices.getItems(itemsReq, this);
    }

    public void onUpdateItemDetails(UpdateItemReq updateItemReq) {
        RestFullServices.updateItem(updateItemReq, this);
    }

    @Override
    public void onAddItemSuccess(AddItemRes itemRes) {
        view.onItemAddSuccess(itemRes);
    }

    @Override
    public void onGetItemsForCompany(GetItemsRes getItemsRes) {
        this.list.postValue(getItemsRes);
        view.onItemGetSuccess(getItemsRes);
    }

    @Override
    public void onUpdateItemDetails(UpdateItemRes updateItemRes) {
        view.onItemUpdateSuccess(updateItemRes);
    }

    @Override
    public void onError(String msg) {
        view.onError(msg);
    }
}
