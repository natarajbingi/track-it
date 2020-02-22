package com.a.goldtrack.addcommodityrates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityAddCommodityRatesBinding;
import com.a.goldtrack.ui.home.CustomAdapter;
import com.a.goldtrack.ui.home.HomeFragment;
import com.a.goldtrack.utils.Constants;

import java.util.List;

public class AddCommodityRatesActivity extends AppCompatActivity implements AddCommodityRatesDataHandler {

    AddCommodityRatesViewModel viewModel;
    ActivityAddCommodityRatesBinding binding;
    Context context;

    protected CustomAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetCompanyRes.ResList> mDataset;
    private static final int SPAN_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = AddCommodityRatesActivity.this;
        // setContentView(R.layout.activity_add_commodity_rates);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_commodity_rates);
        viewModel = ViewModelProviders.of(this).get(AddCommodityRatesViewModel.class);
        binding.setCmdRagesModel(viewModel);


    }


    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mAdapter = new CustomAdapter(mDataset);
        binding.recyclerCommodity.setAdapter(mAdapter);
    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerCommodity.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerCommodity.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(context, SPAN_COUNT);
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

        binding.recyclerCommodity.setLayoutManager(mLayoutManager);
        binding.recyclerCommodity.scrollToPosition(scrollPosition);
    }

}