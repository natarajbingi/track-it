package com.a.goldtrack.camera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import com.a.goldtrack.BuildConfig;
import com.a.goldtrack.R;
import com.a.goldtrack.users.UserForCompanyActivity;
import com.a.goldtrack.utils.FileCompressor;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CamReqActivity extends AppCompatActivity {

    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_req);
        myContext = this;
        mCompressor = new FileCompressor(this);
        selectImage();

    }


    /*  Camera Actions */
    // Camera Actions
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;

    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo",
                "Choose from Library",
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CamReqActivity.this);
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
            Intent resultIntent = new Intent();
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // TODO Add extras or a data URI to this intent as appropriate.
                resultIntent.putExtras(data);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(getRealPathFromUri(selectedImage));
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resultIntent.putExtras(data);
            }
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*Glide.with(UserForCompanyActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions()
                                //  .centerCrop()
                                //  .circleCrop()
                                .placeholder(R.drawable.ic_menu_camera))
                        .into(binding.selectedImg);*/
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = new File(getRealPathFromUri(selectedImage));
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*Glide.with(UserForCompanyActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions()
                                //.centerCrop()
                                // .circleCrop()
                                .placeholder(R.drawable.ic_menu_camera))
                        .into(binding.selectedImg);*/
            }
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
       // imgHmWork.setVisibility(View.VISIBLE);
      //  GSNUtils.ToastMsg(this, "Cropping successful, Sample: " /*+ bm.getSampleSize()*/);

        File mSaveBit; // Your image file
//        String filePath = mSaveBit.getPath();
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//        mImageView.setImageBitmap(bitmap);
        return encImage;
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
}
