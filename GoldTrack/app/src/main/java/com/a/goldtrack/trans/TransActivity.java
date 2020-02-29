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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.CustomerWithOTPReq;
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.R;
import com.a.goldtrack.customer.CustomCustomersAdapter;
import com.a.goldtrack.databinding.ActivityTransBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.ArrayList;
import java.util.List;

public class TransActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked {

    TransViewModel viewModel;
    ActivityTransBinding binding;
    ProgressDialog progressDialog;
    Context context;
    CountDownTimer timer;
    LinearLayout item_add_trans_layout;
    private int current = 1, first = 1, second = 2, third = 3;
    List<ItemsTrans> list;
    private static final String TAG = "TransActivity";
    protected CustomTransAddedItemAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    boolean showingItemAdd = false;
    int counter = 0;
    String otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(TransViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trans);
        binding.setTransModel(viewModel);
        context = TransActivity.this;
        setCurrentLayoutVisible();
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

        item_add_trans_layout = (LinearLayout) findViewById(R.id.item_add_trans_layout);

        binding.numbver.setText("Verify +91 9980766166");
        binding.stepNext.setVisibility(View.VISIBLE);
        binding.bottomTotalLayout.setVisibility(View.GONE);

        setCurrentLayoutVisible();
        binding.stepNext.setOnClickListener(this);
        binding.addItemTrans.setOnClickListener(this);
        binding.itemAddTransLayoutParent.addItem.setOnClickListener(this);
        binding.itemAddTransLayoutParent.cancel.setOnClickListener(this);
        viewModel.getListItemsTest();
        viewModel.list.observe(this, new Observer<List<ItemsTrans>>() {
            @Override
            public void onChanged(List<ItemsTrans> itemsTrans) {
                list = itemsTrans;
                setmRecyclerView();
            }
        });
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
                    current = 2;
                    startCounter();
                } else if (current == 2) {
                    current = 3;
                    timer.cancel();
                }
                setCurrentLayoutVisible();
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
            case R.id.resend:
                otp = Constants.getRandomNumberString();


                break;
            case R.id.add_item:
                ItemsTrans nn = new ItemsTrans();
                nn.amount = binding.itemAddTransLayoutParent.amount.getText().toString();
                nn.commodityWeight = binding.itemAddTransLayoutParent.commodityWeight.getText().toString();
                nn.stoneWastage = binding.itemAddTransLayoutParent.stoneWastage.getText().toString();
                nn.otherWastage = binding.itemAddTransLayoutParent.otherWastage.getText().toString();
                nn.nettWeight = binding.itemAddTransLayoutParent.nettWeight.getText().toString();
                nn.purity = binding.itemAddTransLayoutParent.purity.getText().toString();
                nn.commodity = binding.itemAddTransLayoutParent.selectCommodity.getSelectedItem().toString();
                nn.itemID = (list.size() + 1) + "";

                if (nn.amount.isEmpty() || nn.commodityWeight.isEmpty()
                        || nn.stoneWastage.isEmpty() || nn.otherWastage.isEmpty()
                        || nn.nettWeight.isEmpty() || nn.purity.isEmpty()
                        || nn.commodity.equals("Select")
                ) {
                    Constants.Toasty(context, "Please enter mandatory fields.", Constants.warning);
                    break;
                } else {
                    list.add(nn);
                    viewModel.list.postValue(list);

                    binding.itemAddTransLayoutParent.amount.setText("");
                    binding.itemAddTransLayoutParent.commodityWeight.setText("");
                    binding.itemAddTransLayoutParent.stoneWastage.setText("");
                    binding.itemAddTransLayoutParent.otherWastage.setText("");
                    binding.itemAddTransLayoutParent.nettWeight.setText("");
                    binding.itemAddTransLayoutParent.purity.setText("");
                    binding.itemAddTransLayoutParent.selectCommodity.setSelection(0);

                    binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.itemAddTransLayoutParent.itemAddTransLayout.setVisibility(View.GONE);
                    binding.recyclerTransItems.setVisibility(View.VISIBLE);
                    binding.bottomTotalLayout.setVisibility(View.VISIBLE);

                }
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
        timer = new CountDownTimer(50000, 1000) {
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


    void firstLayoutValidate() {
        CustomerWithOTPReq req = new CustomerWithOTPReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.userID = Sessions.getUserString(context, Constants.userId);
        req.customerID = binding.selectCustomer.getSelectedItem().toString();
        req.customerMob = binding.selectCustomer.getSelectedItem().toString();
        req.counter = (counter + 1) + "";
        req.otp = otp;

        if(req.customerMob.equals("Select")||req.customerID.equals("Select")){

            return;
        }
        viewModel.verifyOtp(req);
    }

    private void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomTransAddedItemAdapter(list);
            mAdapter.setClickListener(this);
            binding.recyclerTransItems.setAdapter(mAdapter);
        } else mAdapter.updateListNew(list);
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
}
