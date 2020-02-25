package com.a.goldtrack.users;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.a.goldtrack.BuildConfig;
import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.AddUserForCompany;
import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.Model.UpdateUserDetails;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityUserForCompanyBinding;
import com.a.goldtrack.network.APIService;
import com.a.goldtrack.network.RetrofitClient;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.FileCompressor;
import com.a.goldtrack.utils.Sessions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserForCompanyActivity extends AppCompatActivity implements View.OnClickListener, RecycleItemClicked, UserCompanyHandler {

    ActivityUserForCompanyBinding binding;
    UserForCompanyViewModel viewModel;
    ProgressDialog progressDialog;
    Context context;
    boolean viewOrEdit = true;
    private static final String TAG = "UserCompanyActivity";
    protected CustomUsersAdapter mAdapter;
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<GetUserForCompanyRes.ResList> mDataset;
    GetUserForCompany user;
    List<String> rolesList = new ArrayList<>();
    private int userIdIfEditing = 0;

    // Camera Actions
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;

    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomUsersAdapter(mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerUserForCmpy.setAdapter(mAdapter);
        } else mAdapter.notifyDataSetChanged();
    }

    public void setRecyclerViewLayoutManager(Constants.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (binding.recyclerUserForCmpy.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) binding.recyclerUserForCmpy.getLayoutManager())
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

        binding.recyclerUserForCmpy.setLayoutManager(mLayoutManager);
        binding.recyclerUserForCmpy.scrollToPosition(scrollPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(UserForCompanyViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_for_company);
        binding.setUserCmpModel(viewModel);
        context = UserForCompanyActivity.this;

        init();
    }

    void init() {

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("in Progress...");
        mCompressor = new FileCompressor(this);

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

        user = new GetUserForCompany();
        user.companyId = Sessions.getUserString(context, Constants.companyId);
        user.userId = "0";
        progressDialog.show();
        viewModel.getUsers(user);
        viewModel.onViewAvailable(this);
        viewModel.list.observe(this, new Observer<GetUserForCompanyRes>() {
            @Override
            public void onChanged(GetUserForCompanyRes getUserForCompanyRes) {
                mDataset = getUserForCompanyRes.resList;
                progressDialog.dismiss();
                setmRecyclerView();
            }
        });
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
        req.profilePicUrl = "";
        req.createdBy = "User";

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


    /*  Camera Actions */

    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo", "Choose from Library",
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(UserForCompanyActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(UserForCompanyActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions()
                                //  .centerCrop()
                                //  .circleCrop()
                                .placeholder(R.drawable.ic_menu_camera))
                        .into(binding.selectedImg);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(getRealPathFromUri(selectedImage));
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(UserForCompanyActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions()
                                //.centerCrop()
                                // .circleCrop()
                                .placeholder(R.drawable.ic_menu_camera))
                        .into(binding.selectedImg);
            }
        }
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions,
                            PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(),
                "Error occurred! ", Toast.LENGTH_SHORT)
                .show())
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
                selectImage();
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
        progressDialog.dismiss();
        //  mDataset = response.body().resList;
        //  setmRecyclerView();
    }

    @Override
    public void addUserSuccess(AddUserForCompanyRes res) {
        if (res.success) {
            Constants.Toasty(context, "User Added successfully", Constants.success);
            resetAll();
            viewOrEdit = true;
            viewModel.getUsers(user);
            // getUserForCompany(user);
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
        progressDialog.dismiss();
        Constants.Toasty(context, "Something went wrong, Reason: \n\t\t" + msg, Constants.error);
    }


/*
    private void addUserForCompany(AddUserForCompany req) {
        Log.d(TAG, "addUserForCompany");
        RetrofitClient retrofitSet = new RetrofitClient();
        Retrofit retrofit = retrofitSet.getClient(Constants.BaseUrl);
        APIService apiService = retrofit.create(APIService.class);
        Call<AddUserForCompanyRes> call = apiService.addUserForCompany(req);


        progressDialog.show();
        call.enqueue(new Callback<AddUserForCompanyRes>() {
            @Override
            public void onResponse(Call<AddUserForCompanyRes> call, Response<AddUserForCompanyRes> response) {
                progressDialog.dismiss();
                Constants.logPrint(call.request().toString(), req, response.body());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().success) {
                            Constants.Toasty(context, "User Added successfully", Constants.success);
                            resetAll();
                            viewOrEdit = true;
                            //getUserForCompany(user);
                        } else {
                            Constants.alertDialogShow(context, response.body().response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddUserForCompanyRes> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Response:", "" + t);
                Constants.alertDialogShow(context, "Something went wrong, please try again");
                t.printStackTrace();
            }
        });

    }

    private void updateUserDetails(UpdateUserDetails req) {
        Log.d(TAG, "addUserForCompany");
        RetrofitClient retrofitSet = new RetrofitClient();
        Retrofit retrofit = retrofitSet.getClient(Constants.BaseUrl);
        APIService apiService = retrofit.create(APIService.class);
        Call<AddUserForCompanyRes> call = apiService.updateUserDetails(req);


        progressDialog.show();
        call.enqueue(new Callback<AddUserForCompanyRes>() {
            @Override
            public void onResponse(Call<AddUserForCompanyRes> call, Response<AddUserForCompanyRes> response) {
                progressDialog.dismiss();
                Constants.logPrint(call.request().toString(), req, response.body());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().success) {
                            Constants.Toasty(context, "User Updated successfully", Constants.success);
                            resetAll();
                            viewOrEdit = true;
                            //getUserForCompany(user);
                        } else {
                            Constants.alertDialogShow(context, response.body().response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AddUserForCompanyRes> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Response:", "" + t);
                Constants.alertDialogShow(context, "Something went wrong, please try again");
                t.printStackTrace();
            }
        });

    }

    private void getUserForCompany(GetUserForCompany req) {
        Log.d(TAG, "getUserForCompany");
        RetrofitClient retrofitSet = new RetrofitClient();
        Retrofit retrofit = retrofitSet.getClient(Constants.BaseUrl);
        APIService apiService = retrofit.create(APIService.class);
        Call<GetUserForCompanyRes> call = apiService.getUserForCompany(req);


        progressDialog.show();
        call.enqueue(new Callback<GetUserForCompanyRes>() {
            @Override
            public void onResponse(Call<GetUserForCompanyRes> call, Response<GetUserForCompanyRes> response) {
                progressDialog.dismiss();
                Constants.logPrint(call.request().toString(), req, response.body());
                try {
                    if (response.isSuccessful()) {
                        if (response.body().success) {
                            mDataset = response.body().resList;
                            setmRecyclerView();
                        } else {
                            Constants.alertDialogShow(context, response.body().response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetUserForCompanyRes> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Response:", "" + t);
                Constants.alertDialogShow(context, "Something went wrong, please try again");
                t.printStackTrace();
            }
        });

    }
*/


}