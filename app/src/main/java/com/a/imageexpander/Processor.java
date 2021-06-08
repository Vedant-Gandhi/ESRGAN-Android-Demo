package com.a.imageexpander;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.a.imageexpander.ml.Esrgan;
import com.a.imageexpander.ml.EsrganDr;
import com.a.imageexpander.ml.EsrganInt8;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class Processor extends AppCompatActivity {
    private  final static String TAG="Processor";
    Uri uri;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.hideActionBar(this);
        setContentView(R.layout.activity_processor);

        uri=Uri.parse(getIntent().getStringExtra(Constant.URI_TRANSFER_NAME));

        myRunnable runnable=new myRunnable(this,getContentResolver(),this,uri);
        Thread actualRunnable=new Thread(runnable);
        actualRunnable.start();

    }

}


class myRunnable implements  Runnable{
    Context ctx;
    ContentResolver resolver;
    AppCompatActivity activity;
    Uri data;
    myRunnable(Context ctx,ContentResolver resolver,AppCompatActivity activity,Uri data){
    this.ctx=ctx;
    this.resolver=resolver;
    this.activity=activity;
    this.data=data;

}
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {

            FileDescriptor descriptor= resolver.openFileDescriptor(data,"r").getFileDescriptor();
            Bitmap image=BitmapFactory.decodeFileDescriptor(descriptor);
            ImageProcessor processor=new ImageProcessor.Builder().add(new ResizeOp(128,128, ResizeOp.ResizeMethod.BILINEAR)).build();

            try {
                Esrgan model = Esrgan.newInstance(ctx);


                // Creates inputs for reference.
                TensorImage originalImage = TensorImage.fromBitmap(image);
                originalImage=processor.process(originalImage);

                // Runs model inference and gets result.
                Esrgan.Outputs outputs = model.process(originalImage);
                TensorImage enhancedImage = outputs.getEnhancedImageAsTensorImage();
                model.close();
                Bitmap outputImage = enhancedImage.getBitmap();


                //Save the image
                File f=new File(Environment.getExternalStorageDirectory()+File.separator+ System.currentTimeMillis() / 1000 +".png");
                f.createNewFile();
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                outputImage.compress(Bitmap.CompressFormat.PNG,0,outputStream);
                byte[] _data=outputStream.toByteArray();

                FileOutputStream fs=new FileOutputStream(f);
                fs.write(_data);
                fs.flush();fs.close();

                Uri newuri=Uri.fromFile(f);
                Intent i=new Intent(ctx,ShowImage.class);
                i.putExtra(Constant.URI_TRANSFER_NAME,newuri.toString());
                ctx.startActivity(i);
                activity.finish();

            } catch (IOException e) {
                Toast.makeText(ctx,"Error occured",Toast.LENGTH_LONG).show();
            }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Processor Runnable",e.toString());
        }
    }
}