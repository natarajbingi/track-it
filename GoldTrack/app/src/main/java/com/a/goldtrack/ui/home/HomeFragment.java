package com.a.goldtrack.ui.home;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.FragmentHomeBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.ImageClickLIstener;
import com.a.goldtrack.utils.LoaderDecorator;
import com.a.goldtrack.utils.Sessions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class HomeFragment extends Fragment implements RecycleItemClicked, IHomeUiView, DatePickerDialog.OnDateSetListener {


    private Constants.LayoutManagerType mCurrentLayoutManagerType;
    private CustomHomeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<GetTransactionRes.DataList> mDataset;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    // private ProgressDialog progressDialog;
    private Context context;
    private HomeViewModel viewModel;
    private LoaderDecorator loader;

    private FragmentHomeBinding binding;
    private GetTransactionReq req;
    private GetCompany reqDrop;

    boolean holderFilter = true, role;
    Map<String, String> branchesArr = null, usersArr = null;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        // progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        // progressDialog.setIndeterminate(true);
        // progressDialog.setMessage("in Progress...");

        loader = new LoaderDecorator(context);
        String str = Sessions.getUserString(context, Constants.roles);
        role = str.equals("ADMIN") || str.equals("SUPER_ADMIN");

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

        if (role) {
            req.employeeID = "0";
        } else
            req.employeeID = Sessions.getUserString(context, Constants.userId);
        req.branchID = "0";
        req.customerID = "0";
        req.commodity = "";
        req.transactionDate = /*Constants.getDateNowyyyymmmdd();*/"";
        loader.start();
        viewModel.getTransactions(req);


        if (role) {
            GetUserForCompany req1 = new GetUserForCompany();
            req1.companyId = Sessions.getUserString(context, Constants.companyId);
            req1.userId = "0";
            viewModel.getUsers(req1);
            binding.emplyeeHolder.setVisibility(View.VISIBLE);
        } else {
            binding.emplyeeHolder.setVisibility(View.GONE);
        }
        viewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                binding.textHome.setText(s);
            }
        });
        viewModel.transList.observe(this, new Observer<GetTransactionRes>() {
            @Override
            public void onChanged(GetTransactionRes getTransactionRes) {
                loader.stop();
                binding.filterHolder.setVisibility(View.GONE);
                holderFilter = true;
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


        binding.filterReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holderFilter) {
                    binding.filterHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.filterHolder.setVisibility(View.GONE);
                }
                holderFilter = !holderFilter;
            }
        });
        binding.filterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dateClosureFilter.setText("");
                binding.selectBranchFilter.setSelection(0);
                binding.selectCommodityFilter.setSelection(0);
                binding.selectEmployeeFilter.setSelection(0);
                loader.start();
                viewModel.getTransactions(req);
            }
        });

        binding.imgDateClickFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        HomeFragment.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });
        binding.filterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strBrnc = binding.selectBranchFilter.getSelectedItem().toString();
                String strCom = binding.selectCommodityFilter.getSelectedItem().toString();
                String struser = binding.selectEmployeeFilter.getSelectedItem().toString();
                String dateFilr = binding.dateClosureFilter.getText().toString();
                if (strBrnc.equals("Select")) {
                    strBrnc = "0";
                } else
                    strBrnc = branchesArr.get(strBrnc);

                if (strCom.equals("Select")) {
                    strCom = "";
                }
                if (struser.equals("Select")) {
                    struser = "0";
                }

                GetTransactionReq req = new GetTransactionReq();
                req.companyID = Sessions.getUserString(context, Constants.companyId);
                req.branchID = strBrnc == null ? "0" : strBrnc;
                req.transactionDate = dateFilr;
                req.commodity = strCom;
                if (role) {
                    req.employeeID = struser.equals("0") ? struser : usersArr.get(struser);
                } else
                    req.employeeID = Sessions.getUserString(context, Constants.userId);

                Constants.logPrint(null, req, null);
                loader.start();
                viewModel.getTransactions(req);
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
        loader.stop();
        if (!res.success) {
            viewModel.setmText(res.response);
            binding.textHome.setVisibility(View.VISIBLE);
        } else {
            binding.textHome.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetDrpSuccess(DropdownDataForCompanyRes dropdownRes) {
        if (dropdownRes.success) {
            Sessions.setUserObj(context, dropdownRes, Constants.dorpDownSession);
            Log.d("TAG", "Saving Dropdown Data");
            if (dropdownRes != null) {
                branchesArr = new LinkedHashMap<String, String>();
                branchesArr.put("Select", "Select");
                /* Branches */
                try {
                    for (int i = 0; i < dropdownRes.branchesList.size(); i++) {
                        branchesArr.put(dropdownRes.branchesList.get(i).branchName.toUpperCase()
                                + "-" + dropdownRes.branchesList.get(i).id, dropdownRes.branchesList.get(i).id);
                    }
                    setSpinners(binding.selectBranchFilter, branchesArr.keySet().toArray(new String[0]));
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
        if (res.success) {
            Log.d("TAG", "GetUserForCompanyRes Data");
            if (res != null) {
                usersArr = new LinkedHashMap<String, String>();
                usersArr.put("Select", "Select");
                /* users */
                try {
                    for (int i = 0; i < res.resList.size(); i++) {
                        usersArr.put(res.resList.get(i).firstName.toUpperCase()
                                + "-" + res.resList.get(i).lastName, res.resList.get(i).id);
                    }
                    setSpinners(binding.selectEmployeeFilter, usersArr.keySet().toArray(new String[0]));
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


    public void setSpinners(Spinner spr, String[] array) {
        // -----------------------------------------------
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                R.layout.custom_spinner,
                array);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // The drop down view
        spr.setAdapter(spinnerArrayAdapter);
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
                loader.start();
                viewModel.getTransactions(req);
                reqDrop = new GetCompany();
                reqDrop.companyId = Sessions.getUserString(context, Constants.companyId);
                viewModel.getDropdown(reqDrop);
            }
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

        }
    };

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + Constants.oneDigToTwo(monthOfYear + 1) + "-" + Constants.oneDigToTwo(dayOfMonth);
        binding.dateClosureFilter.setText(date);
    }
}