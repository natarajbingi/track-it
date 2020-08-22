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
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.DashboardFragBinding;
import com.a.goldtrack.utils.AssessmentActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class DashBrdFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private DashBrdViewModel viewModel;
    private DashboardFragBinding binding;
    private String selectedDate = "";
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(GTrackApplication.getInstance()).create(DashBrdViewModel.class);

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("refresh-from-trans"));

        binding = DataBindingUtil.inflate(inflater, R.layout.dashboard_frag, container, false);
        binding.setDashModel(viewModel);
        binding.currentDate.setText(Constants.getDateMMM(""));

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

        return binding.getRoot();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + Constants.oneDigToTwo(monthOfYear + 1) + "-" + Constants.oneDigToTwo(dayOfMonth);
        selectedDate = date;

        binding.currentDate.setText(Constants.getDateMMM(date));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction() != null && intent.getAction().equals("refresh-from-trans")) {
                // loader.start();
                //  viewModel.getTransactions(req);
                //  reqDrop = new GetCompany();
                //  reqDrop.companyId = Sessions.getUserString(context, Constants.companyId);
                //  viewModel.getDropdown(reqDrop);
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
}