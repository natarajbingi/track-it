package com.a.goldtrack.register;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    ObservableField<String> inputName = null;
    ObservableField<String> edEmail = null;
    ObservableField<String> edPassword = null;
    ObservableField<String> inputAddress = null;
    ObservableField<String> inputMobile = null;
    RegisterDataHandler view;

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    void SetView(RegisterDataHandler view) {
        this.view = view;
    }

    public void loginGoMe() {
        view.onClickTextViewLogin();
    }
}
