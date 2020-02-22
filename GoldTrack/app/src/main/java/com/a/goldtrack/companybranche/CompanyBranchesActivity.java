package com.a.goldtrack.companybranche;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCompanyBinding;
import com.a.goldtrack.databinding.ActivityCompanyBranchesBinding;

public class CompanyBranchesActivity extends AppCompatActivity {

    CompanyBranchesViewModel viewModel;
    ActivityCompanyBranchesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.activity_company_branches);
        viewModel = ViewModelProviders.of(this).get(CompanyBranchesViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_company_branches);
        binding.setCmpBrnchModel(viewModel);

    }
}
