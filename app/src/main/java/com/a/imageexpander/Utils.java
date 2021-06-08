package com.a.imageexpander;

import android.os.Build;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class Utils {
    public static void hideActionBar(AppCompatActivity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            activity.getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
    }
}
