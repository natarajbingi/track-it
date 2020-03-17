package com.a.goldtrack.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

public class ImageClickLIstener implements View.OnClickListener {
    Uri mArrayUri;
    Bitmap mArrayBitmap;
    String url;
    Context context;

    public ImageClickLIstener(Context context, Uri mArrayUri) {
//        this.position = position;
        this.mArrayUri = mArrayUri;
        this.context = context;
    }

    public ImageClickLIstener(Context context, Bitmap mArrayBitmap) {
//        this.position = position;
        this.mArrayBitmap = mArrayBitmap;
        this.context = context;
    }

    public ImageClickLIstener(Context context, String url) {
//        this.position = position;
        this.url = url;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (mArrayUri != null) {
            Constants.popUpImg(context, mArrayUri, "Selected Image", null, null, "URI");
        }
        if (mArrayBitmap != null) {
            Constants.popUpImg(context, null, "Selected Image", null, mArrayBitmap, "bitMap");
        }
        if (url != null) {
            Constants.popUpImg(context, null, "Selected Image",  url,null, "url");
        }
    }
}