package com.a.goldtrack.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Interfaces.ConnectivityReceiverListener;
import com.a.goldtrack.R;

import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;

public class BaseActivity extends AppCompatActivity implements ConnectivityReceiverListener {


    /*Images related starts*/

    public static final int CHOOSER_PERMISSIONS_REQUEST_CODE = 7459;
    public static final int CAMERA_REQUEST_CODE = 7500;
    public static final int CAMERA_VIDEO_REQUEST_CODE = 7501;
    public static final int GALLERY_REQUEST_CODE = 7502;
    public static final int DOCUMENTS_REQUEST_CODE = 7503;
    public ArrayList<MediaFile> photos = new ArrayList<>();
    public EasyImage easyImage;
    public static final String PHOTOS_KEY = "easy_image_photos_list";
    /*Images related starts*/
    public ProgressDialog progressDoalog;

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    /*Images related starts*/
    public LoaderDecorator loader;


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public static enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }


    public void showPbar(Context context) {
        if (progressDoalog == null) {
            progressDoalog = new ProgressDialog(context);
            progressDoalog.setTitle(GTrackApplication.getInstance().getApplicationContext().getString(R.string.app_name));
            progressDoalog.setCancelable(false);
            progressDoalog.setMessage("Please wait....");
        }
        progressDoalog.show();

    }

    public void hidePbar() {
        if (progressDoalog != null) {
            if (progressDoalog.isShowing()) {
                progressDoalog.dismiss();
            }
        }
    }

    public void selectImage(Context context) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    String[] necessaryPermissions = new String[]{Manifest.permission.CAMERA};
                    if (arePermissionsGranted(necessaryPermissions)) {
                        easyImage.openCameraForImage(BaseActivity.this);
                    } else {
                        requestPermissionsCompat(necessaryPermissions, CAMERA_REQUEST_CODE);
                    }
                } else if (item == 1) {
                    String[] necessaryPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (arePermissionsGranted(necessaryPermissions)) {
                        easyImage.openGallery(BaseActivity.this);
                    } else {
                        requestPermissionsCompat(necessaryPermissions, GALLERY_REQUEST_CODE);
                    }
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onSetEasyImg(boolean multiYesNo, Context context) {
        easyImage = new EasyImage.Builder(context)
                .setChooserTitle("Pick media")
                .setCopyImagesToPublicGalleryFolder(false)
//                .setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName("EasyImage sample")
                .allowMultiple(multiYesNo)
                .build();
    }

    public boolean arePermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        }
        return true;
    }

    public void setRecyclerViewLayoutManager(Context context, LayoutManagerType layoutManagerType, RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(context, 2);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(context);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(context);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    public void requestPermissionsCompat(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
    }

    public static Bitmap stringToBitmap(String imageString) {
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        //image.setImageBitmap(decodedImage);
        return decodedImage;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CHOOSER_PERMISSIONS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openChooser(BaseActivity.this);
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForImage(BaseActivity.this);
        } else if (requestCode == CAMERA_VIDEO_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForVideo(BaseActivity.this);
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openGallery(BaseActivity.this);
        } else if (requestCode == DOCUMENTS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openDocuments(BaseActivity.this);
        }
    }
}
