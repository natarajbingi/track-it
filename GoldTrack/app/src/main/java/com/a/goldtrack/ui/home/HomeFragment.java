package com.a.goldtrack.ui.home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.HomeActivity;
import com.a.goldtrack.Interfaces.InterfaceClasses;
import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.R;
import com.a.goldtrack.camera.CamReqActivity;
import com.a.goldtrack.databinding.FragmentHomeBinding;
import com.a.goldtrack.databinding.TransItemPopupBinding;
import com.a.goldtrack.network.APIService;
import com.a.goldtrack.network.RestFullServices;
import com.a.goldtrack.network.RetrofitClient;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.ImageClickLIstener;
import com.a.goldtrack.utils.Sessions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class HomeFragment extends Fragment implements RecycleItemClicked, IHomeUiView {


    private Constants.LayoutManagerType mCurrentLayoutManagerType;
    private CustomHomeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<GetTransactionRes.DataList> mDataset;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    private ProgressDialog progressDialog;
    private Context context;
    private HomeViewModel viewModel;

    private FragmentHomeBinding binding;
    private GetTransactionReq req;
    private GetCompany reqDrop;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setHomeFragModel(viewModel);

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("refresh-from-home"));
        // View root1 = binding.getRoot();//inflater.inflate(R.layout.fragment_home, container, false);
        binding.textHome.setVisibility(View.GONE);
        binding.radioGrp.setVisibility(View.GONE);
        binding.radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        try {
            binding.search.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        req = new GetTransactionReq();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        String role = Sessions.getUserString(context, Constants.roles);
        if (role.equals("ADMIN") || role.equals("SUPER_ADMIN")) {
            req.employeeID = "0";
        } else
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
                binding.textHome.setText(s);
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
        binding.getRoot().setTag(TAG);


        reqDrop = new GetCompany();
        reqDrop.companyId = Sessions.getUserString(context, Constants.companyId);
        viewModel.getDropdown(reqDrop);
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
        return binding.getRoot();
    }

    public void filter(String s) {
        Log.d("mDataset", "" + mDataset.size());
        List<GetTransactionRes.DataList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (GetTransactionRes.DataList d : mDataset) {

                if (d.customerName.toLowerCase().contains(s.toLowerCase()) || d.billNumber.toLowerCase().contains(s.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (mAdapter != null) {
                mAdapter.updateListNew(temp);
            }
        }
    }

    private void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomHomeAdapter(context, mDataset);
            mAdapter.setItemClicked(this);
            binding.recyclerView.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }

    private void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerView.getLayoutManager())
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

        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void oncItemClicked(View view, int position) {
        //  Constants.Toasty(context, mDataset.get(position).customerName, Constants.info);
        popupWindow(mDataset.get(position));
    }

    @Override
    public void onGetTransSuccess(GetTransactionRes res) {
        progressDialog.dismiss();
        if (!res.success) {
            viewModel.setmText(res.response);
            binding.textHome.setVisibility(View.VISIBLE);
        } else {
            binding.textHome.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetDrpSuccess(DropdownDataForCompanyRes body) {
        if (body.success) {
            Sessions.setUserObj(context, body, Constants.dorpDownSession);
            Log.d("TAG", "Saving Dropdown Data");
        } else {
            Constants.Toasty(context, body.response + ", Please refresh again.", 1);
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
//        TransItemPopupBinding popupBinding = DataBindingUtil.inflate()
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.trans_item_popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupView.setFocusable(true);
        final TextView nbfcReferenceNo = (TextView) popupView.findViewById(R.id.nbfcReferenceNo);
        final TextView createdDt = (TextView) popupView.findViewById(R.id.createdDt);
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
        // final ImageView referencePicData = (ImageView) popupView.findViewById(R.id.referencePicData);
        final ImageView pdf_link = (ImageView) popupView.findViewById(R.id.pdf_link);
        final ImageView referencePicPath = (ImageView) popupView.findViewById(R.id.referencePicPath);
        final TextView itemsDataRepeat = (TextView) popupView.findViewById(R.id.itemsDataRepeat);
        final TextView paidAmountForRelease = (TextView) popupView.findViewById(R.id.paidAmountForRelease);
        final TextView roundOffAmount = (TextView) popupView.findViewById(R.id.roundOffAmount);
        final TextView comments = (TextView) popupView.findViewById(R.id.comments);
        final TextView empName = (TextView) popupView.findViewById(R.id.empName);
        final TextView branchName = (TextView) popupView.findViewById(R.id.branchName);
        final LinearLayout itemsDataRepeatLayout = (LinearLayout) popupView.findViewById(R.id.itemsDataRepeatLayout);
        final LinearLayout imgHolderInLastSetTrans = (LinearLayout) popupView.findViewById(R.id.imgHolderInLastSetTrans);

        Button buttonRequestADD = (Button) popupView.findViewById(R.id.TestButton);


        try {
            nbfcReferenceNo.setText("Ref No: " + res.nbfcReferenceNo + "\nBill No: " + res.billNumber);
            customer.setText("Customer: " + res.customerName.toUpperCase());
            commodity.setText("Commodity: " + res.commodity);
            presentDayCommodityRate.setText("Commodity Rate: " + Constants.priceToString(res.presentDayCommodityRate));
            totalCommodityWeight.setText("Cmd Weight:\n" + res.totalCommodityWeight);
            totalStoneWastage.setText("Stone Wst:\n" + res.totalStoneWastage);
            totalOtherWastage.setText("Other Wst:\n" + res.totalOtherWastage);
            totalNettWeight.setText("Total NetWeight: " + res.totalNettWeight);
            totalAmount.setText("Total Amt: " + Constants.priceToString(res.totalAmount));
            grossAmount.setText("Gross Amt: " + Constants.priceToString(res.grossAmount));
            nettAmount.setText("Net Amt: " + Constants.priceToString(res.nettAmount));
            marginPercent.setText("Margin %: " + res.marginPercent);
            marginAmount.setText("Margin Amt: " + Constants.priceToString(res.marginAmount));
            paidAmountForRelease.setText("Released Amt: " + Constants.priceToString(res.paidAmountForRelease) + "\n\nPayable Amount: " + Constants.priceToString(res.amountPayable));
            roundOffAmount.setText("Round Off Amt: " + Constants.priceToString(res.roundOffAmount));
            comments.setText("Comments:\n " + res.comments);
            createdDt.setText("Date: " + Constants.getMiliToDateyyyymmmdd(res.createdDt));
            branchName.setText("Branck: " + res.branchName);
            empName.setText("Emp: " + res.empName);

            if (res.referencePicData != null) {
                Glide.with(context)
                        .load(res.referencePicData)
                        .apply(new RequestOptions()
                                //  .centerCrop()
                                //  .circleCrop()
                                .placeholder(R.drawable.placeholder))
                        .into(referencePicPath);
                referencePicPath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap image = ((BitmapDrawable) referencePicPath.getDrawable()).getBitmap();
                        Constants.popUpImg(context, null, "Selected Image", "", image, "bitMap");
                    }
                });
            } else {
                referencePicPath.setVisibility(View.GONE);
            }


            String itemsDataRepeatStr = "ITEMS:";

            for (int i = 0; i < res.itemList.size(); i++) {
                addItem(res.itemList.get(i), i, itemsDataRepeatLayout);
                // itemsDataRepeatStr += (i + 1) + ") " + res.itemList.get(i).itemName + "\t\t\t" + res.itemList.get(i).commodityWeight + "Grms\t\t\t Rs. " + Constants.priceToString(res.itemList.get(i).amount) + "\n\n";
            }

            if (res.uploadedImages != null)
                addImgs(imgHolderInLastSetTrans, res.uploadedImages);
            itemsDataRepeat.setText(itemsDataRepeatStr);
            pdf_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (res.billPdfPath != null && !res.billPdfPath.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(res.billPdfPath), "text/html");
                        startActivity(intent);
                    } else {
                        Constants.Toasty(context, "Sorry, No PDF Link available for selected transaction.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonRequestADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        popupWindow.showAtLocation(binding.recyclerView, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(binding.recyclerView, 0, 0);
    }

    private void addItem(GetTransactionRes.ItemList data, int i, ViewGroup view) {
        final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                .inflate(R.layout.last_list_text, view, false);

        ((TextView) newView1.findViewById(R.id.commodity)).setText((i + 1) + ") " + data.itemName);
        ((TextView) newView1.findViewById(R.id.grms)).setText(data.commodityWeight + " gms");
        ((TextView) newView1.findViewById(R.id.amt)).setText("Rs. " + data.amount);

        view.addView(newView1);
    }

    private void addImgs(ViewGroup grp, List<GetTransactionRes.UploadedImages> imgDataList) {
        grp.removeAllViews();
        if (imgDataList.size() > 0) {
            for (int i = 0; i < imgDataList.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.img_layout, grp, false);
                ImageView imgNewScroll = (ImageView) newView1.findViewById(R.id.selectedImgOne);
                TextView attachmentCount = (TextView) newView1.findViewById(R.id.attachmentCount);
                attachmentCount.setText("Attachment " + (i + 1));
                // imgNewScroll.setImageBitmap(CamReqActivity.stringToBitmap();
                Picasso.get()
                        .load(imgDataList.get(i).imagePath)
                        // .transform(new BitmapTransform(800, 600))
                        // .resize(size, size)
                        // .centerInside()
                        // .noPlaceholder()
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.load_failed)
                        .into(imgNewScroll);

                imgNewScroll.setOnClickListener(new ImageClickLIstener(context, (imgDataList.get(i).imagePath)));
                grp.addView(newView1);
            }
        }
    }

    @Override
    public void onDestroyView() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        super.onDestroyView();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction() != null && intent.getAction().equals("refresh-from-home")) {
                progressDialog.show();
                viewModel.getTransactions(req);
                reqDrop = new GetCompany();
                reqDrop.companyId = Sessions.getUserString(context, Constants.companyId);
                viewModel.getDropdown(reqDrop);
            }
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

        }
    };
}