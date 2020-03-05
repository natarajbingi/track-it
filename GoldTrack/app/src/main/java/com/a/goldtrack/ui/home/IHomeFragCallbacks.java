package com.a.goldtrack.ui.home;

import com.a.goldtrack.Model.GetTransactionRes;

public interface IHomeFragCallbacks {
    void onGetTransSuccess(GetTransactionRes res);

    void onError(String message);

    void onErrorComplete(String s);
}
