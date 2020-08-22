package com.a.goldtrack.ui.share;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

public class DashBrdViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;

    public DashBrdViewModel(@NonNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        String str = Sessions.getUserString(application, Constants.userName);
        mText.setValue("Welcome, " + str);

    }


    public LiveData<String> getText() {
        return mText;
    }
}