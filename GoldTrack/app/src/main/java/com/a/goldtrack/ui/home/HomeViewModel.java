package com.a.goldtrack.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.network.RestFullServices;

public class HomeViewModel extends ViewModel implements IHomeFragCallbacks {

    private MutableLiveData<String> mText;
    MutableLiveData<GetTransactionRes> transList;
    IHomeUiView view;


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        transList = new MutableLiveData<>();

        mText.setValue("Dashboard in progress.. Please check with menus.");
    }

    public void setmText(String mText) {
        this.mText.setValue(mText);
    }

    public void getTransactions(GetTransactionReq req) {
        if (transList != null)
            RestFullServices.getTransaction(req, null, this);
    }

    public void onViewAvailable(IHomeUiView view) {
        this.view = view;
    }


    public LiveData<String> getText() {
        return mText;
    }


    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        transList.postValue(res);
        view.onGetTransSuccess(res);
    }

    @Override
    public void onError(String message) {
        view.onError(message);
    }

    @Override
    public void onErrorComplete(String s) {
        view.onError(s);
    }
}