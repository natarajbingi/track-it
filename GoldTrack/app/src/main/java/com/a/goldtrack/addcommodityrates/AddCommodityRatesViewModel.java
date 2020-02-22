package com.a.goldtrack.addcommodityrates;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class AddCommodityRatesViewModel extends ViewModel {

    private AddCommodityRatesDataHandler view;
    ObservableField<String> commodity = new ObservableField<>();
    ObservableField<String> rate = new ObservableField<>();
    ObservableField<String> startDate = new ObservableField<>();
    ObservableField<String> endDate = new ObservableField<>();

    void SetView(AddCommodityRatesDataHandler view) {
        this.view = view;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public TextWatcher commodityWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                commodity.set(editable.toString());
            }
        };
    }

    public TextWatcher rateWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rate.set(editable.toString());
            }
        };
    }

    public TextWatcher startDateWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                startDate.set(editable.toString());
            }
        };
    }

    public TextWatcher endDateWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                endDate.set(editable.toString());
            }
        };
    }

}
