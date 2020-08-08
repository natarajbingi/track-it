package com.a.goldtrack.camera;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.a.goldtrack.BuildConfig;
import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.R;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.FileCompressor;
import com.a.goldtrack.utils.Sessions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamReqActivity extends AppCompatActivity {

    private Context myContext;
    /*  Camera Actions */
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;
    ImageView selectedImg;
    private int CAM_REQ_Code = 501;
    public static final String CAM_REQ_ImgData = "ImgData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_req);
        myContext = this;
        mCompressor = new FileCompressor(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        CAM_REQ_Code = getIntent().getExtras().getInt("CAM_REQ_Code");

        selectedImg = findViewById(R.id.imageView);
        selectImage();


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
                finish();
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
        Log.d("requestCodeCR", requestCode + "");
        Log.d("resultCodeCR", resultCode + "");
        if (resultCode == RESULT_OK) {
            Intent resultIntent = new Intent();
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {

                    String filePath = mPhotoFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    saveImage(bitmap);
                    // mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO Add extras or a data URI to this intent as appropriate.
                resultIntent.putExtra(CAM_REQ_ImgData, "Success");
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {

                    mPhotoFile = new File(getRealPathFromUri(selectedImage));
                    String filePath = mPhotoFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    saveImage(bitmap);
                    // mPhotoFile = mCompressor.compressToFile(mPhotoFile, mPhotoFile.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resultIntent.putExtra(CAM_REQ_ImgData, "Success");
            }
            Sessions.setUserString(myContext, fileToStringOfBitmap(mPhotoFile), Constants.sesImgData);
            setValNgoBack(resultIntent);
        }

    }


    private void setValNgoBack(Intent resultIntent) {
        /*Glide.with(CamReqActivity.this)
                .load(mPhotoFile)
                .apply(new RequestOptions()
                        //  .centerCrop()
                        //  .circleCrop()
                        .placeholder(R.drawable.placeholder))
                .into(selectedImg);*/
        setResult(CAM_REQ_Code, resultIntent);
        finish();
    }

    public static String fileToStringOfBitmap(File mPhotoFile) {
        // File mSaveBit; // Your image file
        String filePath = mPhotoFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        // mImageView.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        encImage = encImage == null ? "" : encImage.trim().replace("\n", "");
        return encImage;
    }

    public static byte[] fileToStringOfBitmapMap(File mPhotoFile) {
        // File mSaveBit; // Your image file
        String filePath = mPhotoFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        // mImageView.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return b;
//        return encImage;
    }


    private static void saveImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException ignore) {

        } catch (IOException ignore) {

        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + GTrackApplication.getInstance().getPackageName()
                + "/Files");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "gold_track" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        //String checkValidDataThumb = mediaFile.getAbsolutePath();

        return mediaFile;
    }


    public static Bitmap stringToBitmap(String imageString) {
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        //image.setImageBitmap(decodedImage);
        return decodedImage;
    }

    private String encodeImage() {
        // File mSaveBit; // Your image file
        String filePath = mPhotoFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        // mImageView.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
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
