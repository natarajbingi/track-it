package com.a.goldtrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.a.goldtrack.company.CompanyActivity;
import com.a.goldtrack.companybranche.CompanyBranchesActivity;
import com.a.goldtrack.customer.CustomerActivity;
import com.a.goldtrack.dailyclosure.UserDailyClosureActivity;
import com.a.goldtrack.dailyclosure.UserDailyReportActivity;
import com.a.goldtrack.items.ItemsActivity;
import com.a.goldtrack.login.LoginActivity;
import com.a.goldtrack.trans.TransActivity;
import com.a.goldtrack.users.UserForCompanyActivity;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    Context context;
    NavigationView navigationView;
    TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        versionView = findViewById(R.id.versionView);

        versionView.setText(Constants.versionView);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.menu_logout)
                .setDrawerLayout(drawer)
                .build();

        View hView = navigationView.getHeaderView(0);
        hideItem();
        TextView textHeader = (TextView) hView.findViewById(R.id.text_header);
        TextView textSub = (TextView) hView.findViewById(R.id.text_sub);
        ImageView imageheaderView = (ImageView) hView.findViewById(R.id.imageheaderView);

        textHeader.setText(Sessions.getUserString(context, Constants.userName));
        textSub.setText(Sessions.getUserString(context, Constants.userIdID));


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void hideItem() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_gallery).setVisible(false);

        String role = Sessions.getUserString(context, Constants.roles);

        switch (role) {
            case "ADMIN":
                break;
            case "EXECUTIVE":
                nav_Menu.findItem(R.id.nav_slideshow).setVisible(false);
                nav_Menu.findItem(R.id.nav_share).setVisible(false);
                nav_Menu.findItem(R.id.nav_daily_closure).setVisible(false);
                break;
            case "REPORT":
                break;
            case "SUPER_ADMIN":
                nav_Menu.findItem(R.id.nav_gallery).setVisible(true);
                break;
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            sendMessage();
            Constants.Toasty(context, " Refreshing..", Constants.info);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            case R.id.nav_report:
                i = new Intent(this, UserDailyReportActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_customer:
                i = new Intent(this, CustomerActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.nav_daily_closure:
                i = new Intent(this, UserDailyClosureActivity.class);
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

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("refresh-from-home");
        // You can also include some extra data.
        intent.putExtra("message", "Refresh the recycler view for new Transaction !");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
