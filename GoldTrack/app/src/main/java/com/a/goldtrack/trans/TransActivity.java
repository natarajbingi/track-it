package com.a.goldtrack.trans;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
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
import android.widget.Spinner;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddTransactionReq;
import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.CustomerWithOTPReq;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityTransBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, ITransUiHandler {

    TransViewModel viewModel;
    ActivityTransBinding binding;
    ProgressDialog progressDialog;
    Context context;
    CountDownTimer timer;
    private final int first = 1, second = 2, third = 3, four = 4;
    private int current = 1;
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
        binding.finalLayoutParent.finalLayoutChild.setVisibility(View.VISIBLE);

        setCurrentLayoutVisible();
        binding.stepNext.setOnClickListener(this);
        binding.addItemTrans.setOnClickListener(this);
        binding.itemAddTransLayoutParent.addItem.setOnClickListener(this);
        binding.itemAddTransLayoutParent.itemAddingLocalCalci.setOnClickListener(this);
        binding.itemAddTransLayoutParent.cancel.setOnClickListener(this);
        binding.stepLastSubmit.setText("Proceed");
        binding.stepLastSubmit.setOnClickListener(this);
        viewModel.onViewAvailable(this);
        GetCompany req = new GetCompany();
        req.companyId = Sessions.getUserString(context, Constants.companyId);
        if (Constants.isConnection()) {
            progressDialog.show();
            viewModel.getDropdowns(req);
        } else {
            viewModel.dropdownList = new MutableLiveData<>();
            Gson gj = new Gson();
            DropdownDataForCompanyRes res = gj.fromJson(Constants.listme, DropdownDataForCompanyRes.class);
            viewModel.dropdownList.postValue(res);
        }

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
        branchesArr = new LinkedHashMap<String, String>();
        customersArr = new LinkedHashMap<String, String>();
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

        /* Customer  */
        customersArr.put("Select", "Select");
        try {
            for (int i = 0; i < dropdownRes.customerList.size(); i++) {
                customersArr.put(dropdownRes.customerList.get(i).firstName.toUpperCase()
                        + " " + dropdownRes.customerList.get(i).lastName.toUpperCase()
                        + "-" + dropdownRes.customerList.get(i).mobileNum, dropdownRes.customerList.get(i).id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSpinners(binding.selectBranch, branchesArr.keySet().toArray(new String[0]));
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
            case R.id.step_next: {
                if (current == 1) {
                    CustomerWithOTPReq req = firstLayoutValidate();
                    if (req != null) {
                        progressDialog.show();
                        viewModel.verifyOtp(req);
                    } else
                        Constants.Toasty(context, "Please select or Enter mandatory details.", Constants.warning);
                } else if (current == 2) {
                    String otpeditStr = binding.otpedit.getText().toString();
                    if (otpeditStr.isEmpty()) {
                        Constants.Toasty(context, "Please enter OTP to proceed", Constants.error);
                    } else {
                        if (otp.equals(otpeditStr)) {
                            current = third;
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


                            binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
                            binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.VISIBLE);
                            binding.recyclerTransItems.setVisibility(View.GONE);
                            binding.bottomTotalLayout.setVisibility(View.GONE);
                            showingItemAdd = !showingItemAdd;
                        } else {
                            Constants.Toasty(context, "Please enter Valid OTP, try again.", Constants.error);
                        }
                    }
                }
            }
            break;
            case R.id.add_item_trans: {
                // Constants.Toasty(context, "inProgress", Constants.info);
                if (!showingItemAdd) {
                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.VISIBLE);
                    binding.recyclerTransItems.setVisibility(View.GONE);
                    binding.bottomTotalLayout.setVisibility(View.GONE);
                    resetInnerAddItem(false);
                } else {
                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                    binding.recyclerTransItems.setVisibility(View.VISIBLE);
                    binding.bottomTotalLayout.setVisibility(View.VISIBLE);
                }
                showingItemAdd = !showingItemAdd;
            }
            break;
            case R.id.cancel: {
                binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                binding.recyclerTransItems.setVisibility(View.VISIBLE);
                binding.bottomTotalLayout.setVisibility(View.VISIBLE);

                showingItemAdd = !showingItemAdd;
            }
            break;
            case R.id.item_adding_local_calci: {
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
                float purity = Float.parseFloat(binding.itemAddTransLayoutParent.purity.getText().toString());
                float margin = Float.parseFloat(binding.itemAddTransLayoutParent.margin.getText().toString());

                float netWeight = (commodityWeight - (stoneWastage + otherWastage));
                float netWeight1WithPurity = (netWeight * purity) / 100;

                float netWeightAmount = amount * netWeight1WithPurity;

                float marginAmt = (netWeightAmount * margin) / 100;// 2 / 100
                float FinalAmt = netWeightAmount - marginAmt;

                // float netWeightAmount = amount * netWeight;
                binding.itemAddTransLayoutParent.nettWeight.setText("Net Weight: " + netWeight);
                //   binding.itemAddTransLayoutParent.calculatedItemAmount.setText("Rs. " + netWeightAmount);
                binding.itemAddTransLayoutParent.calculatedItemAmount.setText("Rs. " + FinalAmt);

            }
            break;
            case R.id.resend: {
                otp = Constants.getRandomNumberString();
                CustomerWithOTPReq req = firstLayoutValidate();
                if (req != null) {
                    progressDialog.show();
                    viewModel.verifyOtp(req);
                }
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
                float purity1 = Float.parseFloat(binding.itemAddTransLayoutParent.purity.getText().toString());
                float margin1 = Float.parseFloat(binding.itemAddTransLayoutParent.margin.getText().toString());


                float netWeight1 = (commodityWeight1 - (stoneWastage1 + otherWastage1));
                float netWeight1WithPurity = (netWeight1 * purity1) / 100;

                float netWeightAmount = amount1 * netWeight1WithPurity;

                float marginAmt = (netWeightAmount * margin1) / 100;// 2 / 100
                float FinalAmt = netWeightAmount - marginAmt;


                ItemsTrans nn = new ItemsTrans();
                nn.itemID = itemsArr.get(binding.itemAddTransLayoutParent.selectItem.getSelectedItem().toString());
                nn.amount = FinalAmt + "";
                nn.commodityWeight = commodityWeight1 + "";
                nn.stoneWastage = stoneWastage1 + "";
                nn.otherWastage = otherWastage1 + "";
                nn.nettWeight = binding.itemAddTransLayoutParent.nettWeight.getText().toString().split(":")[1];
                nn.purity = binding.itemAddTransLayoutParent.purity.getText().toString();
                nn.commodity = binding.itemAddTransLayoutParent.selectItem.getSelectedItem().toString();
                nn.margin = binding.itemAddTransLayoutParent.margin.getText().toString();


                if (nn.amount.isEmpty() || nn.commodityWeight.isEmpty()
                        || nn.stoneWastage.isEmpty() || nn.otherWastage.isEmpty() || nn.margin.isEmpty()
                        || nn.nettWeight.isEmpty() || nn.purity.isEmpty()
                        || nn.commodity.equals("Select")
                ) {
                    Constants.Toasty(context, "Please enter mandatory fields.", Constants.warning);
                    break;
                } else {
                    if (!binding.itemAddTransLayoutParent.addItem.getText().toString().equals("UPDATE")) {
                        list.add(nn);
                    } else {
                        list.remove(position);
                        list.add(position, nn);
                    }

                    setmRecyclerView();

                    //  binding.itemAddTransLayoutParent.selectedCommodityAmount.setText("");
                    resetInnerAddItem(false);
                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                    binding.recyclerTransItems.setVisibility(View.VISIBLE);
                    binding.bottomTotalLayout.setVisibility(View.VISIBLE);

                }
            }
            break;
            case R.id.step_last_submit: {
                String str = binding.stepLastSubmit.getText().toString();
                try {
                    if (addTransactionReq.itemList != null && addTransactionReq.itemList.size() > 0) {
                        if (str.equals("Proceed")) {
                            current = four;
                            setCurrentLayoutVisible();
                            settingFinalPageVals();
                            binding.stepLastSubmit.setText("SUBMIT");

                        } else {
                            addTransactionReq.paidAmountForRelease = binding.finalLayoutParent.paidAmountForRelease.getText().toString();
                            addTransactionReq.roundOffAmount = binding.finalLayoutParent.roundOffAmount.getText().toString();
                            addTransactionReq.comments = binding.finalLayoutParent.comments.getText().toString();

                            if (addTransactionReq.paidAmountForRelease.isEmpty() || addTransactionReq.roundOffAmount.isEmpty() || addTransactionReq.comments.isEmpty()) {
                                Constants.Toasty(context, "Please enter Mandatory details to submit.", Constants.warning);
                                break;
                            }
                            if (Constants.isConnection()) {
                                progressDialog.show();
                                viewModel.addTransreq(addTransactionReq);
                            } else
                                Constants.Toasty(context, "Please check network connection.", Constants.info);
                        }
                    } else
                        Constants.Toasty(context, "No Items Found to do transaction.", Constants.warning);
                } catch (Exception e) {
                    Constants.Toasty(context, "No Items Found to do transaction.", Constants.warning);
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
            break;
        }
    }

    void settingFinalPageVals() {
        binding.finalLayoutParent.customer.setText(binding.selectedCustomerName.getText().toString());
        binding.selectedTextCommodityPrice.setText("Price: " + binding.commodityRate.getText().toString());
        binding.selectedBranch.setText("Branch: " + binding.selectBranch.getSelectedItem().toString());

        binding.finalLayoutParent.commodity.setText(binding.selectedTextCommodity.getText().toString());
        binding.finalLayoutParent.nbfcReferenceNo.setText("Ref No: " + addTransactionReq.nbfcReferenceNo);
        binding.finalLayoutParent.totalCommodityWeight.setText("Cmd Weight:\n" + addTransactionReq.totalCommodityWeight);
        binding.finalLayoutParent.totalStoneWastage.setText("Stone Wst:\n" + addTransactionReq.totalStoneWastage);
        binding.finalLayoutParent.totalOtherWastage.setText("Other Wst:\n" + addTransactionReq.totalOtherWastage);
        binding.finalLayoutParent.totalNettWeight.setText("Total NetWeight: " + addTransactionReq.totalNettWeight);
        binding.finalLayoutParent.totalAmount.setText("Total Amount: " + addTransactionReq.totalAmount);
        binding.finalLayoutParent.grossAmount.setText("Gross Amount: " + addTransactionReq.grossAmount);
        binding.finalLayoutParent.marginAmount.setText("Margin Amount: " + addTransactionReq.marginAmount);
        binding.finalLayoutParent.nettAmount.setText("Net Amount: " + addTransactionReq.nettAmount);
        binding.finalLayoutParent.marginPercent.setText("Margin Percent: " + addTransactionReq.marginPercent);

        binding.finalLayoutParent.paidAmountForRelease.setText(addTransactionReq.paidAmountForRelease);
        binding.finalLayoutParent.roundOffAmount.setText(addTransactionReq.roundOffAmount);
        binding.finalLayoutParent.comments.setText(addTransactionReq.comments);

        String itemsDataRepeatStr = "ITEMS: \n";

        for (int i = 0; i < list.size(); i++) {
            itemsDataRepeatStr += (i + 1) + ") " + list.get(i).commodity + "\t\t" + list.get(i).commodityWeight + "Grms\t\t Rs. " + list.get(i).amount + "\n\n";
        }
        binding.finalLayoutParent.itemsDataRepeat.setText(itemsDataRepeatStr);

    }

    private void resetInnerAddItem(boolean finalReset) {
        if (finalReset) {
            list.clear();

            current = first;
            binding.otpedit.setText("");
            binding.selectCommodity.setSelection(0);
            binding.selectBranch.setSelection(0);
            binding.autoCompleteSelectCustomer.setText("");
            binding.commodityRate.setText("");
            binding.numbver.setText("");
            binding.timer.setText("");
            binding.resend.setVisibility(View.GONE);
            binding.selectedCustomerName.setText("");
            binding.transitionCurrentDate.setText("");
            binding.selectedBranch.setText("");
            binding.selectedTextCommodity.setText("");
            setmRecyclerView();
            binding.selectedTextCommodityPrice.setText("");
            binding.grandTotalAmtBottom.setText("Rs. 0.00");
            binding.stepLastSubmit.setText("Proceed");


            binding.finalLayoutParent.customer.setText("");
            binding.selectedTextCommodityPrice.setText("");
            binding.selectedBranch.setText("");

            binding.finalLayoutParent.commodity.setText("");
            binding.finalLayoutParent.nbfcReferenceNo.setText("");
            binding.finalLayoutParent.totalCommodityWeight.setText("");
            binding.finalLayoutParent.totalStoneWastage.setText("");
            binding.finalLayoutParent.totalOtherWastage.setText("");
            binding.finalLayoutParent.totalNettWeight.setText("");
            binding.finalLayoutParent.totalAmount.setText("");
            binding.finalLayoutParent.grossAmount.setText("");
            binding.finalLayoutParent.marginAmount.setText("");
            binding.finalLayoutParent.nettAmount.setText("");
            binding.finalLayoutParent.marginPercent.setText("");

            binding.finalLayoutParent.paidAmountForRelease.setText("");
            binding.finalLayoutParent.roundOffAmount.setText("");
            binding.finalLayoutParent.comments.setText("");

            binding.finalLayoutParent.itemsDataRepeat.setText("");
            setCurrentLayoutVisible();
        }
        binding.itemAddTransLayoutParent.addItem.setText("ADD");
        binding.itemAddTransLayoutParent.commodityWeight.setText("");
        binding.itemAddTransLayoutParent.stoneWastage.setText("");
        binding.itemAddTransLayoutParent.otherWastage.setText("");
        binding.itemAddTransLayoutParent.purity.setText("");
        binding.itemAddTransLayoutParent.margin.setText("");
        binding.itemAddTransLayoutParent.selectItem.setSelection(0);
        binding.itemAddTransLayoutParent.nettWeight.setText("Net Weight: 0.00");
        binding.itemAddTransLayoutParent.calculatedItemAmount.setText("Rs. 0.00");


    }

    void setCurrentLayoutVisible() {
        switch (current) {
            case first:
                binding.firstStepLayout.setVisibility(View.VISIBLE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.GONE);
                binding.finalLayoutParent.finalLayoutChild.setVisibility(View.GONE);

                binding.stepNext.setVisibility(View.VISIBLE);
                binding.bottomTotalLayout.setVisibility(View.GONE);
                otp = Constants.getRandomNumberString();
                break;
            case second:
                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.VISIBLE);
                binding.thirdStepDetails.setVisibility(View.GONE);
                binding.finalLayoutParent.finalLayoutChild.setVisibility(View.GONE);
                break;
            case third:
                binding.stepNext.setVisibility(View.GONE);

                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.VISIBLE);
                binding.finalLayoutParent.finalLayoutChild.setVisibility(View.GONE);

                binding.bottomTotalLayout.setVisibility(View.VISIBLE);
                binding.recyclerTransItems.setVisibility(View.VISIBLE);
                //setmRecyclerView();
                break;
            case four:
                binding.stepNext.setVisibility(View.GONE);

                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.GONE);

                binding.finalLayoutParent.finalLayoutChild.setVisibility(View.VISIBLE);
                // binding.stepLastSubmit.setText("SUBMIT");
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
        String mob = binding.autoCompleteSelectCustomer.getText().toString();
        req.customerID = customersArr.get(mob);
        if (mob.isEmpty()) {

            return null;
        }
        req.customerMob = mob.split("-")[1];
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

    AddTransactionReq addTransactionReq;

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

        double totalCommodityWeight = 0,
                totalStoneWastage = 0,
                totalOtherWastage = 0,
                totalNettWeight = 0,
                totalAmount = 0,
                grossAmount = 0,
                marginAmount = 0,
                nettAmount = 0,
                paidAmountForRelease = 0,
                marginPercent = 0,
                roundOffAmount = 0;
        if (list.size() > 0) {
            addTransactionReq = new AddTransactionReq();
            addTransactionReq.itemList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                AddTransactionReq.ItemList itemList = new AddTransactionReq.ItemList();
                totalCommodityWeight += Double.parseDouble(list.get(i).commodityWeight);
                totalStoneWastage += Double.parseDouble(list.get(i).stoneWastage);
                totalOtherWastage += Double.parseDouble(list.get(i).otherWastage);
                totalNettWeight += Double.parseDouble(list.get(i).nettWeight);
                totalAmount += Double.parseDouble(list.get(i).amount);
                marginPercent += Double.parseDouble(list.get(i).margin);

                itemList.amount = list.get(i).amount;
                itemList.itemID = list.get(i).itemID;
                itemList.commodityWeight = list.get(i).commodityWeight;
                itemList.purity = list.get(i).purity;
                itemList.stoneWastage = list.get(i).stoneWastage;
                itemList.otherWastage = list.get(i).otherWastage;
                itemList.nettWeight = list.get(i).nettWeight;

                addTransactionReq.itemList.add(itemList);
            }
            binding.grandTotalAmtBottom.setText("Total: Rs. " + totalAmount);
        } else {
            binding.grandTotalAmtBottom.setText("Total: Rs. 0.00");
        }

        paidAmountForRelease = totalAmount;
        roundOffAmount = totalAmount;
        addTransactionReq.transValidOTP = otp;
        addTransactionReq.companyID = Sessions.getUserString(context, Constants.companyId);
        addTransactionReq.userID = Sessions.getUserString(context, Constants.userId);
        addTransactionReq.customerID = customersArr.get(binding.autoCompleteSelectCustomer.getText().toString());
        addTransactionReq.branchID = branchesArr.get(binding.selectBranch.getSelectedItem().toString());
        addTransactionReq.commodity = binding.selectCommodity.getSelectedItem().toString();
        addTransactionReq.createdBy = Sessions.getUserString(context, Constants.userIdID);
        addTransactionReq.presentDayCommodityRate = binding.commodityRate.getText().toString();

        addTransactionReq.totalCommodityWeight = totalCommodityWeight + "";
        addTransactionReq.totalStoneWastage = totalStoneWastage + "";
        addTransactionReq.totalOtherWastage = totalOtherWastage + "";
        addTransactionReq.totalNettWeight = totalNettWeight + "";
        addTransactionReq.totalAmount = totalAmount + "";

        addTransactionReq.grossAmount = grossAmount + "";
        addTransactionReq.marginAmount = marginAmount + "";
        addTransactionReq.nettAmount = nettAmount + "";
        addTransactionReq.paidAmountForRelease = paidAmountForRelease + "";
        addTransactionReq.marginPercent = marginPercent + "";
        addTransactionReq.roundOffAmount = roundOffAmount + "";

        addTransactionReq.referencePicData = "";
        addTransactionReq.nbfcReferenceNo = "DYU_" + Constants.getDateNowAll();
        addTransactionReq.comments = "";
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

    int position;

    @Override
    public void oncItemClicked(View view, int position) {
        this.position = position;
        binding.itemAddTransLayoutParent.commodityWeight.setText(list.get(position).commodityWeight);
        binding.itemAddTransLayoutParent.stoneWastage.setText(list.get(position).stoneWastage);
        binding.itemAddTransLayoutParent.otherWastage.setText(list.get(position).otherWastage);
        binding.itemAddTransLayoutParent.purity.setText(list.get(position).purity);
        binding.itemAddTransLayoutParent.margin.setText(list.get(position).margin);
        binding.itemAddTransLayoutParent.selectItem.setSelection(
                ((ArrayAdapter<String>)
                        binding.itemAddTransLayoutParent.selectItem.getAdapter()
                ).getPosition(list.get(position).commodity));
        binding.itemAddTransLayoutParent.nettWeight.setText("Net Weight : " + list.get(position).nettWeight);
        binding.itemAddTransLayoutParent.calculatedItemAmount.setText("Rs. " + list.get(position).amount);

        binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
        binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.VISIBLE);
        binding.recyclerTransItems.setVisibility(View.GONE);
        binding.bottomTotalLayout.setVisibility(View.GONE);
        binding.itemAddTransLayoutParent.addItem.setText("UPDATE");
    }

    @Override
    public void onUiVerifyOtpSuccess(CustomerWithOTPRes body) {
        progressDialog.dismiss();
        current = second;
        binding.numbver.setText("Verify +91 " + binding.autoCompleteSelectCustomer.getText().toString().split("-")[1]);
        startCounter();
        setCurrentLayoutVisible();
        binding.otpedit.setText(otp);

    }

    @Override
    public void onAddTransSuccess(AddTransactionRes res) {
        progressDialog.dismiss();
        Constants.Toasty(context, res.response + "" + res.transactionID, Constants.success);
        Constants.alertDialogShow(context, res.response + "\n" + res.transactionID,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetInnerAddItem(true);
                    }
                });
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {

    }

    @Override
    public void onGetDropDownsSuccess(DropdownDataForCompanyRes res) {
        progressDialog.dismiss();

    }

    @Override
    public void onError(String msg) {
        progressDialog.dismiss();
        Constants.Toasty(context, msg, Constants.error);
    }

    @Override
    public void onErrorComplete(String msg) {
        progressDialog.dismiss();
        Constants.Toasty(context, msg, Constants.warning);
    }
}
