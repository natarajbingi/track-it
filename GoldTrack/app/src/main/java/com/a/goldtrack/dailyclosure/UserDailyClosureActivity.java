package com.a.goldtrack.dailyclosure;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddUserDailyClosureReq;
import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateUserDailyClosureReq;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityUserDailyClosureBinding;
import com.a.goldtrack.utils.BaseActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.LoaderDecorator;
import com.a.goldtrack.utils.Sessions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDailyClosureActivity extends BaseActivity implements View.OnClickListener, IDailyClosureView, RecycleItemClicked, DatePickerDialog.OnDateSetListener {

    UserDailyClosureViewModel viewModel;
    ActivityUserDailyClosureBinding binding;

    protected CustomDailyClosureAdapter mAdapter;
    // ProgressDialog progressDialog;
    Context context;
    GetUserDailyClosureReq req;
    boolean viewOrEdit = true, role = false;
    int whichDate = 0, filterDate = 1, closureDate = 2;

    protected List<GetUserDailyClosureRes.DataList> mDataset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(UserDailyClosureViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_daily_closure);
        binding.setDClosureModel(viewModel);
        context = UserDailyClosureActivity.this;

        loader = new LoaderDecorator(context);
        init();
    }

    Map<String, String> branchesArr = null;
    boolean holderFilter = true;

    void init() {
        String str = Sessions.getUserString(context, Constants.roles);
        role = str.equals("ADMIN") || str.equals("SUPER_ADMIN");

        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.progressBarForTrans.setVisibility(View.GONE);
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

        binding.imgDateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnAddClosure.getText().toString().equalsIgnoreCase("Add")) {
                    callDatePicker();
                    whichDate = closureDate;
                }
            }
        });
        binding.imgDateClickFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDatePicker();
                whichDate = filterDate;
            }
        });
        viewModel.totalAmt.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double totTrans) {
                binding.totalAmt.setText("" + totTrans);
            }
        });
        binding.filterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dateClosureFilter.setText("");
                binding.selectBranchFilter.setSelection(0);
                viewModel.getDailyClosures(req);
            }
        });
        binding.filterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strBrnc = binding.selectBranchFilter.getSelectedItem().toString();
                String dateFilr = binding.dateClosureFilter.getText().toString();
                if (strBrnc.equals("Select")) {
                    strBrnc = "0";
                } else
                    strBrnc = branchesArr.get(strBrnc);

                GetUserDailyClosureReq req = new GetUserDailyClosureReq();
                req.companyID = Sessions.getUserString(context, Constants.companyId);
                req.branchID = strBrnc == null ? "0" : strBrnc;
                req.date = dateFilr;
                if (role) {
                    req.userID = "0";
                } else
                    req.userID = Sessions.getUserString(context, Constants.userId);


                viewModel.getDailyClosures(req);
            }
        });

        viewModel.list.observe(this, new Observer<GetUserDailyClosureRes>() {
            @Override
            public void onChanged(GetUserDailyClosureRes getUserDailyClosureRes) {
                mDataset = getUserDailyClosureRes.dataList;
                setmRecyclerView();
            }
        });
        binding.addSignalClosure.setOnClickListener(this);
        binding.btnAddClosure.setOnClickListener(this);
        binding.fetchTrans.setOnClickListener(this);

        binding.filterReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holderFilter) {
                    binding.filterHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.filterHolder.setVisibility(View.GONE);
                }
                holderFilter = !holderFilter;
            }
        });
        binding.selectBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.dateClosure.setText("");
                binding.fundRecieved.setText("");
                binding.expenses.setText("");
                binding.expensesDesc.setText("");
                binding.cashInHand.setText("");
                binding.totalAmt.setText("");
                binding.comments.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.selectUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.selectBranch.setSelection(0);
                binding.dateClosure.setText("");
                binding.fundRecieved.setText("");
                binding.expenses.setText("");
                binding.expensesDesc.setText("");
                binding.cashInHand.setText("");
                binding.totalAmt.setText("");
                binding.comments.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.expenses.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String totalAmt = binding.totalAmt.getText().toString();
                    double totTrans = Double.parseDouble(totalAmt.isEmpty() ? "0" : totalAmt);
                    String str = binding.expenses.getText().toString();
                    double expesnse = Double.parseDouble(str.isEmpty() ? "0" : str);
                    String recFund = binding.fundRecieved.getText().toString();
                    str = (Double.parseDouble(recFund.isEmpty() ? "0" : recFund) - (totTrans + expesnse)) + "";
                    binding.cashInHand.setText(str);
                }
                //else if (binding.btnAddClosure.getText().toString().equalsIgnoreCase("Add") && !b) {
//                    gettingTrans();
//                }
            }
        });

        req = new GetUserDailyClosureReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.branchID = "0";//Sessions.getUserString(context, Constants.companyId);
        if (role) {
            req.userID = "0";
        } else
            req.userID = Sessions.getUserString(context, Constants.userId);


        viewModel.getDailyClosures(req);

        try {
            Constants.setSpinners(binding.selectBranch, branchesArr.keySet().toArray(new String[0]));
            Constants.setSpinners(binding.selectBranchFilter, branchesArr.keySet().toArray(new String[0]));
            Constants.setSpinners(binding.selectUser, Constants.usersArr.keySet().toArray(new String[0]));
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
    }

    public void filter(String s) {
        Log.d("mDataset", "" + mDataset.size());
        List<GetUserDailyClosureRes.DataList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (GetUserDailyClosureRes.DataList d : mDataset) {

                if (d.userName.toLowerCase().contains(s.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (mAdapter != null) {
                mAdapter.updateListNew(temp);
            }
        } else {
        }
    }
    private void callDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                UserDailyClosureActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setMaxDate(now);
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    void gettingTrans() {
        GetTransactionReq reqTrans = new GetTransactionReq();
        // String recFund = binding.fundRecieved.getText().toString();
        reqTrans.transactionDate = binding.dateClosure.getText().toString();
        reqTrans.companyID = Sessions.getUserString(context, Constants.companyId);

        if (binding.btnAddClosure.getText().toString().contains("Update")) {
            String[] sDete = dete.split("_");
            reqTrans.branchID = sDete[0];
            reqTrans.employeeID = sDete[1];
        } else {
            reqTrans.branchID = branchesArr.get(binding.selectBranch.getSelectedItem().toString());
            String empId = Constants.usersArr.get(binding.selectUser.getSelectedItem().toString());
            reqTrans.employeeID = empId;
        }
        if (reqTrans.branchID.equals("Select") || reqTrans.employeeID.equals("Select") || reqTrans.transactionDate.isEmpty()) {
            Constants.Toasty(context, "Please select User , Branch and date");
        } else {
            binding.progressBarForTrans.setVisibility(View.VISIBLE);
            binding.btnAddClosure.setEnabled(false);
            viewModel.getTrans(reqTrans);
        }
    }

   /* private void setSpinners(Spinner spr, String[] array) {
        // -----------------------------------------------
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                R.layout.custom_spinner,
                array);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // The drop down view
        spr.setAdapter(spinnerArrayAdapter);
    }*/

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
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(context, mCurrentLayoutManagerType, binding.recyclerDailyClosures);
        if (mAdapter == null) {
            mAdapter = new CustomDailyClosureAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerDailyClosures.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add_closure:
                if (binding.btnAddClosure.getText().toString().equals("ADD")) {
                    double totTrans = Double.parseDouble(Constants.isEmptyReturn0(binding.totalAmt.getText().toString()));
                    String str = binding.expenses.getText().toString();
                    double expesnse = Double.parseDouble(Constants.isEmptyReturn0(str));
                    String recFund = binding.fundRecieved.getText().toString();
                    str = (Double.parseDouble(recFund.isEmpty() ? "0" : recFund) - (totTrans + expesnse)) + "";
                    binding.cashInHand.setText(str);

                    Constants.alertDialogShowWithCancel(context, "Are you sure want to proceed?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setValidateAdd();
                        }
                    });
                } else {
                    if (role) {
                        Constants.alertDialogShowWithCancel(context, "Are you sure want to proceed to update?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setValidateUpdate();
                            }
                        });
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

            case R.id.fetchTrans:
                gettingTrans();
                break;
        }
    }

    private void setValidateAdd() {
        AddUserDailyClosureReq req = new AddUserDailyClosureReq();

        req.companyId = Sessions.getUserString(context, Constants.companyId);
        req.branchId = branchesArr.get(binding.selectBranch.getSelectedItem().toString());//.split("-")[1];
        // req.userId = Sessions.getUserString(context, Constants.userId);
        req.userId = Constants.usersArr.get(binding.selectUser.getSelectedItem().toString());

        req.date = binding.dateClosure.getText().toString();
        req.fundRecieved = binding.fundRecieved.getText().toString();
        req.expenses = binding.expenses.getText().toString();
        req.expensesDesc = binding.expensesDesc.getText().toString();
        req.cashInHand = binding.cashInHand.getText().toString();
        req.comments = binding.comments.getText().toString();
        req.createdBy = Sessions.getUserString(context, Constants.userId);

        if (req.userId.equals("Select")) {
            Constants.Toasty(context, "Please select user to proceed", Constants.warning);
        }

        if (req.comments.isEmpty() || req.date.isEmpty() || req.cashInHand.isEmpty()
                || req.fundRecieved.isEmpty() || req.branchId.equalsIgnoreCase("Select")) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }

        req.comments = req.comments + "_" + binding.totalAmt.getText().toString();

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

        if (reqData.date.isEmpty() || reqData.fundRecieved.isEmpty() || reqData.comments.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        reqData.comments = reqData.comments + "_" + binding.totalAmt.getText().toString();

        req.data = new ArrayList<>();
        req.data.add(reqData);

        viewModel.updateDailyClosure(req);
    }

    private void resetAll() {
        binding.selectBranch.setSelection(0);
        binding.selectUser.setSelection(0);
        binding.dateClosure.setText("");
        binding.fundRecieved.setText("");
        binding.companyId.setText("");
        binding.expenses.setText("");
        binding.cashInHand.setText("");
        binding.expensesDesc.setText("");
        binding.comments.setText("");
        binding.btnAddClosure.setText("ADD");
        binding.btnAddClosure.setEnabled(true);
        binding.textView.setText("Add today's closure");
        binding.totalAmt.setText("");
        viewModel.totalAmt.postValue(0.0);

        binding.addSignalClosure.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.extraData.setVisibility(View.GONE);
        binding.selectBranchLayout.setVisibility(View.VISIBLE);
        binding.selectUserLayout.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.fetchTrans.setVisibility(View.GONE);

    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).userName, Constants.info);
        setEditUpdateVals(position);
    }

    String dete = "";

    private void setEditUpdateVals(int position) {

        //  binding.selectBranch.setSelection(0);
        binding.companyId.setText(mDataset.get(position).id);
        binding.dateClosure.setText(mDataset.get(position).date);
        binding.fundRecieved.setText(mDataset.get(position).fundRecieved);
        binding.expenses.setText(mDataset.get(position).expenses);
        binding.cashInHand.setText(mDataset.get(position).cashInHand);
        binding.expensesDesc.setText(mDataset.get(position).expensesDesc);
        dete = mDataset.get(position).branchId + "_" + mDataset.get(position).userId;
        if (mDataset.get(position).comments.contains("_")) {
            String[] cmts = mDataset.get(position).comments.split("_");
            binding.comments.setText(cmts[0]);
            binding.totalAmt.setText(cmts[1]);

        } else
            binding.comments.setText(mDataset.get(position).comments);

        binding.selectBranchLayout.setVisibility(View.GONE);
        binding.selectUserLayout.setVisibility(View.GONE);
        binding.extraData.setVisibility(View.VISIBLE);

        String extra = "User: " + mDataset.get(position).userName
                + "\nBranch: " + mDataset.get(position).companyBranchName
                + "\nCompany Name: " + mDataset.get(position).companyName
                + "\n";
        binding.extraData.setText(extra);

        double expesnse = Double.parseDouble(mDataset.get(position).expenses.isEmpty() ? "0" : mDataset.get(position).expenses);
        double cashinHd = Double.parseDouble(mDataset.get(position).cashInHand.isEmpty() ? "0" : mDataset.get(position).cashInHand);
        double recFund = Double.parseDouble(mDataset.get(position).fundRecieved.isEmpty() ? "0" : mDataset.get(position).fundRecieved);
        String totTrans = (recFund - (cashinHd + expesnse)) + "";


        binding.totalAmt.setText("" + totTrans);

        binding.extraData.setVisibility(View.VISIBLE);
        binding.btnAddClosure.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalClosure.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.fetchTrans.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);
        viewOrEdit = false;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + Constants.oneDigToTwo(monthOfYear + 1) + "-" + Constants.oneDigToTwo(dayOfMonth);
        if (whichDate == filterDate) {
            binding.dateClosureFilter.setText(date);

        } else if (whichDate == closureDate) {
            binding.dateClosure.setText(date);
            binding.fundRecieved.setText("");
            binding.expenses.setText("");
            binding.expensesDesc.setText("");
            binding.cashInHand.setText("");
            binding.totalAmt.setText("");
            binding.comments.setText("");

            if (!role) {
                gettingTrans();
            } else if (binding.btnAddClosure.getText().toString().equalsIgnoreCase("Add")) {
                gettingTrans();
            }

        }
    }

    @Override
    public void onGetDailyClosureSuccess(GetUserDailyClosureRes res) {
        loader.stop();
        binding.filterHolder.setVisibility(View.GONE);
        holderFilter = true;
    }

    @Override
    public void onAddDailyClousureSuccess(AddUserDailyClosureRes res) {
        if (res.success) {
            resetAll();
            Constants.Toasty(context, "Today's Closure Added successfully", Constants.success);
            viewModel.getDailyClosures(req);
        } else {
            Constants.alertDialogShow(context, res.response);
            binding.progressBarForTrans.setVisibility(View.GONE);
            loader.stop();
        }
    }

    //.
    @Override
    public void onUpdateDailyClousureSuccess(UpdateUserDailyClosureRes res) {
        if (res.success) {
            Constants.Toasty(context, "Today's Closure Added successfully", Constants.success);
            resetAll();
            viewModel.getDailyClosures(req);
        } else {
            Constants.alertDialogShow(context, res.response);
            binding.progressBarForTrans.setVisibility(View.GONE);
            loader.stop();
        }

    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        binding.progressBarForTrans.setVisibility(View.GONE);
        loader.stop();
        binding.btnAddClosure.setEnabled(true);
    }

    @Override
    public void onError(String message) {
        binding.progressBarForTrans.setVisibility(View.GONE);
        loader.stop();
        Constants.Toasty(context, message, Constants.error);
    }

    @Override
    public void onErrorComplete(String s) {
        binding.progressBarForTrans.setVisibility(View.GONE);
        loader.stop();
        Constants.Toasty(context, s, Constants.error);
    }

    @Override
    public void onPBShow(String s) {
        loader.start();
    }
}
