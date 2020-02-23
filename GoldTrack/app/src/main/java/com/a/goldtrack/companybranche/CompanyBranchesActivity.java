package com.a.goldtrack.companybranche;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.Model.AddCompanyBranchesReq;
import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.AddCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyBranches;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCompanyBranchesBinding;
import com.a.goldtrack.network.APIService;
import com.a.goldtrack.network.RetrofitClient;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CompanyBranchesActivity extends AppCompatActivity implements View.OnClickListener, CustomCompanyBranchAdapter.CompanyClicked {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.activity_company_branches);
        viewModel = ViewModelProviders.of(this).get(CompanyBranchesViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_company_branches);
        binding.setCmpBrnchModel(viewModel);

        context = CompanyBranchesActivity.this;
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        binding.addSignalBranch.setOnClickListener(this);
        binding.btnAddBranch.setOnClickListener(this);
        GetCompanyBranches reqGet = new GetCompanyBranches();
        reqGet.companyId = Sessions.getUserString(context,Constants.companyId);
        reqGet.branchId = "0";
        getCompanyBranches(reqGet);
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
    private void addCompanyBranches(AddCompanyBranchesReq req) {
        Log.d(TAG, "addCompanyBranches");
        RetrofitClient retrofitSet = new RetrofitClient();
        Retrofit retrofit = retrofitSet.getClient(Constants.BaseUrl);
        APIService apiService = retrofit.create(APIService.class);
        Call<AddCompanyBranchesRes> call = apiService.addCompanyBranches(req);


        progressDialog.show();
        call.enqueue(new Callback<AddCompanyBranchesRes>() {
            @Override
            public void onResponse(Call<AddCompanyBranchesRes> call, Response<AddCompanyBranchesRes> response) {
                progressDialog.dismiss();
                Constants.logPrint(call.request().toString(), req, response.body());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().success) {
                            Constants.Toasty(context, "Branch Added successfully", Constants.success);
                            resetAll();
                            GetCompanyBranches reqGet = new GetCompanyBranches();
                            reqGet.companyId = Sessions.getUserString(context, Constants.companyId);
                            reqGet.branchId = "0";
                            getCompanyBranches(reqGet);
                        } else {
                            Constants.alertDialogShow(context, response.body().response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddCompanyBranchesRes> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Response:", "" + t);
                Constants.alertDialogShow(context, "Something went wrong, please try again");
                t.printStackTrace();
            }
        });

    }

    private void getCompanyBranches(GetCompanyBranches req) {
        Log.d(TAG, "getUserForCompany");
        RetrofitClient retrofitSet = new RetrofitClient();
        Retrofit retrofit = retrofitSet.getClient(Constants.BaseUrl);
        APIService apiService = retrofit.create(APIService.class);
        Call<GetCompanyBranchesRes> call = apiService.getCompanyBranches(req);


        progressDialog.show();
        call.enqueue(new Callback<GetCompanyBranchesRes>() {
            @Override
            public void onResponse(Call<GetCompanyBranchesRes> call, Response<GetCompanyBranchesRes> response) {
                progressDialog.dismiss();
                Constants.logPrint(call.request().toString(), req, response.body());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().success) {
                            mDataset = response.body().resList;
                            setmRecyclerView();
                        } else {
                            Constants.alertDialogShow(context, response.body().response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetCompanyBranchesRes> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Response:", "" + t);
                Constants.alertDialogShow(context, "Something went wrong, please try again");
                t.printStackTrace();
            }
        });

    }

    private void updateCompanyDetails(UpdateCompanyDetails req) {

    }

    private void resetAll() {
        binding.branchName.setText("");
        binding.branchDesc.setText("");
        binding.branchAddress1.setText("");
        binding.branchCity.setText("");
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
        addCompanyBranches(req);
    }

    private void setValidateUpdate() {

    }

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mAdapter = new CustomCompanyBranchAdapter(mDataset);
        mAdapter.setClickListener(this);
        binding.recyclerBranches.setAdapter(mAdapter);
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
    }
}
