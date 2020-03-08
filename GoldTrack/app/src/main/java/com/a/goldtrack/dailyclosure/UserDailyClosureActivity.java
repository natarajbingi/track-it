package com.a.goldtrack.dailyclosure;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddItemReq;
import com.a.goldtrack.Model.AddUserDailyClosureReq;
import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateItemReq;
import com.a.goldtrack.Model.UpdateUserDailyClosureReq;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityUserDailyClosureBinding;
import com.a.goldtrack.items.CustomItemsAdapter;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDailyClosureActivity extends AppCompatActivity implements View.OnClickListener, IDailyClosureView, RecycleItemClicked, DatePickerDialog.OnDateSetListener {

    UserDailyClosureViewModel viewModel;
    ActivityUserDailyClosureBinding binding;
    protected CustomDailyClosureAdapter mAdapter;
    ProgressDialog progressDialog;
    Context context;
    GetUserDailyClosureReq req;
    boolean viewOrEdit = true;

    protected Constants.LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetUserDailyClosureRes.DataList> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(UserDailyClosureViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_daily_closure);
        binding.setDClosureModel(viewModel);
        context = UserDailyClosureActivity.this;
        init();
    }

    Map<String, String> branchesArr = null;

    void init() {

        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        viewModel.onViewAvailable(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        DropdownDataForCompanyRes dropdownRes = (DropdownDataForCompanyRes) Sessions.getUserObj(context, Constants.dorpDownSession, DropdownDataForCompanyRes.class);
        if (dropdownRes != null) {
            branchesArr = new LinkedHashMap<String, String>();
            branchesArr.put("Select", "Select");
            /* Branches */
            try {
                for (int i = 0; i < dropdownRes.branchesList.size(); i++) {
                    branchesArr.put(dropdownRes.branchesList.get(i).branchName.toUpperCase()
                            + "-" + dropdownRes.branchesList.get(i).id, dropdownRes.branchesList.get(i).id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Constants.Toasty(context, "Please refresh home screen to load details", Constants.error);
        }

        binding.dateClosure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        UserDailyClosureActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });
        viewModel.list.observe(this, new Observer<GetUserDailyClosureRes>() {
            @Override
            public void onChanged(GetUserDailyClosureRes getUserDailyClosureRes) {
                progressDialog.dismiss();
                mDataset = getUserDailyClosureRes.dataList;
                setmRecyclerView();
            }
        });
        binding.addSignalClosure.setOnClickListener(this);
        binding.btnAddClosure.setOnClickListener(this);

        req = new GetUserDailyClosureReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.branchID = "0";//Sessions.getUserString(context, Constants.companyId);
        String role = Sessions.getUserString(context, Constants.roles);
        if (role.equals("ADMIN") || role.equals("SUPER_ADMIN")) {
            req.userID = "0";
        } else
            req.userID = Sessions.getUserString(context, Constants.userId);

        progressDialog.show();
        viewModel.getDailyClosures(req);
        setSpinners(binding.selectBranch, branchesArr.keySet().toArray(new String[0]));
    }

    private void setSpinners(Spinner spr, String[] array) {
        // -----------------------------------------------
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                R.layout.custom_spinner,
                array);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // The drop down view
        spr.setAdapter(spinnerArrayAdapter);
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
            mAdapter = new CustomDailyClosureAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerDailyClosures.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerDailyClosures.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerDailyClosures.getLayoutManager())
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

        binding.recyclerDailyClosures.setLayoutManager(mLayoutManager);
        binding.recyclerDailyClosures.scrollToPosition(scrollPosition);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add_closure:
                if (binding.btnAddClosure.getText().toString().equals("ADD"))
                    setValidateAdd();
                else {
                    String role = Sessions.getUserString(context, Constants.roles);
                    if (role.equals("ADMIN") || role.equals("SUPER_ADMIN")) {
                        setValidateUpdate();
                    } else
                        Constants.Toasty(context, "Only Admin can update Daily Closure details.", Constants.info);
                }
                break;
            case R.id.add_signal_closure:

                if (viewOrEdit) {
                    resetAll();
                    binding.btnAddClosure.setText("ADD");
                    binding.textView.setText("Add today's closure");

                    binding.addSignalClosure.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalClosure.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
        }
    }

    private void setValidateAdd() {
        AddUserDailyClosureReq req = new AddUserDailyClosureReq();


        req.companyId = Sessions.getUserString(context, Constants.companyId);
        req.branchId = binding.selectBranch.getSelectedItem().toString().split("-")[1];
        req.userId = Sessions.getUserString(context, Constants.userId);

        req.date = binding.dateClosure.getText().toString();
        req.fundRecieved = binding.fundRecieved.getText().toString();
        req.expenses = binding.expenses.getText().toString();
        req.expensesDesc = binding.expensesDesc.getText().toString();
        req.cashInHand = binding.cashInHand.getText().toString();
        req.comments = binding.comments.getText().toString();
        req.createdBy = Sessions.getUserString(context, Constants.userId);


        if (req.comments.isEmpty() || req.date.isEmpty() || req.cashInHand.isEmpty() || req.fundRecieved.isEmpty() || req.branchId.equalsIgnoreCase("Select")) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        progressDialog.show();
        viewModel.addDailyClosure(req);
    }

    private void setValidateUpdate() {
        UpdateUserDailyClosureReq req = new UpdateUserDailyClosureReq();
        UpdateUserDailyClosureReq.Data reqData = new UpdateUserDailyClosureReq.Data();

        reqData.id = binding.companyId.getText().toString().trim();
        //reqData.userId =
        reqData.companyId = Sessions.getUserString(context, Constants.companyId);
        //   reqData.branchId =Sessions.getUserString(context,Constants.companyId);
        reqData.date = binding.dateClosure.getText().toString();
        reqData.fundRecieved = binding.fundRecieved.getText().toString();
        reqData.expenses = binding.expenses.getText().toString();
        reqData.cashInHand = binding.cashInHand.getText().toString();
        reqData.expensesDesc = binding.expensesDesc.getText().toString();
        reqData.comments = binding.comments.getText().toString();
        reqData.updatedBy = Sessions.getUserString(context, Constants.userId);
        reqData.delete = false;

        if (reqData.date.isEmpty() || reqData.fundRecieved.isEmpty() || reqData.comments.isEmpty() || reqData.cashInHand.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }

        req.data = new ArrayList<>();
        req.data.add(reqData);

        progressDialog.show();
        viewModel.updateDailyClosure(req);
    }

    private void resetAll() {
        binding.selectBranch.setSelection(0);
        binding.dateClosure.setText("");
        binding.fundRecieved.setText("");
        binding.companyId.setText("");
        binding.expenses.setText("");
        binding.cashInHand.setText("");
        binding.expensesDesc.setText("");
        binding.comments.setText("");
        binding.btnAddClosure.setText("ADD");
        binding.textView.setText("Add today's closure");

        binding.addSignalClosure.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.extraData.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.GONE);

    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).userName, Constants.info);
        setEditUpdateVals(position);
    }

    private void setEditUpdateVals(int position) {

        //  binding.selectBranch.setSelection(0);
        binding.companyId.setText(mDataset.get(position).id);
        binding.dateClosure.setText(mDataset.get(position).date);
        binding.fundRecieved.setText(mDataset.get(position).fundRecieved);
        binding.expenses.setText(mDataset.get(position).expenses);
        binding.cashInHand.setText(mDataset.get(position).cashInHand);
        binding.expensesDesc.setText(mDataset.get(position).expensesDesc);
        binding.comments.setText(mDataset.get(position).comments);
        binding.selectBranch.setSelection(
                ((ArrayAdapter<String>)
                        binding.selectBranch.getAdapter()
                ).getPosition(mDataset.get(position).branchId));


        String extra =
                "User Name: " + mDataset.get(position).userName
                        + "\nCompany Name: " + mDataset.get(position).companyName+"\n";
        binding.extraData.setText(extra);
        binding.extraData.setVisibility(View.VISIBLE);
        binding.btnAddClosure.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalClosure.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);
        viewOrEdit = !viewOrEdit;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        binding.dateClosure.setText(date);
    }

    @Override
    public void onGetDailyClosureSuccess(GetUserDailyClosureRes res) {

    }

    @Override
    public void onAddDailyClousureSuccess(AddUserDailyClosureRes res) {
        if (res.success) {
            Constants.Toasty(context, "Today's Closure Added successfully", Constants.success);
            resetAll();
            viewModel.getDailyClosures(req);
        } else {
            Constants.alertDialogShow(context, res.response);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onUpdateDailyClousureSuccess(UpdateUserDailyClosureRes res) {
        if (res.success) {
            Constants.Toasty(context, "Today's Closure Added successfully", Constants.success);
            resetAll();
            viewModel.getDailyClosures(req);
        } else {
            Constants.alertDialogShow(context, res.response);
            progressDialog.dismiss();
        }

    }

    @Override
    public void onError(String message) {
        progressDialog.dismiss();
        Constants.Toasty(context, message, Constants.error);
    }

    @Override
    public void onErrorComplete(String s) {
        progressDialog.dismiss();
        Constants.Toasty(context, s, Constants.error);
    }
}
