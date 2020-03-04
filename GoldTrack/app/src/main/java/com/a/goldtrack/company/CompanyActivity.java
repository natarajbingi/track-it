package com.a.goldtrack.company;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Interfaces.ConnectivityReceiverListener;
import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddCompany;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCompanyBinding;
import com.a.goldtrack.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CompanyActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, ICompanyView, ConnectivityReceiverListener {
    CompanyViewModel viewModel;
    ActivityCompanyBinding binding;
    //ProgressDialog progressDialog;
    Context context;
    boolean viewOrEdit = true;
    GetCompany reqGet;
    private static final String TAG = "CompanyActivity";
    protected CustomCompanyAdapter mAdapter;

    protected Constants.LayoutManagerType mCurrentLayoutManagerType;


    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetCompanyRes.ResList> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CompanyViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_company);
        binding.setCmpModel(viewModel);
        context = CompanyActivity.this;

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.GONE);

        binding.addSignalCompany.setOnClickListener(this);
        binding.btnAddCompany.setOnClickListener(this);
        reqGet = new GetCompany();
        reqGet.companyId = "0";

        // progressDialog.show();
        binding.progressbar.setVisibility(View.VISIBLE);

        viewModel.getCompany(reqGet);
        viewModel.onViewAvailable(this);
        viewModel.list.observe(this, new Observer<GetCompanyRes>() {
            @Override
            public void onChanged(GetCompanyRes getCompanyRes) {
                mDataset = getCompanyRes.resList;
                binding.progressbar.setVisibility(View.GONE);
                //progressDialog.dismiss();
                setmRecyclerView();
            }
        });

        binding.listDetailsHolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //binding.listDetailsHolder.setRefreshing(true);
                binding.progressbar.setVisibility(View.VISIBLE);
                viewModel.getCompany(reqGet);
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

    private void setValidateAdd() {
        AddCompany req = new AddCompany();


        req.name = binding.name.getText().toString();
        req.desc = binding.desc.getText().toString();
        req.mobileNo = binding.mobileNo.getText().toString();
        req.landline = binding.landline.getText().toString();
        req.emailID = binding.emailID.getText().toString();
        req.website = binding.website.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.city = binding.city.getText().toString();
        req.district = binding.district.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.logoImageData = "";
        req.logoImagePath = "";

        if (req.name.isEmpty() || req.mobileNo.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }

        // progressDialog.show();
        binding.progressbar.setVisibility(View.VISIBLE);
        viewModel.addCompany(req);
    }

    private void setValidateUpdate() {
        UpdateCompanyDetails req1 = new UpdateCompanyDetails();
        req1.data = new ArrayList<UpdateCompanyDetails.Data>();
        UpdateCompanyDetails.Data req = new UpdateCompanyDetails.Data();


        req.companyId = binding.companyId.getText().toString().trim();
        req.name = binding.name.getText().toString();
        req.desc = binding.desc.getText().toString();
        req.mobileNo = binding.mobileNo.getText().toString();
        req.landline = binding.landline.getText().toString();
        req.emailID = binding.emailID.getText().toString();
        req.website = binding.website.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.city = binding.city.getText().toString();
        req.district = binding.district.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.delete = false;
        req.modify = true;
        req.add = false;
        req.updatedDt = Constants.getDateNowyyyymmmdd();
        req.updatedBy = "USER";
        req.logoImageData = "";
        req.logoImagePath = "";
        if (req.name.isEmpty() || req.mobileNo.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        req1.data.add(req);
        //  progressDialog.show();
        binding.progressbar.setVisibility(View.VISIBLE);
        viewModel.updateCompany(req1);
    }

    @Override
    public void addCompanyDetailes() {

        Constants.Toasty(context, "Company Added successfully", Constants.success);
        resetAll();
        // progressDialog.dismiss();
        viewModel.getCompany(reqGet);
    }

    @Override
    public void updateCompanyDetailes() {
        // progressDialog.dismiss();
        Constants.Toasty(context, "Company Updated successfully", Constants.success);
        resetAll();
        viewOrEdit = true;
        viewModel.getCompany(reqGet);
    }

    @Override
    public void onSuccessGetCompany(GetCompanyRes model) {
        //progressDialog.dismiss();
        binding.progressbar.setVisibility(View.GONE);
        binding.listDetailsHolder.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onErrorSpread(String msg) {
        binding.progressbar.setVisibility(View.GONE);
        binding.listDetailsHolder.setRefreshing(false);
    }

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomCompanyAdapter(mDataset);
            binding.recyclerBranches.setAdapter(mAdapter);
            mAdapter.setClickListener(this);
        } else
            mAdapter.updateListNew(mDataset);

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


    private void resetAll() {
        binding.name.setText("");
        binding.desc.setText("");
        binding.mobileNo.setText("");
        binding.landline.setText("");
        binding.emailID.setText("");
        binding.website.setText("");
        binding.address1.setText("");
        binding.address2.setText("");
        binding.city.setText("");
        binding.district.setText("");
        binding.state.setText("");
        binding.pin.setText("");

        binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_company:
                if (binding.btnAddCompany.getText().toString().equals("Add Company"))
                    setValidateAdd();
                else
                    setValidateUpdate();
                break;
            case R.id.add_signal_company:

                if (viewOrEdit) {

                    resetAll();
                    binding.btnAddCompany.setText("Add Company");
                    binding.textView.setText("Add Company");

                    binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
        }
    }


    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).name, Constants.info);
        setEditUpdateVals(position);
    }

    private void setEditUpdateVals(int position) {
        binding.companyId.setText(mDataset.get(position).id);
        binding.name.setText(mDataset.get(position).name);
        binding.desc.setText(mDataset.get(position).desc);
        binding.mobileNo.setText(mDataset.get(position).mobileNo);
        binding.landline.setText(mDataset.get(position).landline);
        binding.emailID.setText(mDataset.get(position).emailID);
        binding.website.setText(mDataset.get(position).website);
        binding.address1.setText(mDataset.get(position).address1);
        binding.address2.setText(mDataset.get(position).address2);
        binding.city.setText(mDataset.get(position).city);
        binding.district.setText(mDataset.get(position).district);
        binding.state.setText(mDataset.get(position).state);
        binding.pin.setText(mDataset.get(position).pin);

        binding.btnAddCompany.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);

        viewOrEdit = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        GTrackApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Constants.showSnack(isConnected, binding.textView);
    }
}
