package com.a.goldtrack.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.R;
import com.a.goldtrack.network.APIService;
import com.a.goldtrack.network.RetrofitClient;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class HomeFragment extends Fragment implements RecycleItemClicked, IHomeUiView {

    private HomeViewModel viewModel;


    protected Constants.LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected CustomHomeAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    // protected List<GetCompanyRes.ResList> mDataset;
    List<GetTransactionRes.DataList> mDataset;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    private RadioGroup radioGrp;
    ProgressDialog progressDialog;
    Context context;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.text_home);
        textView.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        radioGrp = (RadioGroup) root.findViewById(R.id.radioGrp);
        radioGrp.setVisibility(View.GONE);
        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.linear_layout_rb:
                        setRecyclerViewLayoutManager(Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                        break;
                    case R.id.grid_layout_rb:
                        setRecyclerViewLayoutManager(Constants.LayoutManagerType.GRID_LAYOUT_MANAGER);
                        break;
                }
            }
        });
        viewModel.onViewAvailable(this);

        GetTransactionReq req = new GetTransactionReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.employeeID = Sessions.getUserString(context, Constants.userId);
        req.branchID = "0";
        req.customerID = "0";
        req.commodity = "";
        req.transactionDate = /*Constants.getDateNowyyyymmmdd();*/"";
        progressDialog.show();
        viewModel.getTransactions(req);

        viewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        viewModel.transList.observe(this, new Observer<GetTransactionRes>() {
            @Override
            public void onChanged(GetTransactionRes getTransactionRes) {
                progressDialog.dismiss();
                mDataset = getTransactionRes.dataList;
                setmRecyclerView();
            }
        });
        root.setTag(TAG);


        return root;
    }

    private void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mAdapter = new CustomHomeAdapter(mDataset);
        mAdapter.setItemClicked(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = Constants.LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, mDataset.get(position).customerName, Constants.info);
        popupWindow(mDataset.get(position));
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        progressDialog.dismiss();
        if (!res.success) {
            viewModel.setmText(res.response);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String message) {
        progressDialog.dismiss();
        Constants.Toasty(context, message, Constants.info);

    }

    @Override
    public void onErrorComplete(String s) {
        progressDialog.dismiss();
        Constants.Toasty(context, s, Constants.info);
    }


    private void popupWindow(GetTransactionRes.DataList res) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.trans_item_popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupView.setFocusable(true);
        final TextView nbfcReferenceNo = (TextView) popupView.findViewById(R.id.nbfcReferenceNo);
        final TextView customer = (TextView) popupView.findViewById(R.id.customer);
        final TextView presentDayCommodityRate = (TextView) popupView.findViewById(R.id.selectedTextCommodityPrice);
        final TextView commodity = (TextView) popupView.findViewById(R.id.commodity);
        final TextView totalCommodityWeight = (TextView) popupView.findViewById(R.id.totalCommodityWeight);
        final TextView totalStoneWastage = (TextView) popupView.findViewById(R.id.totalStoneWastage);
        final TextView totalOtherWastage = (TextView) popupView.findViewById(R.id.totalOtherWastage);
        final TextView totalNettWeight = (TextView) popupView.findViewById(R.id.totalNettWeight);
        final TextView totalAmount = (TextView) popupView.findViewById(R.id.totalAmount);
        final TextView grossAmount = (TextView) popupView.findViewById(R.id.grossAmount);
        final TextView nettAmount = (TextView) popupView.findViewById(R.id.nettAmount);
        final TextView marginPercent = (TextView) popupView.findViewById(R.id.marginPercent);
        final TextView marginAmount = (TextView) popupView.findViewById(R.id.marginAmount);
        final ImageView referencePicData = (ImageView) popupView.findViewById(R.id.referencePicData);
        final TextView itemsDataRepeat = (TextView) popupView.findViewById(R.id.itemsDataRepeat);
        final TextView paidAmountForRelease = (TextView) popupView.findViewById(R.id.paidAmountForRelease);
        final TextView roundOffAmount = (TextView) popupView.findViewById(R.id.roundOffAmount);
        final TextView comments = (TextView) popupView.findViewById(R.id.comments);

        Button buttonRequestADD = (Button) popupView.findViewById(R.id.TestButton);


        try {
            nbfcReferenceNo.setText("Ref No: " + res.nbfcReferenceNo + "\nBill No: " + res.billNumber);
            customer.setText("Customer: " + res.customerName);
            commodity.setText("Commodity: " + res.commodity);
            presentDayCommodityRate.setText("Commodity Rate: " + res.presentDayCommodityRate);
            totalCommodityWeight.setText("Cmd Weight:\n" + res.totalCommodityWeight);
            totalStoneWastage.setText("Stone Wst:\n" + res.totalStoneWastage);
            totalOtherWastage.setText("Other Wst:\n" + res.totalOtherWastage);
            totalNettWeight.setText("Total NetWeight: " + res.totalNettWeight);
            totalAmount.setText("Total Amt: " + res.totalAmount);
            grossAmount.setText("Gross Amt: " + res.grossAmount);
            nettAmount.setText("Net Amt: " + res.nettAmount);
            marginPercent.setText("Margin %: " + res.marginPercent);
            marginAmount.setText("Margin Amt: " + res.marginAmount);
            paidAmountForRelease.setText("Released Amt: " + res.paidAmountForRelease);
            roundOffAmount.setText("Round Off Amt: " + res.roundOffAmount);
            comments.setText("Comments:\n " + res.comments);


            String itemsDataRepeatStr = "ITEMS: \n";

            for (int i = 0; i < res.itemList.size(); i++) {
                itemsDataRepeatStr += (i + 1) + ") " + res.itemList.get(i).itemName + "\t\t" + res.itemList.get(i).commodityWeight + "Grms\t\t Rs. " + res.itemList.get(i).amount + "\n\n";
            }

            itemsDataRepeat.setText(itemsDataRepeatStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonRequestADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        popupWindow.showAtLocation(mRecyclerView, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(mRecyclerView, 0, 0);
    }
}