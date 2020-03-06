package com.a.goldtrack.customer;

import androidx.appcompat.app.ActionBar;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddCustomerReq;
import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.GetCustomerReq;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.UpdateCustomerReq;
import com.a.goldtrack.Model.UpdateCustomerRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCustomerBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, ICustomerhandler {

    CustomerViewModel viewModel;
    ActivityCustomerBinding binding;
    Context context;

    boolean viewOrEdit = true;
    private static final String TAG = "CustomerActivity";
    protected CustomCustomersAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetCustomerRes.ResList> mDataset;
    GetCustomerReq custReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = CustomerActivity.this;
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(CustomerViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer);
        binding.setCustModel(viewModel);


        init();
    }

    void init() {

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.GONE);

        binding.addSignalCustomer.setOnClickListener(this);
        binding.btnAddCustomer.setOnClickListener(this);

        custReq = new GetCustomerReq();
        //  custReq.companyID = Sessions.getUserString(context, Constants.companyId);
        custReq.customerId = "0";
        viewModel.onViewAvailable(this);
        binding.progressbar.setVisibility(View.VISIBLE);
        viewModel.getCustomer(custReq);
        viewModel.list.observe(this, new Observer<GetCustomerRes>() {
            @Override
            public void onChanged(GetCustomerRes resList) {
                mDataset = resList.resList;
                //progressDialog.dismiss();
                setmRecyclerView();
            }
        });

        try {
            binding.search.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.listDetailsHolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //binding.listDetailsHolder.setRefreshing(true);
                binding.progressbar.setVisibility(View.VISIBLE);
                viewModel.getCustomer(custReq);
            }
        });


        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void setValidateAdd() {
        AddCustomerReq req = new AddCustomerReq();
        req.firstName = binding.firstName.getText().toString();
        req.lastName = binding.lastName.getText().toString();
        req.mobileNum = binding.mobileNum.getText().toString();
        req.emailId = binding.emailId.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.createdBy = Sessions.getUserString(context, Constants.userName);

        if (req.firstName.isEmpty() || req.mobileNum.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please fill the mandatory fields.", Constants.warning);
            return;
        }
        binding.progressbar.setVisibility(View.VISIBLE);
        viewModel.addCustomer(req);
    }

    private void setValidateUpdate() {
        UpdateCustomerReq req = new UpdateCustomerReq();
        req.id = binding.companyId.getText().toString();
        req.firstName = binding.firstName.getText().toString();
        req.lastName = binding.lastName.getText().toString();
        req.mobileNum = binding.mobileNum.getText().toString();
        req.emailId = binding.emailId.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.delete = false;
        //req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.updatedBy = Sessions.getUserString(context, Constants.userName);

        if (req.firstName.isEmpty() || req.mobileNum.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please fill the mandatory fields.", Constants.warning);
            return;
        }
        binding.progressbar.setVisibility(View.VISIBLE);
        viewModel.updateCustomer(req);
    }

    private void setmRecyclerView() {
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

    private void resetAll() {
        binding.companyId.setText("");
        binding.firstName.setText("");
        binding.lastName.setText("");
        binding.mobileNum.setText("");
        binding.emailId.setText("");
        binding.address1.setText("");
        binding.address2.setText("");
        binding.state.setText("");
        binding.pin.setText("");

        binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
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


    public void filter(String s) {
        Log.d("mDataset", "" + mDataset.size());
        List<GetCustomerRes.ResList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (GetCustomerRes.ResList d : mDataset) {

                if (d.firstName.toLowerCase().contains(s.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (mAdapter != null) {
                mAdapter.updateListNew(temp);
            }
        } else {
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_customer:
                if (binding.btnAddCustomer.getText().toString().equals("Add Customer"))
                    setValidateAdd();
                else
                    setValidateUpdate();
                break;
            case R.id.add_signal_customer:
                if (viewOrEdit) {

                    resetAll();
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
        Constants.Toasty(context, "Customer Added successfully", Constants.success);
        resetAll();
        viewOrEdit = true;
        viewModel.getCustomer(custReq);
    }

    @Override
    public void updateCustomerSuccess(UpdateCustomerRes res) {
        Constants.Toasty(context, "Customer Updated successfully", Constants.success);
        resetAll();
        viewOrEdit = true;
        viewModel.getCustomer(custReq);
    }


    @Override
    public void getCustomerSuccess(GetCustomerRes res) {
        binding.progressbar.setVisibility(View.GONE);
        binding.listDetailsHolder.setRefreshing(false);
    }

    @Override
    public void onErrorSpread(String msg) {
        Constants.Toasty(context, "Something went wrong, reason: " + msg, Constants.error);
    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).firstName, Constants.info);
        setEditUpdateVals(position);
    }

}
