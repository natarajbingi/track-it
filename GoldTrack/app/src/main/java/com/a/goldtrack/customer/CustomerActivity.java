package com.a.goldtrack.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.GetCustomerReq;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.Model.UpdateCustomerRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCustomerBinding;
import com.a.goldtrack.users.CustomUsersAdapter;
import com.a.goldtrack.users.UserCompanyHandler;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, ICustomerhandler {

    CustomerViewModel viewModel;
    ActivityCustomerBinding binding;
    Context context;
    boolean viewOrEdit = true;
    private static final String TAG = "UserCompanyActivity";
    protected CustomCustomersAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetCustomerRes.ResList> mDataset;
    GetCustomerReq custReq;
    List<String> rolesList = new ArrayList<>();
    private int userIdIfEditing = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_customer);
        context = CustomerActivity.this;
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(CustomerViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer);
        binding.setCustModel(viewModel);

        custReq = new GetCustomerReq();
        custReq.companyID = Sessions.getUserString(context, Constants.companyId);
        custReq.customerId = "0";
        viewModel.onViewAvailable(this);
        binding.progressbar.setVisibility(View.VISIBLE);
        viewModel.getCustomer(custReq);
        viewModel.list.observe(this, new Observer<GetCustomerRes>() {
            @Override
            public void onChanged(GetCustomerRes resList) {
                mDataset = resList.resList;
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
                viewModel.getCustomer(custReq);
            }
        });

    }

    void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.GONE);

        binding.addSignalCustomer.setOnClickListener(this);
        binding.btnAddCustomer.setOnClickListener(this);
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

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomCustomersAdapter(mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerCustomer.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerCustomer.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerCustomer.getLayoutManager())
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

        binding.recyclerCustomer.setLayoutManager(mLayoutManager);
        binding.recyclerCustomer.scrollToPosition(scrollPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_customer:
                //   if (binding.btnAddCustomer.getText().toString().equals("Add Customer"))
                //  setValidateAdd();
                //  else
                // setValidateUpdate();
                break;
            case R.id.add_signal_customer:

                if (viewOrEdit) {

                    //   resetAll();
                    binding.btnAddCustomer.setText("Add Customer");
                    binding.textView.setText("Add Customer");

                    binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
        }
    }


    @Override
    public void addCustomerSuccess(AddCustomerRes res) {

    }

    @Override
    public void updateCustomerSuccess(UpdateCustomerRes res) {

    }


    @Override
    public void getCustomerSuccess(GetCustomerRes res) {

    }

    @Override
    public void onErrorSpread(String msg) {

    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + position/*mDataset.get(position).name*/, Constants.info);
        setEditUpdateVals(position);
    }

    private void setEditUpdateVals(int position) {
        binding.companyId.setText(mDataset.get(position).id);
        binding.firstName.setText(mDataset.get(position).firstName);
        binding.lastName.setText(mDataset.get(position).lastName);
        binding.mobileNum.setText(mDataset.get(position).mobileNum);
        binding.emailId.setText(mDataset.get(position).emailId);
        binding.address1.setText(mDataset.get(position).address1);
        binding.address2.setText(mDataset.get(position).address2);
        binding.state.setText(mDataset.get(position).state);
        binding.pin.setText(mDataset.get(position).pin);

        binding.btnAddCustomer.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);

        viewOrEdit = false;
    }
}
