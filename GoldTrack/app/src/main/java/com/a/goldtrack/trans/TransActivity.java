package com.a.goldtrack.trans;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPReq;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.R;
import com.a.goldtrack.customer.CustomCustomersAdapter;
import com.a.goldtrack.databinding.ActivityTransBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import es.dmoral.toasty.Toasty;

public class TransActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, ITransUiHandler {

    TransViewModel viewModel;
    ActivityTransBinding binding;
    ProgressDialog progressDialog;
    Context context;
    CountDownTimer timer;
    private int current = 1, first = 1, second = 2, third = 3;
    List<ItemsTrans> list;
    private static final String TAG = "TransActivity";
    protected CustomTransAddedItemAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    boolean showingItemAdd = false;
    int counter = 0;
    String otp = "";
    DropdownDataForCompanyRes dropdownRes;

    Map<String, String> branchesArr = null;
    Map<String, String> itemsArr = null;
    Map<String, String> customersArr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(TransViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trans);
        binding.setTransModel(viewModel);
        context = TransActivity.this;

        list = new ArrayList<>();
        init();
    }

    private void init() {

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");

        binding.numbver.setText("Verify +91 9980766166");
        binding.stepNext.setVisibility(View.VISIBLE);
        binding.bottomTotalLayout.setVisibility(View.GONE);

        setCurrentLayoutVisible();
        binding.stepNext.setOnClickListener(this);
        binding.addItemTrans.setOnClickListener(this);
        binding.itemAddTransLayoutParent.addItem.setOnClickListener(this);
        binding.itemAddTransLayoutParent.itemAddingLocalCalci.setOnClickListener(this);
        binding.itemAddTransLayoutParent.cancel.setOnClickListener(this);
        binding.stepLastSubmit.setOnClickListener(this);
        viewModel.onViewAvailable(this);
        GetCompany req = new GetCompany();
        req.companyId = Sessions.getUserString(context, Constants.companyId);
        progressDialog.show();
        viewModel.getDropdowns(req);

        viewModel.dropdownList.observe(this, new Observer<DropdownDataForCompanyRes>() {
            @Override
            public void onChanged(DropdownDataForCompanyRes dropdownDataForCompanyRes) {
                dropdownRes = dropdownDataForCompanyRes;
                setDropDowns();
            }
        });
        binding.selectCommodity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = binding.selectCommodity.getSelectedItem().toString().trim();
                if (!str.equals("Select")) {
                    setItemsArr(str);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setDropDowns() {
        Map<String, String> ReturnArray = new LinkedHashMap<String, String>();
        ReturnArray.put("Select", "Select");

        /* Branches
         * */
        try {
            for (int i = 0; i < dropdownRes.branchesList.size(); i++) {
                ReturnArray.put(dropdownRes.branchesList.get(i).branchName.toUpperCase() + "-" + dropdownRes.branchesList.get(i).id, dropdownRes.branchesList.get(i).id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        branchesArr = ReturnArray;
        setSpinners(binding.selectBranch, branchesArr.keySet().toArray(new String[0]));

        /* Customer
         * */
        ReturnArray.clear();
        ReturnArray.put("Select", "Select");
        try {
            for (int i = 0; i < dropdownRes.customerList.size(); i++) {
                ReturnArray.put(dropdownRes.customerList.get(i).firstName.toUpperCase()
                        + " " + dropdownRes.customerList.get(i).lastName.toUpperCase()
                        + "-" + dropdownRes.customerList.get(i).mobileNum, dropdownRes.customerList.get(i).id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        customersArr = ReturnArray;
        setAutoComplete(binding.autoCompleteSelectCustomer, customersArr.keySet().toArray(new String[0]));

    }

    public void setItemsArr(String commodity) {
        Log.d("commoditySelected", commodity);
        Map<String, String> ReturnArray = new LinkedHashMap<String, String>();
        /* Items
         * */
        ReturnArray.put("Select", "Select");
        try {
            for (int i = 0; i < dropdownRes.itemsList.size(); i++) {
                if (dropdownRes.itemsList.get(i).commodity.equals(commodity))
                    ReturnArray.put(dropdownRes.itemsList.get(i).itemName.toUpperCase() + "-" + dropdownRes.itemsList.get(i).id, dropdownRes.itemsList.get(i).id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemsArr = ReturnArray;
        setSpinners(binding.itemAddTransLayoutParent.selectItem, itemsArr.keySet().toArray(new String[0]));
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

    private void setAutoComplete(AutoCompleteTextView auto, String[] array) {
        // -----------------------------------------------
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item,
                array);
        //Getting the instance of AutoCompleteTextView
        // binding.autoCompleteSelectCustomer.setThreshold(1);//will start working from first character
        auto.setThreshold(1);//will start working from first character
        auto.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        auto.setTextColor(Color.RED);
    }

    @Override
    public void onBackPressed() {
        if (current != 1) {
            askBeforeExit();
        } else
            finish();//  onBackPressed();
        // overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.step_next:
                if (current == 1) {
                    CustomerWithOTPReq req = firstLayoutValidate();
                    if (req != null) {
                        progressDialog.show();
                        viewModel.verifyOtp(req);
                    }
                } else if (current == 2) {
                    String otpeditStr = binding.otpedit.getText().toString();
                    if (otpeditStr.isEmpty()) {
                        Constants.Toasty(context, "Please enter OTP to proceed", Constants.error);
                    } else {
                        if (otp.equals(otpeditStr)) {
                            current = 3;
                            timer.cancel();
                            setCurrentLayoutVisible();
                            binding.selectedCustomerName.setText("Customer: " + binding.autoCompleteSelectCustomer.getText().toString().split("-")[0]);
                            binding.transitionCurrentDate.setText("Date: " + Constants.getDateNowyyyymmmdd());
                            binding.selectedTextCommodity.setText("Commodity: " + binding.selectCommodity.getSelectedItem().toString());
                            binding.selectedTextCommodityPrice.setText("Price: " + binding.commodityRate.getText().toString());
                            binding.selectedBranch.setText("Branch: " + binding.selectBranch.getSelectedItem().toString());

                            binding.itemAddTransLayoutParent.selectedCommodity.setText(
                                    "Commodity: " + binding.selectCommodity.getSelectedItem().toString());
                            binding.itemAddTransLayoutParent.selectedCommodityAmount.setText(
                                    binding.commodityRate.getText().toString());
                        } else {
                            Constants.Toasty(context, "Please enter Valid OTP, try again.", Constants.error);
                        }
                    }
                }
                break;
            case R.id.add_item_trans:
                // Constants.Toasty(context, "inProgress", Constants.info);
                if (!showingItemAdd) {
                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.VISIBLE);
                    binding.recyclerTransItems.setVisibility(View.GONE);
                    binding.bottomTotalLayout.setVisibility(View.GONE);
                } else {
                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                    binding.recyclerTransItems.setVisibility(View.VISIBLE);
                    binding.bottomTotalLayout.setVisibility(View.VISIBLE);
                }
                showingItemAdd = !showingItemAdd;
                break;
            case R.id.cancel:
                binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                binding.recyclerTransItems.setVisibility(View.VISIBLE);
                binding.bottomTotalLayout.setVisibility(View.VISIBLE);

                showingItemAdd = !showingItemAdd;
                break;
            case R.id.item_adding_local_calci:
                if (binding.itemAddTransLayoutParent.selectedCommodityAmount.getText().toString().isEmpty() ||
                        binding.itemAddTransLayoutParent.commodityWeight.getText().toString().isEmpty() ||
                        binding.itemAddTransLayoutParent.stoneWastage.getText().toString().isEmpty()) {
                    Constants.Toasty(context, "Please enter Mandatory fields", Constants.warning);
                    break;
                }
                float amount = Float.parseFloat(binding.itemAddTransLayoutParent.selectedCommodityAmount.getText().toString());
                float commodityWeight = Float.parseFloat(binding.itemAddTransLayoutParent.commodityWeight.getText().toString());
                float stoneWastage = Float.parseFloat(binding.itemAddTransLayoutParent.stoneWastage.getText().toString());
                float otherWastage = Float.parseFloat(binding.itemAddTransLayoutParent.otherWastage.getText().toString());

                float netWeight = (commodityWeight - (stoneWastage + otherWastage));
                float netWeightAmount = amount * netWeight;
                binding.itemAddTransLayoutParent.nettWeight.setText(netWeight + "");
                binding.itemAddTransLayoutParent.calculatedItemAmount.setText("Rs. " + netWeightAmount);


                break;
            case R.id.resend:
                otp = Constants.getRandomNumberString();
                CustomerWithOTPReq req = firstLayoutValidate();
                if (req != null) {
                    progressDialog.show();
                    viewModel.verifyOtp(req);
                }

                break;
            case R.id.add_item: {
                if (binding.itemAddTransLayoutParent.nettWeight.getText().toString().isEmpty() || binding.itemAddTransLayoutParent.nettWeight.getText().toString().equals("0.00")) {
                    Constants.Toasty(context, "Please check calculation before add ITEM", Constants.warning);
                    break;
                }
                float amount1 = Float.parseFloat(binding.itemAddTransLayoutParent.selectedCommodityAmount.getText().toString());
                float commodityWeight1 = Float.parseFloat(binding.itemAddTransLayoutParent.commodityWeight.getText().toString());
                float stoneWastage1 = Float.parseFloat(binding.itemAddTransLayoutParent.stoneWastage.getText().toString());
                float otherWastage1 = Float.parseFloat(binding.itemAddTransLayoutParent.otherWastage.getText().toString());

                float netWeight1 = (commodityWeight1 - (stoneWastage1 + otherWastage1));
                float netWeightAmount1 = amount1 * netWeight1;

                ItemsTrans nn = new ItemsTrans();
                nn.amount = netWeightAmount1 + "";
                nn.commodityWeight = commodityWeight1 + "";
                nn.stoneWastage = stoneWastage1 + "";
                nn.otherWastage = otherWastage1 + "";
                nn.nettWeight = binding.itemAddTransLayoutParent.nettWeight.getText().toString();
                nn.purity = binding.itemAddTransLayoutParent.purity.getText().toString();
                nn.commodity = binding.itemAddTransLayoutParent.selectItem.getSelectedItem().toString();
                nn.itemID = itemsArr.get(binding.itemAddTransLayoutParent.selectItem.getSelectedItem().toString());


                if (nn.amount.isEmpty() || nn.commodityWeight.isEmpty()
                        || nn.stoneWastage.isEmpty() || nn.otherWastage.isEmpty()
                        || nn.nettWeight.isEmpty() || nn.purity.isEmpty()
                        || nn.commodity.equals("Select")
                ) {
                    Constants.Toasty(context, "Please enter mandatory fields.", Constants.warning);
                    break;
                } else {
                    list.add(nn);
                    setmRecyclerView();
                    //viewModel.list.postValue(list);

                    //  binding.itemAddTransLayoutParent.selectedCommodityAmount.setText("");
                    binding.itemAddTransLayoutParent.commodityWeight.setText("");
                    binding.itemAddTransLayoutParent.stoneWastage.setText("");
                    binding.itemAddTransLayoutParent.otherWastage.setText("");
                    binding.itemAddTransLayoutParent.nettWeight.setText("0.00");
                    binding.itemAddTransLayoutParent.purity.setText("");
                    binding.itemAddTransLayoutParent.calculatedItemAmount.setText("0.00");
                    binding.itemAddTransLayoutParent.selectItem.setSelection(0);

                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                    binding.recyclerTransItems.setVisibility(View.VISIBLE);
                    binding.bottomTotalLayout.setVisibility(View.VISIBLE);

                }
            }
            break;
            case R.id.step_last_submit:
                Constants.Toasty(context, "Now we talking last stage", Constants.info);
                break;
        }
    }

    void setCurrentLayoutVisible() {
        switch (current) {
            case 1:
                binding.firstStepLayout.setVisibility(View.VISIBLE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.GONE);
                otp = Constants.getRandomNumberString();
                break;
            case 2:
                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.VISIBLE);
                binding.thirdStepDetails.setVisibility(View.GONE);
                break;
            case 3:
                binding.stepNext.setVisibility(View.GONE);

                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.VISIBLE);

                binding.bottomTotalLayout.setVisibility(View.VISIBLE);
                binding.recyclerTransItems.setVisibility(View.VISIBLE);
                //setmRecyclerView();
                break;
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
            if (current != 1) {
                askBeforeExit();
            } else
                finish();//  onBackPressed();
        }
        return true;
    }

    void startCounter() {
        timer = new CountDownTimer(500000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.timer.setText("Resend Code in " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                binding.resend.setEnabled(true);
                binding.resend.setVisibility(View.VISIBLE);
                binding.timer.setText("Tap on resend");
            }
        }.start();
    }

    void askBeforeExit() {
        new AlertDialog.Builder(TransActivity.this, R.style.AppTheme_Dark_Dialog)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to cancel the registration process?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TransActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
    }


    CustomerWithOTPReq firstLayoutValidate() {
        CustomerWithOTPReq req = new CustomerWithOTPReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.userID = Sessions.getUserString(context, Constants.userId);
        req.customerID = customersArr.get(binding.autoCompleteSelectCustomer.getText().toString());
        req.customerMob = binding.autoCompleteSelectCustomer.getText().toString().split("-")[1];
        req.counter = (counter + 1) + "";
        req.otp = otp;

        if (binding.selectBranch.getSelectedItem().toString().equals("Select")
                || binding.selectCommodity.getSelectedItem().toString().equals("Select")
                || req.customerMob.equals("Select")
                || req.customerID.equals("Select")
                || binding.commodityRate.getText().toString().isEmpty()
        ) {

            return null;
        }
        return req;
    }

    private void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomTransAddedItemAdapter(list);
            mAdapter.setClickListener(this);
            binding.recyclerTransItems.setAdapter(mAdapter);
        } else {
            mAdapter.updateListNew(list);
        }
        if (list.size() > 0) {
            double grandTotalAmt = 0.0;
            for (int i = 0; i < list.size(); i++) {
                grandTotalAmt += Double.parseDouble(list.get(i).amount);
            }
            binding.grandTotalAmtBottom.setText("Total: Rs. " + grandTotalAmt);
        } else {
            binding.grandTotalAmtBottom.setText("Total: Rs. 0.00");
        }

    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerTransItems.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerTransItems.getLayoutManager())
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

        binding.recyclerTransItems.setLayoutManager(mLayoutManager);
        binding.recyclerTransItems.scrollToPosition(scrollPosition);
    }

    @Override
    public void oncItemClicked(View view, int position) {

    }

    @Override
    public void onUiVerifyOtpSuccess(CustomerWithOTPRes body) {
        progressDialog.dismiss();
        current = 2;
        startCounter();
        setCurrentLayoutVisible();
    }

    @Override
    public void onAddTransSuccess(AddTransactionRes res) {
        progressDialog.dismiss();
        Constants.Toasty(context, res.response + "" + res.transactionID, Constants.success);
    }

    @Override
    public void onGetDropDownsSuccess(DropdownDataForCompanyRes res) {
        progressDialog.dismiss();

    }

    @Override
    public void onError(String msg) {
        progressDialog.dismiss();
        Constants.Toasty(context, msg, Constants.warning);
    }

    @Override
    public void onErrorComplete(String msg) {
        progressDialog.dismiss();
        Constants.Toasty(context, msg, Constants.warning);
    }
}
