package com.a.goldtrack.users;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.a.goldtrack.databinding.ActivityUserForCompanyBinding;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

import java.util.ArrayList;
import java.util.List;


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


    void setmRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        if (mAdapter == null) {
            mAdapter = new CustomUsersAdapter(context, mDataset);
            mAdapter.setClickListener(this);
            binding.recyclerUserForCmpy.setAdapter(mAdapter);
        } else mAdapter.updateListNew(mDataset);
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
        // binding.triggImgGet.setOnClickListener(this);
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
                Intent cam = new Intent(UserForCompanyActivity.this, CamReqActivity.class);
                startActivityForResult(cam, 501);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 501) {

        }
    }

}