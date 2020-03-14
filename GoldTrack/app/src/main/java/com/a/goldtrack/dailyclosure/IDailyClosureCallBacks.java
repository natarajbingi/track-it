package com.a.goldtrack.dailyclosure;

import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;

public interface IDailyClosureCallBacks {

    void onGetDailyClosureSuccess(GetUserDailyClosureRes res);

    void onAddDailyClousureSuccess(AddUserDailyClosureRes res);

    void onUpdateDailyClousureSuccess(UpdateUserDailyClosureRes res);
    void onGetTransSuccess(GetTransactionRes res);

    void onError(String message);

    void onErrorComplete(String s);
}
