package com.a.imageexpander;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class ShowImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.hideActionBar(this);
        setContentView(R.layout.activity_show_image);
        ImageView view=findViewById(R.id.displayimage);
        Uri uri=Uri.parse(getIntent().getStringExtra(Constant.URI_TRANSFER_NAME));
        FileDescriptor descriptor= null;
        try {
            descriptor = getContentResolver().openFileDescriptor(uri,"r").getFileDescriptor();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap image= BitmapFactory.decodeFileDescriptor(descriptor);
        image=Bitmap.createScaledBitmap(image,512,384,false);
        view.setImageBitmap(image);

    }
}