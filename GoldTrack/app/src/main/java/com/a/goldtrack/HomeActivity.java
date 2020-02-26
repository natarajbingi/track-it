package com.a.goldtrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.a.goldtrack.company.CompanyActivity;
import com.a.goldtrack.companybranche.CompanyBranchesActivity;
import com.a.goldtrack.customer.CustomerActivity;
import com.a.goldtrack.items.ItemsActivity;
import com.a.goldtrack.login.LoginActivity;
import com.a.goldtrack.trans.TransActivity;
import com.a.goldtrack.users.UserForCompanyActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    Context context;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.menu_logout)
                .setDrawerLayout(drawer)
                .build();

        View hView = navigationView.getHeaderView(0);

        TextView textHeader = (TextView) hView.findViewById(R.id.text_header);
        TextView textSub = (TextView) hView.findViewById(R.id.text_sub);
        ImageView imageheaderView = (ImageView) hView.findViewById(R.id.imageheaderView);

        textHeader.setText(Sessions.getUserString(context, Constants.userName));
        textSub.setText(Sessions.getUserString(context, Constants.userId));


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_gallery:
                i = new Intent(this, CompanyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_slideshow:
                i = new Intent(this, CompanyBranchesActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_tools:
                i = new Intent(this, ItemsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_share:
                i = new Intent(this, UserForCompanyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_trans:
                i = new Intent(this, TransActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_customer:
                i = new Intent(this, CustomerActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.menu_logout: {
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.logout_confirm)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onLogOut();
                            }

                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            }
            break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void onLogOut() {
        Sessions.setUserString(context, "FALSE", Constants.keepMeSignedStr);
        Sessions.removeUserKey(context, Constants.userLogin);
        Sessions.removeUserKey(context, Constants.companyId);
        Sessions.removeUserKey(context, Constants.userId);
        Sessions.removeUserKey(context, Constants.userName);
        Sessions.removeUserKey(context, Constants.pwdId);
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
