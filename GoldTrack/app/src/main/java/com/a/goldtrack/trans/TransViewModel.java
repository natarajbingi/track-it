package com.a.goldtrack.trans;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPReq;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.network.RestFullServices;

import java.util.ArrayList;
import java.util.List;

public class TransViewModel extends ViewModel implements ITransCallBacks {

    MutableLiveData<List<ItemsTrans>> list;
    ITransUiHandler view;

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void getListItemsTest() {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        list.postValue(addTranItemsNowTest());
    }

    public void onViewAvailable(ITransUiHandler view) {
        this.view = view;
    }

    List<ItemsTrans> addTranItemsNowTest() {
        List<ItemsTrans> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemsTrans im = new ItemsTrans();
            im.commodity = "Chain " + i;
            im.commodityWeight = "100." + i;
            im.amount = "200" + i;
            im.nettWeight = "100 " + i;
            im.purity = "100 " + i;
            im.stoneWastage = "100 " + i;
            im.otherWastage = "100 " + i;
            im.itemID = "" + i;
            list.add(im);
        }
        return list;
    }

    public void verifyOtp(CustomerWithOTPReq req) {
        RestFullServices.getPTO(req, this);
    }

    public void addTransreq(AddTransactionReq req) {
        RestFullServices.addTransaction(req,this);
    }
    @Override
    public void onOtpSuccess(CustomerWithOTPRes body) {
        view.onUiVerifyOtpSuccess(body);
    }

    @Override
    public void onAddTransSuccess(AddTransactionRes res) {
        view.onAddTransSuccess(res);
    }

    @Override
    public void onError(String message) {
        view.onError(message);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onErrorComplete(s);
    }
}
