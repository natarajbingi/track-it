package com.a.goldtrack.trans;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
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
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.R;
import com.a.goldtrack.customer.CustomCustomersAdapter;
import com.a.goldtrack.databinding.ActivityTransBinding;
import com.a.goldtrack.utils.Constants;

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

    }

    @Override
    public void onBackPressed() {
        if (current != 1) {
            askBeforeExit();
        } else
            finish();//  onBackPressed();
        // overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    boolean showingItemAdd = false;

    List<ItemsTrans> addTranItemsNowTest() {
        List<ItemsTrans> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemsTrans im = new ItemsTrans();
            im.commodity = "Chain " + i;
            im.commodityWeight = "100." + i;
            im.amount = "200" + i;
            im.nettWeight = "100 " + i;
            im.purity = "100 " + i;
            im.stoneWastage = "100 " + i;
            im.otherWastage = "100 " + i;
            im.itemID = "" + i;
            list.add(im);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.step_next) {
            if (current == 1) {
                current = 2;
                startCounter();
            } else if (current == 2) {
                current = 3;
                timer.cancel();
                binding.stepNext.setVisibility(View.GONE);
                binding.bottomTotalLayout.setVisibility(View.VISIBLE);
                binding.recyclerTransItems.setVisibility(View.VISIBLE);
                setmRecyclerView();
            }
            setCurrentLayoutVisible();
        } else if (v.getId() == R.id.add_item_trans) {
            // Constants.Toasty(context, "inProgress", Constants.info);
            if (!showingItemAdd) {
                binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
                binding.itemAddTransLayoutParent.setVisibility(View.VISIBLE);
                binding.recyclerTransItems.setVisibility(View.GONE);
            } else {
                binding.addItemTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                binding.itemAddTransLayoutParent.setVisibility(View.GONE);
                binding.recyclerTransItems.setVisibility(View.VISIBLE);
            }
            showingItemAdd = !showingItemAdd;
        }
    }

    void setCurrentLayoutVisible() {
        switch (current) {
            case 1:
                binding.firstStepLayout.setVisibility(View.VISIBLE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.GONE);
                break;
            case 2:
                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.VISIBLE);
                binding.thirdStepDetails.setVisibility(View.GONE);
                break;
            case 3:
                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepOtp.setVisibility(View.GONE);
                binding.thirdStepDetails.setVisibility(View.VISIBLE);
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


    private static final String TAG = "TransActivity";
    protected CustomTransAddedItemAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;

    private void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomTransAddedItemAdapter(addTranItemsNowTest());
            mAdapter.setClickListener(this);
            binding.recyclerTransItems.setAdapter(mAdapter);
        } else mAdapter.updateListNew(addTranItemsNowTest());
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
