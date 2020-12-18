package com.a.goldtrack.ui.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.DashboardFragBinding;
import com.a.goldtrack.ui.home.IHomeUiView;
import com.a.goldtrack.utils.AssessmentActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.LoaderDecorator;
import com.a.goldtrack.utils.Sessions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class DashBrdFragment extends Fragment implements DatePickerDialog.OnDateSetListener, IHomeUiView,IDailyClosureDashCallBacks {

    private DashBrdViewModel viewModel;
    private DashboardFragBinding binding;
    private String selectedDate = "";
    private Context context;
    private LoaderDecorator loader;
    private GetCompany reqDrop;
    private boolean role;
    private GetUserDailyClosureReq reqClsour;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        loader = new LoaderDecorator(context);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(GTrackApplication.getInstance()).create(DashBrdViewModel.class);

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("refresh-from-home"));

        binding = DataBindingUtil.inflate(inflater, R.layout.dashboard_frag, container, false);
        binding.setDashModel(viewModel);
        binding.currentDate.setText(Constants.getDateMMM(""));
        viewModel.onViewAvailable(this,this);

        String str = Sessions.getUserString(context, Constants.roles);
        role = str.equals("ADMIN") || str.equals("SUPER_ADMIN");

        viewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                binding.textView.setText(s);
            }
        });


        binding.cardTransGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AssessmentActivity.class);
                i.putExtra("title", "Transactions");
                i.putExtra("data", "trans");
                i.putExtra("date", selectedDate);
                startActivity(i);
            }
        });

        binding.dateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DashBrdFragment.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });

        callFirstLaodData();

        return binding.getRoot();
    }

    private void callFirstLaodData(){
        loader.start();
        GetUserForCompany req1 = new GetUserForCompany();
        req1.companyId = Sessions.getUserString(context, Constants.companyId);
        req1.userId = "0";
        viewModel.getUsers(req1);

        reqDrop = new GetCompany();
        reqDrop.companyId = Sessions.getUserString(context, Constants.companyId);
        viewModel.getDropdown(reqDrop);


        reqClsour = new GetUserDailyClosureReq();
        reqClsour.companyID = Sessions.getUserString(context, Constants.companyId);
        reqClsour.date = Constants.getDateNowyyyymmmdd();
        if (role) {
            reqClsour.userID = "0";
        } else {
            reqClsour.userID = Sessions.getUserString(context, Constants.userId);
        }
        selectedDate = reqClsour.date;
        viewModel.getDailyClosures(reqClsour);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + Constants.oneDigToTwo(monthOfYear + 1) + "-" + Constants.oneDigToTwo(dayOfMonth);
        selectedDate = date;

        binding.currentDate.setText(Constants.getDateMMM(date));

        loader.start();
        reqClsour.date = selectedDate;
        viewModel.getDailyClosures(reqClsour);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction() != null && intent.getAction().equals("refresh-from-home")) {
                // loader.start();
                //  viewModel.getTransactions(req);
                //  reqDrop = new GetCompany();
                //  reqDrop.companyId = Sessions.getUserString(context, Constants.companyId);
                //  viewModel.getDropdown(reqDrop);



                callFirstLaodData();
                binding.currentDate.setText(Constants.getDateMMM(reqClsour.date));
            }
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

        }
    };

    @Override
    public void onDestroyView() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        super.onDestroyView();

    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        loader.stop();
    }

    @Override
    public void onGetDrpSuccess(DropdownDataForCompanyRes dropdownRes) {
        if (dropdownRes.success) {
            Sessions.setUserObj(context, dropdownRes, Constants.dorpDownSession);
            Log.d("TAG", "Saving Dropdown Data");
            if (dropdownRes != null) {
                Constants.branchesArr = new LinkedHashMap<String, String>();
                Constants.branchesArr.put("Select", "Select");
                /* Branches */
                try {
                    for (int i = 0; i < dropdownRes.branchesList.size(); i++) {
                        Constants.branchesArr.put(dropdownRes.branchesList.get(i).branchName.toUpperCase()
                                + "-" + dropdownRes.branchesList.get(i).id, dropdownRes.branchesList.get(i).id);
                    }
                   // setSpinners(binding.selectBranchFilter, Constants.branchesArr.keySet().toArray(new String[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Constants.Toasty(context, "Please refresh home screen to load details", Constants.error);
            }
        } else {
            Constants.Toasty(context, dropdownRes.response + ", Please refresh again.", 1);
        }

    }

    @Override
    public void getUsersSuccess(GetUserForCompanyRes res) {
        loader.stop();
        if (res.success) {
            Log.d("TAG", "GetUserForCompanyRes Data");
            if (res != null) {
                Constants.usersArr = new LinkedHashMap<String, String>();
                Constants.usersArr.put("Select", "Select");
                /* users */
                try {
                    for (int i = 0; i < res.resList.size(); i++) {
                        Constants.usersArr.put(res.resList.get(i).firstName.toUpperCase()
                                + "-" + res.resList.get(i).lastName, res.resList.get(i).id);
                    }
                   // setSpinners(binding.selectEmployeeFilter, Constants.usersArr.keySet().toArray(new String[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Constants.Toasty(context, "Please refresh home screen to load details", Constants.error);
            }
        } else {
            Constants.Toasty(context, res.response + ", Please refresh again.", 1);
        }
    }

    @Override
    public void onGetDailyClosureSuccess(GetUserDailyClosureRes res) {
        // call it
        loader.stop();
        double totAmt = 0.0, cashInHand = 0.0, expenses = 0.0;int TotTrans = 0;
        for (int i = 0; i < res.dataList.size(); i++) {
            // fundReceived += Double.parseDouble(res.dataList.get(i).fundRecieved);
            expenses += Double.parseDouble(res.dataList.get(i).expenses);
            cashInHand += Double.parseDouble(res.dataList.get(i).cashInHand);
            if (res.dataList.get(i).transactionsForday != null) {
                TotTrans += res.dataList.get(i).transactionsForday.size();
                String[] strAmts = Constants.getTotalGross(res.dataList.get(i).transactionsForday).split("_");
                totAmt += Double.parseDouble(strAmts[3]);//ClBal
            }
        }

        binding.totalTrans.setText(TotTrans + "");
        binding.totExpenses.setText(Constants.getFormattedNumber(expenses));
        binding.totAmt.setText(Constants.getFormattedNumber(totAmt));
    }

    @Override
    public void onError(String message) {
        loader.stop();
        Constants.Toasty(context, message, Constants.info);

    }

    @Override
    public void onErrorComplete(String s) {
        loader.stop();
        Constants.Toasty(context, s, Constants.info);
    }
}