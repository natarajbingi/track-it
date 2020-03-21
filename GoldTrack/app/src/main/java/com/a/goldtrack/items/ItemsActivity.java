package com.a.goldtrack.items;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddItemReq;
import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.GetItemsReq;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.UpdateItemReq;
import com.a.goldtrack.Model.UpdateItemRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityItemsBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.ArrayList;
import java.util.List;


public class ItemsActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, IItemsView {

    ItemsViewModel viewModel;
    ActivityItemsBinding binding;
    ProgressDialog progressDialog;
    Context context;
    boolean viewOrEdit = true;
    GetItemsReq reqGet;
    private static final String TAG = "ItemsActivity";
    protected CustomItemsAdapter mAdapter;

    protected Constants.LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<DropdownDataForCompanyRes.ItemsList> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_items);
        binding.setItemsModel(viewModel);
        context = ItemsActivity.this;

        init();
    }

    void init() {

        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        binding.addSignalItem.setOnClickListener(this);
        binding.btnAddItem.setOnClickListener(this);
        reqGet = new GetItemsReq();
        reqGet.companyId = Sessions.getUserString(context, Constants.companyId);
        reqGet.itemId = "0";

        progressDialog.show();
        viewModel.onGetItemsForCompany(reqGet);
        viewModel.onViewAvailable(this);
        viewModel.list.observe(this, new Observer<List<DropdownDataForCompanyRes.ItemsList>>() {
            @Override
            public void onChanged(List<DropdownDataForCompanyRes.ItemsList> customerLists) {
                mDataset = customerLists;
                progressDialog.dismiss();
                setmRecyclerView();
            }
        });
        try {
            binding.search.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // binding.triggImgGet.setOnClickListener(this);
    }

    public void filter(String s) {
        Log.d("mDataset", "" + mDataset.size());
        List<DropdownDataForCompanyRes.ItemsList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (DropdownDataForCompanyRes.ItemsList d : mDataset) {

                if (d.itemName.toLowerCase().contains(s.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (mAdapter != null) {
                mAdapter.updateListNew(temp);
            }
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

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomItemsAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerItems.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerItems.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerItems.getLayoutManager())
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

        binding.recyclerItems.setLayoutManager(mLayoutManager);
        binding.recyclerItems.scrollToPosition(scrollPosition);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add_item:
                if (binding.btnAddItem.getText().toString().equals("Add Item"))
                    setValidateAdd();
                else
                    setValidateUpdate();
                break;
            case R.id.add_signal_item:

                if (viewOrEdit) {

                    resetAll();
                    binding.btnAddItem.setText("Add Item");
                    binding.textView.setText("Add Item");

                    binding.addSignalItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
        }
    }

    private void setValidateAdd() {
        AddItemReq req = new AddItemReq();


        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.commodity = binding.commodity.getSelectedItem().toString();
        req.itemDesc = binding.itemDesc.getText().toString();
        req.itemName = binding.itemName.getText().toString();


        if (req.itemName.isEmpty() || req.commodity.equalsIgnoreCase("Select")) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        progressDialog.show();
        viewModel.onAddItem(req);
    }

    private void setValidateUpdate() {
        UpdateItemReq req = new UpdateItemReq();

        req.id = binding.companyId.getText().toString().trim();
        req.itemName = binding.itemName.getText().toString();
        req.itemDesc = binding.itemDesc.getText().toString();
        req.delete = false;
        req.modify = true;
        if (req.itemName.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }

        progressDialog.show();
        viewModel.onUpdateItemDetails(req);
    }

    private void resetAll() {
        binding.commodity.setSelection(0);
        binding.itemName.setText("");
        binding.itemDesc.setText("");
        binding.companyId.setText("");

        binding.addSignalItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).itemName, Constants.info);
        setEditUpdateVals(position);
    }

    private void setEditUpdateVals(int position) {
        binding.companyId.setText(mDataset.get(position).id);
        switch (mDataset.get(position).commodity) {
            case "PLATINUM":
                binding.commodity.setSelection(1);
                break;
            case "GOLD":
                binding.commodity.setSelection(2);
                break;
            case "SILVER":
                binding.commodity.setSelection(3);
                break;
            default:
                binding.commodity.setSelection(0);
                break;
        }
        binding.itemName.setText(mDataset.get(position).itemName);
        binding.itemDesc.setText(mDataset.get(position).itemDesc);

        binding.btnAddItem.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);

        viewOrEdit = false;
    }

    @Override
    public void onItemGetSuccess(GetItemsRes getItemsRes) {
        progressDialog.dismiss();
        /*if (getItemsRes.success) {
            mDataset = getItemsRes.resList;
            setmRecyclerView();
        } else {
            Constants.alertDialogShow(context, getItemsRes.response);
            progressDialog.dismiss();
        }*/
    }

    @Override
    public void onItemAddSuccess(AddItemRes itemRes) {
        if (itemRes.success) {
            Constants.Toasty(context, "Company Added successfully", Constants.success);
            resetAll();
            viewModel.onGetItemsForCompany(reqGet);
        } else {
            Constants.alertDialogShow(context, itemRes.response);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onItemUpdateSuccess(UpdateItemRes itemRes) {
        if (itemRes.success) {
            Constants.Toasty(context, "Item Updated successfully", Constants.success);
            resetAll();
            viewOrEdit = true;
            viewModel.onGetItemsForCompany(reqGet);
        } else {
            Constants.alertDialogShow(context, itemRes.response);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onGetDrpSuccess(DropdownDataForCompanyRes res) {
        progressDialog.dismiss();
        Sessions.setUserObj(context, res, Constants.dorpDownSession);

    }

    @Override
    public void onError(String msg) {
        progressDialog.dismiss();
    }

}
