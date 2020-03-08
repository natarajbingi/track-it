package com.a.goldtrack.companybranche;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddCompanyBranchesReq;
import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyBranches;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.UpdateCompanyBranchesReq;
import com.a.goldtrack.Model.UpdateCompanyBranchesRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCompanyBranchesBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.List;


public class CompanyBranchesActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, IBranchView {

    CompanyBranchesViewModel viewModel;
    ActivityCompanyBranchesBinding binding;

    ProgressDialog progressDialog;

    Context context;
    boolean viewOrEdit = true;
    private static final String TAG = "CompanyBranchesActivity";
    protected CustomCompanyBranchAdapter mAdapter;

    protected Constants.LayoutManagerType mCurrentLayoutManagerType;


    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetCompanyBranchesRes.ResList> mDataset;
    GetCompanyBranches reqGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CompanyBranchesViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_company_branches);
        binding.setCmpBrnchModel(viewModel);
        context = CompanyBranchesActivity.this;

        init();
    }

    private void init() {
        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        binding.addSignalBranch.setOnClickListener(this);
        binding.btnAddBranch.setOnClickListener(this);

        reqGet = new GetCompanyBranches();
        reqGet.companyId = Sessions.getUserString(context, Constants.companyId);
        reqGet.branchId = "0";
        progressDialog.show();
        viewModel.onGetBranch(reqGet);
        viewModel.onViewAvailable(this);
        viewModel.list.observe(this, new Observer<GetCompanyBranchesRes>() {
            @Override
            public void onChanged(GetCompanyBranchesRes getCompanyBranchesRes) {
                mDataset = getCompanyBranchesRes.resList;
                progressDialog.dismiss();
                setmRecyclerView();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_none, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) // API 5+ solution
        {
            finish();//  onBackPressed();
        }
        return true;
    }


    private void resetAll() {
        binding.companyId.setText("");
        binding.branchName.setText("");
        binding.branchDesc.setText("");
        binding.branchAddress1.setText("");
        binding.branchCity.setText("");
        binding.branchPhNumber.setText("");
        binding.branchPin.setText("");


        binding.addSignalBranch.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_branch:
                if (binding.btnAddBranch.getText().toString().equals("Add Branch"))
                    setValidateAdd();
                else
                    setValidateUpdate();
                break;
            case R.id.add_signal_branch:

                if (viewOrEdit) {

                    resetAll();
                    binding.btnAddBranch.setText("Add Branch");
                    binding.textView.setText("Add Branch");

                    binding.addSignalBranch.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalBranch.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
        }
    }

    private void setValidateAdd() {
        AddCompanyBranchesReq req = new AddCompanyBranchesReq();


        req.branchCode = "DYU_" + Constants.getDateNowAll();
        req.companyId = Sessions.getUserString(context, Constants.companyId);
        req.branchName = binding.branchName.getText().toString();
        req.branchDesc = binding.branchDesc.getText().toString();
        req.branchAddress1 = binding.branchAddress1.getText().toString();
        req.branchCity = binding.branchCity.getText().toString();
        req.branchPin = binding.branchPin.getText().toString();
        req.branchPhNumber = binding.branchPhNumber.getText().toString();
        req.createdBy = "USER";


        if (req.branchName.isEmpty() || req.branchPhNumber.isEmpty() || req.branchPin.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        viewModel.onAddBranch(req);
    }

    private void setValidateUpdate() {
        UpdateCompanyBranchesReq req = new UpdateCompanyBranchesReq();

        req.companyId = Sessions.getUserString(context, Constants.companyId);
        req.id = binding.companyId.getText().toString();
        req.branchName = binding.branchName.getText().toString();
        req.branchDesc = binding.branchDesc.getText().toString();
        req.branchAddress1 = binding.branchAddress1.getText().toString();
        req.branchCity = binding.branchCity.getText().toString();
        req.branchPin = binding.branchPin.getText().toString();
        req.branchPhNumber = binding.branchPhNumber.getText().toString();
        req.updatedBy = Sessions.getUserString(context, Constants.userName);
        req.delete=false;


        if (req.branchName.isEmpty() || req.branchPhNumber.isEmpty() || req.branchPin.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        progressDialog.show();
        viewModel.onUpdateBranch(req);
    }

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomCompanyBranchAdapter(context,mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerBranches.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerBranches.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerBranches.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(context, 2);
                mCurrentLayoutManagerType = Constants.LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(context);
                mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(context);
                mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        binding.recyclerBranches.setLayoutManager(mLayoutManager);
        binding.recyclerBranches.scrollToPosition(scrollPosition);
    }


    @Override
    public void oncItemClicked(View view, int position) {

        Constants.Toasty(context, "Edit " + mDataset.get(position).branchName, Constants.info);
        setUpdateVals(position);
    }

    private void setUpdateVals(int position) {

        binding.companyId.setText(mDataset.get(position).id);
        binding.branchName.setText(mDataset.get(position).branchName);
        binding.branchDesc.setText(mDataset.get(position).branchDesc);
        binding.branchAddress1.setText(mDataset.get(position).branchAddress1);
        binding.branchCity.setText(mDataset.get(position).branchCity);
        binding.branchPhNumber.setText(mDataset.get(position).branchPhNumber);
        binding.branchPin.setText(mDataset.get(position).branchPin);

        binding.btnAddBranch.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalBranch.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);

        viewOrEdit = false;
    }

    @Override
    public void onSuccessGetBranch() {
        progressDialog.dismiss();
    }

    @Override
    public void onSuccessAddBranch(AddCompanyBranchesRes branchesRes) {
        if (branchesRes.success) {
            Constants.Toasty(context, "Branch Added successfully", Constants.success);
            resetAll();
            viewModel.onGetBranch(reqGet);
        } else {
            progressDialog.dismiss();
            Constants.alertDialogShow(context, branchesRes.response);
        }
    }

    @Override
    public void onSuccessUpdateBranch(UpdateCompanyBranchesRes res) {
        // progressDialog.dismiss();
        Constants.Toasty(context, "Branch Updated successfully", Constants.success);
        resetAll();
        viewModel.onGetBranch(reqGet);
    }

    @Override
    public void onErrorBranch(String msg) {

        Constants.Toasty(context, msg, Constants.warning);
        progressDialog.dismiss();
    }

}
