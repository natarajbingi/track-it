package com.a.goldtrack.dailyclosure;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddUserDailyClosureReq;
import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateUserDailyClosureReq;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;
import com.a.goldtrack.network.RestFullServices;

public class UserDailyClosureViewModel extends ViewModel implements IDailyClosureCallBacks {
    @Override
    protected void onCleared() {
        super.onCleared();
    }

    MutableLiveData<GetUserDailyClosureRes> list;
    IDailyClosureView view;
    public MutableLiveData<Double> totalAmt;
    public MutableLiveData<Double> cashInHand;

    public void onViewAvailable(IDailyClosureView view) {
        this.view = view;
        list = new MutableLiveData<>();
        totalAmt = new MutableLiveData<>();
    }

    public void getDailyClosures(GetUserDailyClosureReq req) {
        RestFullServices.getDailyClosures(req, this);
    }

    public void getTrans(GetTransactionReq req) {
        RestFullServices.getTransaction(req, null, null, this);
    }

    public void addDailyClosure(AddUserDailyClosureReq req) {
        RestFullServices.addDailyClosure(req, this);
    }

    public void updateDailyClosure(UpdateUserDailyClosureReq req) {
        RestFullServices.updateDailyClosures(req, this);
    }

    @Override
    public void onGetDailyClosureSuccess(GetUserDailyClosureRes res) {
        list.postValue(res);
        view.onGetDailyClosureSuccess(res);
    }

    @Override
    public void onAddDailyClousureSuccess(AddUserDailyClosureRes res) {
        view.onAddDailyClousureSuccess(res);
    }

    @Override
    public void onUpdateDailyClousureSuccess(UpdateUserDailyClosureRes res) {
        view.onUpdateDailyClousureSuccess(res);
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        calTransTotalAmt(res);
        view.onGetTransSuccess(res);
    }

    @Override
    public void onError(String message) {
        view.onError(message);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onErrorComplete(s);
    }


    private void calTransTotalAmt(GetTransactionRes res) {
        double amt = 0;
        for (int i = 0; i < res.dataList.size(); i++) {
            amt = amt + (Double.parseDouble(res.dataList.get(i).amountPayable));
        }
        totalAmt.postValue(amt);
    }
}
