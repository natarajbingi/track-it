package com.a.goldtrack.addcommodityrates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityAddCommodityRatesBinding;

public class AddCommodityRatesActivity extends AppCompatActivity implements AddCommodityRatesDataHandler {

    AddCommodityRatesViewModel viewModel;
    ActivityAddCommodityRatesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_add_commodity_rates);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_commodity_rates);
        viewModel = ViewModelProviders.of(this).get(AddCommodityRatesViewModel.class);
        binding.setCmdRagesModel(viewModel);

    }
}
