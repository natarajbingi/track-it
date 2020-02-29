package com.a.goldtrack.trans;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.Model.ItemsTrans;

import java.util.List;

public class TransViewModel extends ViewModel {

    MutableLiveData<List<ItemsTrans>> list;

    @Override
    protected void onCleared() {
        super.onCleared();
    }


}
