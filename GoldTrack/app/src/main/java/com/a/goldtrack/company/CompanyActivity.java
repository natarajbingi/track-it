package com.a.goldtrack.company;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddCompany;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCompanyBinding;
import com.a.goldtrack.utils.BaseActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.LoaderDecorator;
import com.a.goldtrack.utils.Sessions;
import com.a.goldtrack.utils.UtilAimgWs;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class CompanyActivity extends BaseActivity implements View.OnClickListener, RecycleItemClicked,
        ICompanyView {

    CompanyViewModel viewModel;
    ActivityCompanyBinding binding;

    Context context;
    boolean viewOrEdit = true;
    GetCompany reqGet;
    private static final String TAG = "CompanyActivity";
    protected CustomCompanyAdapter mAdapter;
    protected List<GetCompanyRes.ResList> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CompanyViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_company);
        binding.setCmpModel(viewModel);
        viewModel.onViewAvailable(this);
        context = CompanyActivity.this;
        loader = new LoaderDecorator(context);

        init();


        if (savedInstanceState != null) {
            photos = savedInstanceState.getParcelableArrayList(PHOTOS_KEY);
        }
    }

    private void init() {


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);


        binding.addSignalCompany.setOnClickListener(this);
        binding.btnAddCompany.setOnClickListener(this);
        reqGet = new GetCompany();
        reqGet.companyId = "0";
        onSetEasyImg(false, context);
        viewModel.getCompany(reqGet);
        viewModel.list.observe(this, new Observer<GetCompanyRes>() {
            @Override
            public void onChanged(GetCompanyRes getCompanyRes) {
                mDataset = getCompanyRes.resList;
                loader.stop();
                //progressDialog.dismiss();
                setmRecyclerView();
            }
        });

        binding.listDetailsHolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //binding.listDetailsHolder.setRefreshing(true);
                viewModel.getCompany(reqGet);
            }
        });
        try {
            binding.search.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        binding.triggImgGet.setOnClickListener(this);
    }

    public void filter(String s) {
        Log.d("mDataset", "" + mDataset.size());
        List<GetCompanyRes.ResList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (GetCompanyRes.ResList d : mDataset) {

                if (d.name.toLowerCase().contains(s.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (mAdapter != null) {
                mAdapter.updateListNew(temp);
            }
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
            finish();//  onBackPressed();
        }
        return true;
    }

    private void setValidateAdd() {
        AddCompany req = new AddCompany();
        req.name = binding.name.getText().toString();
        req.desc = binding.desc.getText().toString();
        req.mobileNo = binding.mobileNo.getText().toString();
        req.landline = binding.landline.getText().toString();
        req.emailID = binding.emailID.getText().toString();
        req.website = binding.website.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.city = binding.city.getText().toString();
        req.district = binding.district.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.logoImageData = photos.size() != 0 ? Constants.fileToStringOfBitmap(photos.get(0).getFile()) : "";
        ;
        req.logoImagePath = "";
        req.smsSenderID = binding.smsSenderID.getText().toString();
        req.gstNo = binding.gstNo.getText().toString();

        if (req.name.isEmpty() || req.mobileNo.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }

        viewModel.addCompany(req);
    }

    private void setValidateUpdate() {
        UpdateCompanyDetails req1 = new UpdateCompanyDetails();
        req1.data = new ArrayList<UpdateCompanyDetails.Data>();
        UpdateCompanyDetails.Data req = new UpdateCompanyDetails.Data();


        req.companyId = binding.companyId.getText().toString().trim();
        req.name = binding.name.getText().toString();
        req.desc = binding.desc.getText().toString();
        req.mobileNo = binding.mobileNo.getText().toString();
        req.landline = binding.landline.getText().toString();
        req.emailID = binding.emailID.getText().toString();
        req.website = binding.website.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.city = binding.city.getText().toString();
        req.district = binding.district.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.delete = false;
        req.modify = true;
        req.add = false;
        req.updatedDt = Constants.getDateNowyyyymmmdd();
        req.updatedBy = Sessions.getUserString(context, Constants.userId);
        req.logoImageData = photos.size() != 0 ? Constants.fileToStringOfBitmap(photos.get(0).getFile()) : "";
        req.logoImagePath = "";
        req.smsSenderID = binding.smsSenderID.getText().toString();
        req.gstNo = binding.gstNo.getText().toString();
        if (req.name.isEmpty() || req.mobileNo.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        req1.data.add(req);
        viewModel.updateCompany(req1);
    }

    @Override
    public void addCompanyDetailes() {

        Constants.Toasty(context, "Company Added successfully", Constants.success);
        resetAll();
        viewModel.getCompany(reqGet);
    }

    @Override
    public void updateCompanyDetailes() {
        // progressDialog.dismiss();
        Constants.Toasty(context, "Company Updated successfully", Constants.success);
        resetAll();
        viewOrEdit = true;
        viewModel.getCompany(reqGet);
    }

    @Override
    public void PbSHow() {
        loader.start();
    }

    @Override
    public void onSuccessGetCompany(GetCompanyRes model) {
        loader.stop();
        binding.listDetailsHolder.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onErrorSpread(String msg) {
        loader.stop();
        binding.listDetailsHolder.setRefreshing(false);
    }

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(context, mCurrentLayoutManagerType, binding.recyclerBranches);
        if (mAdapter == null) {
            mAdapter = new CustomCompanyAdapter(context, mDataset);
            binding.recyclerBranches.setAdapter(mAdapter);
            mAdapter.setClickListener(this);
        } else
            mAdapter.updateListNew(mDataset);

    }


    private void resetAll() {
        binding.name.setText("");
        binding.desc.setText("");
        binding.mobileNo.setText("");
        binding.landline.setText("");
        binding.emailID.setText("");
        binding.website.setText("");
        binding.address1.setText("");
        binding.address2.setText("");
        binding.city.setText("");
        binding.district.setText("");
        binding.state.setText("");
        binding.pin.setText("");
        binding.smsSenderID.setText("");
        binding.gstNo.setText("");
        photos.clear();
        Glide.with(context)
                .load("")
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder))
                .into(binding.selectedImg);
        binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_company:
                if (binding.btnAddCompany.getText().toString().equals("Add Company"))
                    setValidateAdd();
                else
                    setValidateUpdate();
                break;
            case R.id.add_signal_company:

                if (viewOrEdit) {

                    resetAll();
                    binding.btnAddCompany.setText("Add Company");
                    binding.textView.setText("Add Company");

                    binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
            case R.id.triggImgGet:
                selectImage(context);
                break;
        }
    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).name, Constants.info);
        setEditUpdateVals(position);
    }

    private void setEditUpdateVals(int position) {
        binding.companyId.setText(mDataset.get(position).id);
        binding.name.setText(mDataset.get(position).name);
        binding.desc.setText(mDataset.get(position).desc);
        binding.mobileNo.setText(mDataset.get(position).mobileNo);
        binding.landline.setText(mDataset.get(position).landline);
        binding.emailID.setText(mDataset.get(position).emailID);
        binding.website.setText(mDataset.get(position).website);
        binding.address1.setText(mDataset.get(position).address1);
        binding.address2.setText(mDataset.get(position).address2);
        binding.city.setText(mDataset.get(position).city);
        binding.district.setText(mDataset.get(position).district);
        binding.state.setText(mDataset.get(position).state);
        binding.pin.setText(mDataset.get(position).pin);
        binding.gstNo.setText(mDataset.get(position).gstNo);
        binding.smsSenderID.setText(mDataset.get(position).smsSenderID);

        Glide.with(context)
                .load(mDataset.get(position).logoImagePath)
                .apply(new RequestOptions()
                        //  .centerCrop()
                        //  .circleCrop()
                        .placeholder(R.drawable.placeholder))
                .into(binding.selectedImg);
        binding.btnAddCompany.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalCompany.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);

        viewOrEdit = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        GTrackApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Constants.showSnack(isConnected, binding.textView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                photos.clear();
                for (MediaFile imageFile : imageFiles) {
                    photos.add(imageFile);
                }
                Picasso.get()
                        .load(photos.get(0).getFile())
                        .fit()
                        .centerCrop()
                        .into(binding.selectedImg);
                uploadFile(photos.get(0).getFile(), Constants.CreateFileNameWithWith_Height(500, 600, selectedType));
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });
    }
    private String selectedType = "";
    private TransferUtility transferUtility;
    private UtilAimgWs util;
    private String uploadedVidUrl;

    private void uploadFile(File file, String filename1) {

        String datemade = Constants.todayDate() + "/";
        util = new UtilAimgWs();
        transferUtility = util.getTransferUtility(context);

        TransferObserver observer = transferUtility.upload(
                UtilAimgWs.AMAZON_S3_USER_FILES_BUCKET_GTRACK + datemade + filename1,//
                file, CannedAccessControlList.PublicRead
        );

        uploadedVidUrl = UtilAimgWs.AMAZON_S3_URL + UtilAimgWs.AMAZON_S3_USER_FILES_BUCKET_GTRACK + datemade + filename1;

        observer.setTransferListener(new UploadListener());
        loader.start();

        Log.d(TAG, "finalImgStr:1 " + filename1);
        Log.d(TAG, "finalImgStr:1 " + uploadedVidUrl);
    }

    /*
     * A TransferListener class that can listen to a upload task and be notified
     * when the status changes.
     */
    class UploadListener implements TransferListener {
        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            // Log.e(TAG, "Error during upload: " + id, e);

            loader.stop();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
//                    id, bytesTotal, bytesCurrent));

            float value;
            if (bytesTotal >= 1024)
                value = bytesTotal / 1024f;
            else
                value = bytesTotal;

            float cvalue;
            if (bytesCurrent >= 1024)
                cvalue = bytesCurrent / 1024f;
            else
                cvalue = bytesCurrent;

            //  progressDialog.setMax((int) value);
            //  progressDialog.setProgress((int) cvalue);
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {

            if (newState.toString().equalsIgnoreCase("COMPLETED")) {
                // req.path1 = uploadedVidUrl;
                Log.e(TAG, "onStateChanged: " + uploadedVidUrl);

                loader.stop();
                //  Retro.addEventsRes(req, AddFeedBottomSheetDialog.this);
            } else if (newState.toString().equalsIgnoreCase("FAILED")) {

                loader.stop();
            }

        }
    }


}
