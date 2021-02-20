package com.a.goldtrack.dailyclosure;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityUserReportClosureBinding;
import com.a.goldtrack.utils.BaseActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.LoaderDecorator;
import com.a.goldtrack.utils.Sessions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDailyReportActivity extends BaseActivity implements View.OnClickListener, IDailyClosureView, RecycleItemClicked, DatePickerDialog.OnDateSetListener {

    UserDailyReportViewModel viewModel;
    ActivityUserReportClosureBinding binding;

    protected CustomDailyReportAdapter mAdapter;
    // ProgressDialog progressDialog;
    Context context;
    GetUserDailyClosureReq req;
    boolean viewOrEdit = true, role = false;
    int whichDate = 0, filterDate = 1, closureDate = 2;

    protected List<GetUserDailyClosureRes.DataList> mDataset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(UserDailyReportViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_report_closure);
        binding.setDReportModel(viewModel);
        context = UserDailyReportActivity.this;

        loader = new LoaderDecorator(context);
        init();
    }

    Map<String, String> branchesArr = null;
    boolean holderFilter = true;

    void init() {
        String str = Sessions.getUserString(context, Constants.roles);
        role = str.equals("ADMIN") || str.equals("SUPER_ADMIN");

        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        // binding.addDetailsHolder.setVisibility(View.GONE);
        //  binding.progressBarForTrans.setVisibility(View.GONE);
        viewModel.onViewAvailable(this);

        // getSupportActionBar().setDisplayShowTitleEnabled(true);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Reports");
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

        binding.imgDateClickFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDatePicker();
                whichDate = filterDate;
            }
        });
        /*viewModel.totalAmt.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double totTrans) {
                binding.totalAmt.setText("" + totTrans);
            }
        });*/

        if (role) {
            binding.selectEmployeeFilter.setVisibility(View.VISIBLE);
        } else {
            binding.selectEmployeeFilter.setVisibility(View.GONE);
        }
        binding.filterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dateClosureFilter.setText("");
                binding.selectBranchFilter.setSelection(0);
                binding.selectEmployeeFilter.setSelection(0);
                req.branchID = "0";
                req.userID = "0";
                req.date = Constants.getDateNowyyyymmmdd();
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
                } else {
                    strBrnc = branchesArr.get(strBrnc);
                }

                //GetUserDailyClosureReq req = new GetUserDailyClosureReq();
                // req.companyID = Sessions.getUserString(context, Constants.companyId);
                req.branchID = strBrnc == null ? "0" : strBrnc;
                req.date = dateFilr;
                if (role) {
                    //req.userID = "0";
                    String struser = binding.selectEmployeeFilter.getSelectedItem().toString();
                    if (struser.equals("Select")) {
                        struser = "0";
                    }
                    req.userID = struser == null ? "0" : Constants.usersArr.get(struser);
                } else {
                    req.userID = Sessions.getUserString(context, Constants.userId);
                }


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
        // binding.addSignalClosure.setOnClickListener(this);
        //  binding.btnAddClosure.setOnClickListener(this);
        //  binding.fetchTrans.setOnClickListener(this);

       /* binding.filterReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holderFilter) {
                    binding.filterHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.filterHolder.setVisibility(View.GONE);
                }
                holderFilter = !holderFilter;
            }
        });*/

        req = new GetUserDailyClosureReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.branchID = "0";//Sessions.getUserString(context, Constants.companyId);
        req.date = Constants.getDateNowyyyymmmdd();
        req.userID = role ? "0" : Sessions.getUserString(context, Constants.userId);


        viewModel.getDailyClosures(req);
        if (Constants.usersArr != null)
            Constants.setSpinners(binding.selectEmployeeFilter, Constants.usersArr.keySet().toArray(new String[0]));
        if (branchesArr != null)
            Constants.setSpinners(binding.selectBranchFilter, branchesArr.keySet().toArray(new String[0]));

    }

    private void callDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                UserDailyReportActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setMaxDate(now);
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// API 5+ solution
                finish();//  onBackPressed();
                break;
            case R.id.action_filter:
                if (holderFilter) {
                    binding.filterHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.filterHolder.setVisibility(View.GONE);
                }
                holderFilter = !holderFilter;
                break;
        }
        return true;
    }

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(context, mCurrentLayoutManagerType, binding.recyclerDailyClosures);
        if (mAdapter == null) {
            mAdapter = new CustomDailyReportAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerDailyClosures.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);

        if (mDataset.size() == 0) {
            binding.nodataImg.setVisibility(View.VISIBLE);
            binding.recyclerDailyClosures.setVisibility(View.GONE);
        } else {
            binding.nodataImg.setVisibility(View.GONE);
            binding.recyclerDailyClosures.setVisibility(View.VISIBLE);
        }

        double fundRecieved = 0.0, cashInHand = 0.0, expenses = 0.0, clBal = 0.0;
        for (int i = 0; i < mDataset.size(); i++) {
            String[] strAmts = Constants.getTotalGross(mDataset.get(i).transactionsForday).split("_");
            fundRecieved += Double.parseDouble(mDataset.get(i).fundRecieved);
            expenses += Double.parseDouble(mDataset.get(i).expenses);
            cashInHand += Double.parseDouble(mDataset.get(i).cashInHand);

            clBal += Double.parseDouble(mDataset.get(i).fundRecieved) -
                    (Double.parseDouble(mDataset.get(i).expenses) + Double.parseDouble(strAmts[4]));

        }
        binding.runningAdminPaid.setText("Fund Received \nfrom Admin:\n" + Constants.priceToString(Constants.getFormattedNumber(fundRecieved)));
        binding.runningExpense.setText("Expenses:\n" + Constants.priceToString(Constants.getFormattedNumber(expenses)));
        binding.runningBalance.setText("Cash in Hand:\n" + Constants.priceToString(Constants.getFormattedNumber(cashInHand)));
        binding.runningBalance.setText("Cl Bal:\n" + Constants.priceToString(Constants.getFormattedNumber(clBal)));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fetchTrans:
                break;
        }
    }


    @Override
    public void oncItemClicked(View view, int position) {
        // Constants.Toasty(context, "Edit " + mDataset.get(position).userName, Constants.info);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + Constants.oneDigToTwo(monthOfYear + 1) + "-" + Constants.oneDigToTwo(dayOfMonth);
        if (whichDate == filterDate) {
            binding.dateClosureFilter.setText(date);
        }
    }

    @Override
    public void onGetDailyClosureSuccess(GetUserDailyClosureRes res) {
        loader.stop();
        binding.filterHolder.setVisibility(View.GONE);
        holderFilter = true;
        binding.runningDate.setText("Date: YYYY-MM-DD\n" + req.date);
    }

    @Override
    public void onAddDailyClousureSuccess(AddUserDailyClosureRes res) {
    }

    //.
    @Override
    public void onUpdateDailyClousureSuccess(UpdateUserDailyClosureRes res) {
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        loader.stop();
    }

    @Override
    public void onError(String message) {
        loader.stop();
        Constants.Toasty(context, message, Constants.error);
    }

    @Override
    public void onErrorComplete(String s) {
        loader.stop();
        Constants.Toasty(context, s, Constants.error);
    }

    @Override
    public void onPBShow(String s) {
        loader.start();
    }
}
