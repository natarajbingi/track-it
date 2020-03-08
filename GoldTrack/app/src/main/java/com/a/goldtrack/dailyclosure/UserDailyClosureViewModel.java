package com.a.goldtrack.dailyclosure;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.AddUserDailyClosureReq;
import com.a.goldtrack.Model.AddUserDailyClosureRes;
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

    public void onViewAvailable(IDailyClosureView view) {
        this.view = view;
        list = new MutableLiveData<>();
    }

    public void getDailyClosures(GetUserDailyClosureReq req) {
        RestFullServices.getDailyClosures(req, this);
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
    public void onError(String message) {
        view.onError(message);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onErrorComplete(s);
    }
}
