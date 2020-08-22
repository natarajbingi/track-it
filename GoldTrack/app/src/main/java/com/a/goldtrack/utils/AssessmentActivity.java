package com.a.goldtrack.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.R;
import com.a.goldtrack.databinding.ActivityAssessmentBinding;
import com.a.goldtrack.ui.home.HomeFragment;

public class AssessmentActivity extends BaseActivity  {

    private AssessmentActViewModel viewModel;
    private ActivityAssessmentBinding binding;
    FragmentManager fragmentManager;
    boolean firstFrag = false;
    Bundle bundle = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = AssessmentActivity.this;

        loader = new LoaderDecorator(context);
        fragmentManager = getSupportFragmentManager();

        String title = getIntent().getStringExtra("title");
        String data = getIntent().getStringExtra("data");

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(GTrackApplication.getInstance()).create(AssessmentActViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_assessment);
        binding.setAsseActModel(viewModel);
        // viewModel.setView(this);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        // ab.setTitle(getString(R.string.manage_assessment));
        ab.setTitle(title);


        if (data.equals("trans"))
            SetFrag(HomeFragment.class);
       // else
       //     SetFrag(StudentVidListFragment.class);

        firstFrag = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (android.R.id.home == id) // API 5+ solution
            onBackPressed();

        if (id == R.id.action_settings) {
            sendMessage();
            Constants.Toasty(context, " Refreshing..", Constants.info);
            return true;
        }
        return true;
    }

    private void SetFrag(Class fragmentClass) {
        bundle.putString("From", "FROM");
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.assess_host_fragment, fragment);
            if (firstFrag)
                transaction.addToBackStack("" + fragmentClass.getName());

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("refresh-from-trans");
        // You can also include some extra data.
        intent.putExtra("message", "Refresh the recycler view for new Transaction !");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
