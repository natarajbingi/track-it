package com.a.goldtrack.customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddCustomerReq;
import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.AddRemoveCommonImage;
import com.a.goldtrack.Model.AddRemoveCommonImageReq;
import com.a.goldtrack.Model.AddRemoveCommonImageRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCustomerReq;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.UpdateCustomerReq;
import com.a.goldtrack.Model.UpdateCustomerRes;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityCustomerBinding;
import com.a.goldtrack.utils.BaseActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.ImageClickLIstener;
import com.a.goldtrack.utils.LoaderDecorator;
import com.a.goldtrack.utils.Sessions;
import com.a.goldtrack.utils.UtilAimgWs;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class CustomerActivity extends BaseActivity implements View.OnClickListener, RecycleItemClicked, ICustomerhandler {

    private static final String TAG = "CustomerActivity";
    CustomerViewModel viewModel;
    ActivityCustomerBinding binding;
    Context context;

    boolean viewOrEdit = true;
    protected CustomCustomersAdapter mAdapter;
    protected List<DropdownDataForCompanyRes.CustomerList> mDataset;

    List<AddRemoveCommonImage> imgFinalList;
    GetCustomerReq custReq;
    String ImgDataProfURL = "", currentCustID = "";
    File ImgDataProf;
    int CAM_REQ_Code_Test = 0, CAM_REQ_Code_Profile = 1001,
            CAM_REQ_Code_Aadhar = 1002, CAM_REQ_Code_Dl = 1003,
            CAM_REQ_Code_Pan = 1004;
    int currentNoImgAttach = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = CustomerActivity.this;
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(CustomerViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer);
        binding.setCustModel(viewModel);

        loader = new LoaderDecorator(context);


        init();
    }

    private void init() {

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.editAgainHolder.setVisibility(View.GONE);

        Constants.hideProgress(context);

        binding.addSignalCustomer.setOnClickListener(this);
        binding.btnAddCustomer.setOnClickListener(this);
        binding.triggImgGet.setOnClickListener(this);
        binding.triggImgKYC.setOnClickListener(this);
        binding.btnAddImagesToCust.setOnClickListener(this);
        binding.removeAll.setOnClickListener(this);
        onSetEasyImg(false, context);

        custReq = new GetCustomerReq();
        imgFinalList = new ArrayList<>();

        custReq.companyID = Sessions.getUserString(context, Constants.companyId);
        custReq.customerId = "0";
        viewModel.onViewAvailable(this);
        if (Constants.isConnection()) {

            viewModel.getCustomer(custReq);
        } else {
            Constants.Toasty(context, "No Internet connection.", Constants.info);
        }
        viewModel.list.observe(this, new Observer<List<DropdownDataForCompanyRes.CustomerList>>() {
            @Override
            public void onChanged(List<DropdownDataForCompanyRes.CustomerList> customerLists) {
                mDataset = customerLists;
                //progressDialog.dismiss();
                if (mDataset.size() == 0) binding.nodataLayout.setVisibility(View.VISIBLE);
                else binding.nodataLayout.setVisibility(View.GONE);

                setmRecyclerView();
            }
        });
        binding.editAgainHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.firstStepLayout.setVisibility(View.GONE);
                binding.secondStepLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.listDetailsHolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //binding.listDetailsHolder.setRefreshing(true);
                //  binding.progressbar.setVisibility(View.VISIBLE);
                viewModel.getCustomer(custReq);
            }
        });

        binding.selectedImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap image = ((BitmapDrawable) binding.selectedImg.getDrawable()).getBitmap();
                Constants.popUpImg(context, null, "Selected Image", "", image, "bitMap");
                return false;
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
    }

    private AddCustomerReq req;

    private void setValidateAdd() {
        req = new AddCustomerReq();

        req.firstName = binding.firstName.getText().toString();
        req.lastName = binding.lastName.getText().toString();
        req.mobileNum = binding.mobileNum.getText().toString();
        req.emailId = binding.emailId.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.createdBy = Sessions.getUserString(context, Constants.userId);
        req.profilePicUrl = ImgDataProfURL;//todo

        if (req.firstName.isEmpty() || req.mobileNum.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please fill the mandatory fields.", Constants.warning);
            return;
        }

        uploadFile(ImgDataProf, req.firstName + "_" + req.mobileNum);
    }

    private void addImagesForAttach(List<AddRemoveCommonImage> imgFinalList) {
        AddRemoveCommonImageReq req = new AddRemoveCommonImageReq();
        req.data = new ArrayList<>();
        for (int i = 0; i < imgFinalList.size(); i++) {
            AddRemoveCommonImageReq.Data req1 = new AddRemoveCommonImageReq.Data();
            req1.id = imgFinalList.get(i).id;
            req1.commonID = imgFinalList.get(i).commonID;
            req1.companyID = imgFinalList.get(i).companyID;
            req1.actionType = imgFinalList.get(i).actionType;
            req1.createdBy = imgFinalList.get(i).createdBy;
            req1.imageType = imgFinalList.get(i).imageType;
            req1.imageTable = imgFinalList.get(i).imageTable;
            req1.imagePath = imgFinalList.get(i).imagePath;

            req.data.add(req1);
        }
        viewModel.addRemoveCommonImageReq(req);
    }

    private void setValidateUpdate() {
        UpdateCustomerReq req = new UpdateCustomerReq();
        req.id = binding.companyId.getText().toString();
        req.firstName = binding.firstName.getText().toString();
        req.lastName = binding.lastName.getText().toString();
        req.mobileNum = binding.mobileNum.getText().toString();
        req.emailId = binding.emailId.getText().toString();
        req.address1 = binding.address1.getText().toString();
        req.address2 = binding.address2.getText().toString();
        req.state = binding.state.getText().toString();
        req.pin = binding.pin.getText().toString();
        req.delete = false;
        //req.companyID = Sessions.getUserString(context, Constants.companyId);
        req.updatedBy = Sessions.getUserString(context, Constants.userName);

        if (req.firstName.isEmpty() || req.mobileNum.isEmpty() || req.address1.isEmpty() || req.pin.isEmpty()) {
            Constants.Toasty(context, "Please fill the mandatory fields.", Constants.warning);
            return;
        }
        viewModel.updateCustomer(req);
    }

    private void addImgs() {
        binding.imgHolderInLastSetTrans.removeAllViews();
        if (imgFinalList.size() > 0) {
            binding.removeAll.setVisibility(View.VISIBLE);
            for (int i = 0; i < imgFinalList.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.img_layout, binding.imgHolderInLastSetTrans, false);
                ImageView imgNewScroll = (ImageView) newView1.findViewById(R.id.selectedImgOne);
                TextView attachmentCount = (TextView) newView1.findViewById(R.id.attachmentCount);
                attachmentCount.setText(imgFinalList.get(i).imageType + " " + (i + 1));
                // imgNewScroll.setImageBitmap(Constants.stringToBitmap(imgFinalList.get(i).imageData));
                // imgNewScroll.setOnClickListener(new ImageClickLIstener(context, Constants.stringToBitmap(imgFinalList.get(i).imageData)));

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFinalList.get(i).imageData.getAbsolutePath());
                imgNewScroll.setImageBitmap(myBitmap);

                imgNewScroll.setOnClickListener(new ImageClickLIstener(context, myBitmap));
                binding.imgHolderInLastSetTrans.addView(newView1);
            }
        } else {
            binding.removeAll.setVisibility(View.GONE);
            final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                    .inflate(R.layout.img_layout, binding.imgHolderInLastSetTrans, false);
            binding.imgHolderInLastSetTrans.addView(newView1);
        }
    }

    private void addImgUp(List<DropdownDataForCompanyRes.UploadedImages> uploadedImages) {
        binding.imgHolderInLastSetCustUp.removeAllViews();
        if (uploadedImages.size() > 0) {
            for (int i = 0; i < uploadedImages.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.img_layout, binding.imgHolderInLastSetCustUp, false);
                ImageView imgNewScroll = (ImageView) newView1.findViewById(R.id.selectedImgOne);
                TextView attachmentCount = (TextView) newView1.findViewById(R.id.attachmentCount);
                attachmentCount.setText(uploadedImages.get(i).imageType + " " + (i + 1));
                Picasso.get()
                        .load(uploadedImages.get(i).imagePath)
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.load_failed)
                        .into(imgNewScroll);
                imgNewScroll.setOnClickListener(new ImageClickLIstener(context, (uploadedImages.get(i).imagePath)));
                binding.imgHolderInLastSetCustUp.addView(newView1);
            }
        }
    }

    private void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(context, mCurrentLayoutManagerType, binding.recyclerCustomer);
        if (mAdapter == null) {
            mAdapter = new CustomCustomersAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerCustomer.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }


    private void resetAll() {
        binding.companyId.setText("");
        binding.firstName.setText("");
        binding.lastName.setText("");
        binding.mobileNum.setText("");
        binding.emailId.setText("");
        binding.address1.setText("");
        binding.address2.setText("");
        binding.state.setText("");
        binding.pin.setText("");
        // ImgData = null;

        imgFinalList.clear();
        currentNoImgAttach = 0;
        binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.selectedImg.setImageDrawable(getResources().getDrawable(R.drawable.profile_icon_menu));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
        binding.firstStepLayout.setVisibility(View.GONE);
        binding.secondStepLayout.setVisibility(View.GONE);
        binding.editAgainHolder.setVisibility(View.VISIBLE);
        binding.selectedImgLayout.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.GONE);
        binding.imgHolderInLastSetCustUp.removeAllViews();

        loader.stop();
    }

    private void setEditUpdateVals(int position) {
        currentCustID = mDataset.get(position).id;
        binding.companyId.setText(mDataset.get(position).id);
        binding.firstName.setText(mDataset.get(position).firstName);
        binding.lastName.setText(mDataset.get(position).lastName);
        binding.mobileNum.setText(mDataset.get(position).mobileNum);
        binding.emailId.setText(mDataset.get(position).emailId);
        binding.address1.setText(mDataset.get(position).address1);
        binding.address2.setText(mDataset.get(position).address2);
        binding.state.setText(mDataset.get(position).state);
        binding.pin.setText(mDataset.get(position).pin);
        // ImgData = "";
        binding.btnAddCustomer.setText("Update");
        binding.textView.setText("Update");

        if (mDataset.get(position).uploadedImages == null || mDataset.get(position).uploadedImages.size() == 0) {
            binding.editAgainHolder.setVisibility(View.VISIBLE);
            imgFinalList.clear();
        } else {
            binding.editAgainHolder.setVisibility(View.GONE);
        }

        Constants.setGilde(mDataset.get(position).profile_pic_url, binding.selectedImg);
        binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);
        binding.firstStepLayout.setVisibility(View.VISIBLE);
        binding.selectedImgLayout.setVisibility(View.VISIBLE);
        binding.secondStepLayout.setVisibility(View.GONE);

        addImgUp(mDataset.get(position).uploadedImages);
        viewOrEdit = false;
    }


    public void filter(String s) {
        Log.d("mDataset", "" + mDataset.size());
        List<DropdownDataForCompanyRes.CustomerList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (DropdownDataForCompanyRes.CustomerList d : mDataset) {

                if (d.firstName.toLowerCase().contains(s.toLowerCase())) {
                    temp.add(d);
                }
            }
            if (mAdapter != null) {
                mAdapter.updateListNew(temp);
            }
        } else {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.removeAll:
                imgFinalList.clear();
                addImgs();
                break;
            case R.id.btn_add_customer:
                if (binding.btnAddCustomer.getText().toString().equals("Add Customer"))
                    setValidateAdd();
                else
                    setValidateUpdate();
                break;
            case R.id.triggImgGet:
                onSetEasyImg(false, context);
                CAM_REQ_Code_Test = CAM_REQ_Code_Profile;
                selectImage(context);

                break;
            case R.id.triggImgKYC: {

                CAM_REQ_Code_Test = 0;
                String strSpnr = binding.kycSpnner.getSelectedItem().toString();
                if (strSpnr.equalsIgnoreCase("AADHAR CARD")) {
                    CAM_REQ_Code_Test = CAM_REQ_Code_Aadhar;

                } else if (strSpnr.equalsIgnoreCase("PAN")) {
                    CAM_REQ_Code_Test = CAM_REQ_Code_Pan;

                } else if (strSpnr.equalsIgnoreCase("DRIVING LICENCE")) {
                    CAM_REQ_Code_Test = CAM_REQ_Code_Dl;

                }
                if (CAM_REQ_Code_Test == 0) {
                    Constants.Toasty(context, "Please select Type of KYC.");
                } else {

                    onSetEasyImg(true, context);
                    selectImage(context);

                }
            }
            break;
            case R.id.btn_addImagesToCust: {
                if (imgFinalList.size() == 0) {
                    Constants.Toasty(context, "Please select at least one image to proceed.");
                } else {
                    String imgPath = imgFinalList.get(currentNoImgAttach).imageType + "_" + imgFinalList.get(currentNoImgAttach).commonID + ".PNG";
                    uploadFile(imgFinalList.get(currentNoImgAttach).imageData, imgPath);

                    //  addImagesForAttach(imgFinalList);
                }
            }
            break;
            case R.id.add_signal_customer:
                if (viewOrEdit) {

                    resetAll();
                    binding.btnAddCustomer.setText("Add Customer");
                    binding.textView.setText("Add Customer");

                    binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                    binding.firstStepLayout.setVisibility(View.VISIBLE);
                    binding.secondStepLayout.setVisibility(View.GONE);
                } else {
                    binding.addSignalCustomer.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    binding.listDetailsHolder.setVisibility(View.VISIBLE);
                    binding.addDetailsHolder.setVisibility(View.GONE);
                    binding.firstStepLayout.setVisibility(View.GONE);
                    binding.secondStepLayout.setVisibility(View.GONE);
                }
                viewOrEdit = !viewOrEdit;
                break;
        }
    }

    @Override
    public void addCustomerSuccess(AddCustomerRes res) {
        currentCustID = res.id + "";
        binding.progressbar.setVisibility(View.GONE);
        loader.stop();

        binding.addDetailsHolder.setVisibility(View.VISIBLE);
        binding.firstStepLayout.setVisibility(View.GONE);
        binding.secondStepLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateCustomerSuccess(UpdateCustomerRes res) {
        Constants.Toasty(context, "Customer Updated successfully", Constants.success);
        resetAll();
        viewOrEdit = true;
        viewModel.getCustomer(custReq);
    }


    @Override
    public void getCustomerSuccess(GetCustomerRes res) {
        binding.progressbar.setVisibility(View.GONE);
        loader.stop();
        binding.listDetailsHolder.setRefreshing(false);
    }

    @Override
    public void onAddRemoveCommonImageSuccess(AddRemoveCommonImageRes res) {
        // currentNoImgAttach++;
        // if (imgFinalList.size() > currentNoImgAttach) {
        //     uploadFile(imgFinalList.get(currentNoImgAttach).imageData, Constants.CreateFileNameWithWith_Height(600, 500, "IMG"));
        // } else {
        binding.progressbar.setVisibility(View.GONE);
        loader.stop();
        resetAll();
        viewOrEdit = true;
        viewModel.getCustomer(custReq);
        Constants.Toasty(context, "Customer Added successfully", Constants.success);

        // }
        // todo:

    }

    @Override
    public void onGetDrpSuccess(DropdownDataForCompanyRes res) {
        binding.progressbar.setVisibility(View.GONE);
        loader.stop();
        binding.listDetailsHolder.setRefreshing(false);
        Sessions.setUserObj(context, res, Constants.dorpDownSession);
    }

    @Override
    public void onErrorSpread(String msg) {
        binding.progressbar.setVisibility(View.GONE);
        loader.stop();
        Constants.Toasty(context, "Something went wrong, reason: " + msg, Constants.error);
    }

    @Override
    public void onPbShow() {
        loader.start();
    }

    @Override
    public void oncItemClicked(View view, int position) {
        Constants.Toasty(context, "Edit " + mDataset.get(position).firstName, Constants.info);
        setEditUpdateVals(position);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] imageFiles, @NotNull MediaSource source) {
                if (imageFiles.length != 0) {
                    int i = 0;
                    if (CAM_REQ_Code_Test == CAM_REQ_Code_Profile) {
                        ImgDataProf = imageFiles[0].getFile();
                        // ImgDataProf = Constants.fileToStringOfBitmap(imageFiles[0].getFile());
                        Picasso.get()
                                .load(imageFiles[0].getFile())
                                // .fit()
                                // .centerCrop()
                                .into(binding.selectedImg);
                    } else if (CAM_REQ_Code_Test == CAM_REQ_Code_Aadhar) {
                        for (MediaFile imageFile : imageFiles) {
                            if (i == 3) {
                                break;
                            }
                            AddRemoveCommonImage req = new AddRemoveCommonImage();
                            req.imageData = (imageFile.getFile());
                            req.imageType = Constants.imageTypeAADHAR;
                            req.imageTable = Constants.imageTableCUSTOMER_IMAGE;
                            req.actionType = Constants.actionTypeADD;
                            req.companyID = Sessions.getUserString(context, Constants.companyId);
                            req.createdBy = Sessions.getUserString(context, Constants.userId);
                            req.commonID = currentCustID;
                            imgFinalList.add(req);
                            i++;
                        }

                    } else if (CAM_REQ_Code_Test == CAM_REQ_Code_Dl) {
                        for (MediaFile imageFile : imageFiles) {
                            if (i == 3) {
                                break;
                            }
                            AddRemoveCommonImage req = new AddRemoveCommonImage();
                            req.imageData = (imageFile.getFile());
                            req.imageType = Constants.imageTypeDL;
                            req.imageTable = Constants.imageTableCUSTOMER_IMAGE;
                            req.actionType = Constants.actionTypeADD;
                            req.companyID = Sessions.getUserString(context, Constants.companyId);
                            req.createdBy = Sessions.getUserString(context, Constants.userId);
                            req.commonID = currentCustID;
                            imgFinalList.add(req);
                            i++;
                        }
                    } else if (CAM_REQ_Code_Test == CAM_REQ_Code_Pan) {
                        for (MediaFile imageFile : imageFiles) {
                            if (i == 3) {
                                break;
                            }
                            AddRemoveCommonImage req = new AddRemoveCommonImage();
                            req.imageData = (imageFile.getFile());
                            req.imageType = Constants.imageTypeANYTHING;
                            req.imageTable = Constants.imageTableCUSTOMER_IMAGE;
                            req.actionType = Constants.actionTypeADD;
                            req.companyID = Sessions.getUserString(context, Constants.companyId);
                            req.createdBy = Sessions.getUserString(context, Constants.userId);
                            req.commonID = currentCustID;
                            imgFinalList.add(req);
                            i++;
                        }
                    }

                    addImgs();
                    if (imageFiles.length > 3) {
                        Constants.Toasty(context, "Max 3 Image can upload.");
                    }
                    //  } else
                    //  Constants.Toasty(context, "Image Loading have a problem please try again.");
                }
                //  } else Constants.Toasty(context, "Image Loading have a problem please try again.");


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

        loader.start();
        /*
         * A TransferListener class that can listen to a upload task and be notified
         * when the status changes.
         */
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {

                if (newState.toString().equalsIgnoreCase("COMPLETED")) {
                    Log.e(TAG, "onStateChanged: " + uploadedVidUrl);
                    // loader.stop();

                    if (CAM_REQ_Code_Test != CAM_REQ_Code_Profile) {
                        imgFinalList.get(currentNoImgAttach).imagePath = uploadedVidUrl;
                        currentNoImgAttach++;
                        if (imgFinalList.size() > currentNoImgAttach) {
                            uploadFile(imgFinalList.get(currentNoImgAttach).imageData, Constants.CreateFileNameWithWith_Height(600, 500, "IMG"));
                        } else {
                            addImagesForAttach(imgFinalList);
                        }
                    } else {
                        req.profilePicUrl = uploadedVidUrl;
                        viewModel.addCustomer(req);
                    }

                } else if (newState.toString().equalsIgnoreCase("FAILED")) {
                    loader.stop();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",  id, bytesTotal, bytesCurrent));

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
            }

            @Override
            public void onError(int id, Exception ex) {
                loader.stop();
            }
        });


    }


}
