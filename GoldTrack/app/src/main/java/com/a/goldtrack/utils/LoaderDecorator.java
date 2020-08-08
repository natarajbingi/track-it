package com.a.goldtrack.utils;

import android.app.Dialog;
import android.content.Context;

import com.a.goldtrack.R;
import com.airbnb.lottie.LottieAnimationView;

public class LoaderDecorator extends Dialog {

    public LoaderDecorator(Context context) {
        super(context, R.style.LoaderDialog);
        setContentView(R.layout.dialog_loader);
    }

    public void start() {
        show();
        ((LottieAnimationView) findViewById(R.id.loader)).playAnimation();
    }

    public void stop() {
        ((LottieAnimationView) findViewById(R.id.loader)).cancelAnimation();
        dismiss();
    }
}
