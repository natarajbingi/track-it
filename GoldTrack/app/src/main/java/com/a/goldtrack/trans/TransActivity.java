package com.a.goldtrack.trans;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityTransBinding;

public class TransActivity extends AppCompatActivity {

    TransViewModel viewModel;
    ActivityTransBinding binding;
    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trans);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(TransViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trans);
        binding.setTransModel(viewModel);
        context = TransActivity.this;

        init();
    }

    private void init() {

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
