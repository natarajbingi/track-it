package com.a.goldtrack.ui.share;

import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;

public interface IDailyClosureDashCallBacks {

    void onGetDailyClosureSuccess(GetUserDailyClosureRes res);

    void onError(String message);

    void onErrorComplete(String s);
}
