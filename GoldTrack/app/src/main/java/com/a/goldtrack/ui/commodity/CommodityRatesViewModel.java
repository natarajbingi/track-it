package com.a.goldtrack.ui.commodity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CommodityRatesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CommodityRatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is send fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}