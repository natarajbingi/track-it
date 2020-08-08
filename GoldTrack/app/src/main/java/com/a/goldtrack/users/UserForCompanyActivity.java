package com.a.goldtrack.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddUserForCompany;
import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.Model.UpdateUserDetails;
import com.a.goldtrack.R;
import com.a.goldtrack.camera.CamReqActivity;
import com.a.goldtrack.company.CompanyActivity;
import com.a.goldtrack.customer.CustomerActivity;
import com.a.goldtrack.dailyclosure.UserDailyClosureActivity;
import com.a.goldtrack.databinding.ActivityUserForCompanyBinding;
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
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;


public class UserForCompanyActivity extends BaseActivity implements View.OnClickListener, RecycleItemClicked,
        DatePickerDialog.OnDateSetListener, UserCompanyHandler {

    ActivityUserForCompanyBinding binding;
    UserForCompanyViewModel viewModel;
    Context context;
    boolean viewOrEdit = true;
    private static final String TAG = "UserCompanyActivity";
    protected CustomUsersAdapter mAdapter;
    protected List<GetUserForCompanyRes.ResList> mDataset;
    GetUserForCompany user;
    List<String> rolesList = new ArrayList<>();
    private int userIdIfEditing = 0;
    //String ImgData = "";


    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(context, mCurrentLayoutManagerType, binding.recyclerUserForCmpy);
        if (mAdapter == null) {
            mAdapter = new CustomUsersAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerUserForCmpy.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(UserForCompanyViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_for_company);
        binding.setUserCmpModel(viewModel);
        viewModel.onViewAvailable(this);
        context = UserForCompanyActivity.this;
        loader = new LoaderDecorator(context);

        init();
        onSetEasyImg(false, context);
    }

    void init() {

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);

        binding.btnAddUser.setOnClickListener(this);
        binding.triggImgGet.setOnClickListener(this);
        binding.addSignalUserForCmpy.setOnClickListener(this);

        binding.selectedImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap image = ((BitmapDrawable) binding.selectedImg.getDrawable()).getBitmap();
                Constants.popUpImg(context, null, "Selected Image", "", image, "bitMap");
                return false;
            }
        });
        binding.imgDateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        UserForCompanyActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.setMaxDate(now);
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });
        user = new GetUserForCompany();
        user.companyId = Sessions.getUserString(context, Constants.companyId);
        user.userId = "0";
        viewModel.getUsers(user);
        viewModel.getList().observe(this, new Observer<GetUserForCompanyRes>() {
            @Override
            public void onChanged(GetUserForCompanyRes getUserForCompanyRes) {
                mDataset = getUserForCompanyRes.resList;
                hidePbar();
                setmRecyclerView();
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
        List<GetUserForCompanyRes.ResList> temp = new ArrayList<>();
        if (mDataset != null && mDataset.size() > 0) {
            for (GetUserForCompanyRes.ResList d : mDataset) {

                if (d.firstName.toLowerCase().contains(s.toLowerCase()) || d.lastName.toLowerCase().contains(s.toLowerCase())) {
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

    private void setValidNcall() {
        AddUserForCompany req = new AddUserForCompany();
        req.companyId = Sessions.getUserString(context, Constants.companyId);
        req.user_UID = "DYU_USER_" + Constants.getDateNowAll();
        req.profilePicUrl = photos.size() != 0 ? Constants.fileToStringOfBitmap(photos.get(0).getFile()) : "";
        req.createdBy = Sessions.getUserString(context, Constants.userId);

        req.userName = binding.emailID.getText().toString();
        req.emailID = binding.emailID.getText().toString();
        req.firstName = binding.firstName.getText().toString();
        req.lastName = binding.lastName.getText().toString();
        req.gender = binding.gender.getSelectedItem().toString();
        req.mobileNo = binding.mobileNo.getText().toString();
        req.dob = binding.dob.getText().toString();
        req.roles = new ArrayList<String>();
        req.roles.add(binding.role.getSelectedItem().toString());

        if (req.userName.isEmpty() || req.firstName.isEmpty() || req.mobileNo.isEmpty() || req.roles.size() == 0) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }

        viewModel.addUser(req);
    }

    private void setValidateUpdate() {
        UpdateUserDetails req1 = new UpdateUserDetails();
        req1.data = new ArrayList<UpdateUserDetails.Data>();
        UpdateUserDetails.Data req = new UpdateUserDetails.Data();

        req.companyId = binding.companyId.getText().toString().trim();
        req.id = userIdIfEditing + "";
        req.firstName = binding.firstName.getText().toString();
        req.lastName = binding.lastName.getText().toString();
        req.mobileNo = binding.mobileNo.getText().toString();
        req.gender = binding.gender.getSelectedItem().toString();
        req.emailID = binding.emailID.getText().toString();
        req.dob = binding.dob.getText().toString();
        req.roles = rolesList;
        req.delete = false;
        req.modify = true;
        req.add = false;
        req.updatedDt = Constants.getDateNowyyyymmmdd();
        req.updatedBy = Sessions.getUserString(context, Constants.userId);
        req.profilePicUrl = "";

        if (req.firstName.isEmpty() || req.mobileNo.isEmpty() || req.emailID.isEmpty() || req.roles.size() == 0) {
            Constants.Toasty(context, "Please Enter mandatory Fields", Constants.warning);
            return;
        }
        req1.data.add(req);
        viewModel.updateUser(req1);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_user:
                if (binding.btnAddUser.getText().toString().equals("Add User"))
                    setValidNcall();
                else
                    setValidateUpdate();
                break;
            case R.id.add_signal_user_for_Cmpy:
                if (viewOrEdit) {
                    resetAll();
                    binding.btnAddUser.setText("Add User");
                    binding.textView.setText("Add User");
                    binding.addSignalUserForCmpy.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));

                    binding.listDetailsHolder.setVisibility(View.GONE);
                    binding.addDetailsHolder.setVisibility(View.VISIBLE);
                } else {
                    binding.addSignalUserForCmpy.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
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

    private void resetAll() {
        binding.emailID.setText("");
        binding.emailID.setEnabled(true);
        binding.firstName.setText("");
        binding.lastName.setText("");
        binding.mobileNo.setText("");
        binding.emailID.setText("");
        binding.dob.setText("");
        binding.gender.setSelection(0);
        binding.roleListEditTime.setText("");
        binding.roleListEditTime.setVisibility(View.GONE);
        binding.role.setSelection(0);
        userIdIfEditing = 0;
        photos.clear();

        binding.addSignalUserForCmpy.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.listDetailsHolder.setVisibility(View.VISIBLE);
        binding.addDetailsHolder.setVisibility(View.GONE);
    }

    @Override
    public void oncItemClicked(View view, int position) {

        Constants.Toasty(context, "Edit " + mDataset.get(position).firstName, Constants.info);
        setEditUpdateVals(position);
    }

    private void setEditUpdateVals(int position) {
        userIdIfEditing = Integer.parseInt((mDataset.get(position).id));
        binding.companyId.setText(mDataset.get(position).companyId);
        binding.emailID.setText(mDataset.get(position).emailID);
        binding.emailID.setEnabled(false);
        binding.firstName.setText(mDataset.get(position).firstName);
        binding.lastName.setText(mDataset.get(position).lastName);
        binding.mobileNo.setText(mDataset.get(position).mobileNo);
        binding.dob.setText(mDataset.get(position).dob);
        if (mDataset.get(position).gender.equalsIgnoreCase("MALE"))
            binding.gender.setSelection(1);
        else if (mDataset.get(position).gender.equalsIgnoreCase("FEMALE"))
            binding.gender.setSelection(2);
        else
            binding.gender.setSelection(0);

        String strRole = "Roles\n";
        for (int i = 0; i < mDataset.get(position).roles.size(); i++) {
            strRole += mDataset.get(position).roles.get(i) + "\n";
        }
        rolesList.clear();
        rolesList.addAll(mDataset.get(position).roles);
        binding.roleListEditTime.setText(strRole);
        binding.roleListEditTime.setVisibility(View.VISIBLE);

        binding.btnAddUser.setText("Update");
        binding.textView.setText("Update");

        binding.addSignalUserForCmpy.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        binding.listDetailsHolder.setVisibility(View.GONE);
        binding.addDetailsHolder.setVisibility(View.VISIBLE);

        viewOrEdit = false;
    }

    @Override
    public void getUsersSuccess(GetUserForCompanyRes res) {
    }

    @Override
    public void addUserSuccess(AddUserForCompanyRes res) {
        if (res.success) {
            Constants.Toasty(context, "User Added successfully", Constants.success);
            resetAll();
            viewOrEdit = true;
            viewModel.getUsers(user);
        } else {
            Constants.alertDialogShow(context, res.response);
        }
    }

    @Override
    public void updateUserSuccess(AddUserForCompanyRes res) {
        if (res.success) {
            Constants.Toasty(context, "User Updated successfully", Constants.success);
            resetAll();
            viewOrEdit = true;
            viewModel.getUsers(user);
        } else {
            Constants.alertDialogShow(context, res.response);
        }
    }

    @Override
    public void onError(String msg) {
        Constants.Toasty(context, "Something went wrong, Reason: \n\t\t" + msg, Constants.error);
    }

    @Override
    public void pbShow() {
        showPbar(context);
        loader.start();
    }

    @Override
    public void pbHide() {
        hidePbar();
        loader.stop();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + Constants.oneDigToTwo(monthOfYear + 1) + "-" + Constants.oneDigToTwo(dayOfMonth);
        binding.dob.setText(date);
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
        pbShow();

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
            hidePbar();
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
                hidePbar();
                //  Retro.addEventsRes(req, AddFeedBottomSheetDialog.this);
            } else if (newState.toString().equalsIgnoreCase("FAILED")) {
                hidePbar();
            }

            loader.stop();
        }
    }


}