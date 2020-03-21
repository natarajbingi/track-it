package com.a.goldtrack.items;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Model.AddItemReq;
import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetItemsReq;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.UpdateItemReq;
import com.a.goldtrack.Model.UpdateItemRes;
import com.a.goldtrack.network.RestFullServices;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.List;

public class    ItemsViewModel extends ViewModel implements IItemsCallBacks , IDropdownDataCallBacks {

    MutableLiveData<List<DropdownDataForCompanyRes.ItemsList>> list;
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
        GetCompany req1 = new GetCompany();
        req1.companyId = Sessions.getUserString(GTrackApplication.getInstance().getApplicationContext(), Constants.companyId);
        RestFullServices.getDropdownDataForCompanyHome(req1, this);
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
    }

    @Override
    public void onUpdateItemDetails(UpdateItemRes updateItemRes) {
        view.onItemUpdateSuccess(updateItemRes);
    }

    @Override
    public void onGetDrpSuccess(DropdownDataForCompanyRes res) {

    }

    @Override
    public void onDropDownSuccess(DropdownDataForCompanyRes body) {
        this.list.postValue(body.itemsList);
        view.onGetDrpSuccess(body);
    }

    @Override
    public void onError(String msg) {
        view.onError(msg);
    }

    @Override
    public void onErrorComplete(String s) {

    }
}
