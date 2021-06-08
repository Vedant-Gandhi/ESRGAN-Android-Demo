package com.a.imageexpander;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderinFuture;
    private final static String TAG="MainActivity";
    private final static String[] permissions={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Preview p;
    private  ImageCapture capture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.hideActionBar(this);
        setContentView(R.layout.activity_main);
        p=new Preview.Builder().build();
        Button captureButton=findViewById(R.id.button);
        captureButton.setOnClickListener(v->{
            saveImage(capture);
        });

        capture=new ImageCapture.Builder().setTargetRotation(p.getTargetRotation()).build();


        if(checkifPermissionareGranted()){
            startCameraProcess();
        }
        else{
            requestPermissions();
        }
        ImageView v=findViewById(R.id.filechoosericon);
        v.setOnClickListener(v1->{
            Intent subintent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            subintent.setType("image/*");
            Intent callintent=Intent.createChooser(subintent,"Select image");
            startActivityForResult(callintent,5);
        });
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(this,permissions,20);
    }
    private boolean checkifPermissionareGranted(){
        int len=permissions.length;
        for (String permission : permissions) {
            if (!(ContextCompat.checkSelfPermission(getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return  true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 20){
            if(checkifPermissionareGranted()){
                startCameraProcess();
            }
            else {
                Toast.makeText(this,"Camera Permission is necessary",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5){
            if(resultCode == Activity.RESULT_OK){
                String uri=data.getDataString().toString();
                startNextActivity(Uri.parse(uri));
            }
        }
    }

    private void startCameraProcess(){
        PreviewView v=(PreviewView) findViewById(R.id.camerapreviewview);

        //Initialize the cameraProvider
        cameraProviderinFuture=ProcessCameraProvider.getInstance(this);
        cameraProviderinFuture.addListener(()->{
            try{
                ProcessCameraProvider cameraProvider=cameraProviderinFuture.get();
                bindtoPreview(cameraProvider,v.getSurfaceProvider(),(LifecycleOwner)this,true);
            }
            catch (Exception e){
                Log.e(TAG,"Error occured:"+e);
            }

        }, ContextCompat.getMainExecutor(this));
    }
    private void bindtoPreview(@NonNull ProcessCameraProvider cameraProvider, @NonNull Preview.SurfaceProvider previewsurfaceprovider, @NonNull LifecycleOwner lifecycleOwner, boolean cameraback){

        CameraSelector selector;
        if (cameraback){
            selector =new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        }
        else{
            selector =new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        }
        p.setSurfaceProvider(previewsurfaceprovider);
        cameraProvider.bindToLifecycle(lifecycleOwner,selector,p,capture);
    }
    private File getAppDir(){
      File f= new File(getExternalMediaDirs()[0],getResources().getString(R.string.app_name));
      f.mkdirs();
      if(f.exists()){
          return  f;
      }
      return null;
    }

    private void saveImage(ImageCapture imageCapture){
        File photo=new File(getAppDir(),new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())+".bmp");
       ImageCapture.OutputFileOptions options= new ImageCapture.OutputFileOptions.Builder(photo).build();
       imageCapture.takePicture(options,ContextCompat.getMainExecutor(this),new ImageCapture.OnImageSavedCallback(){

           @Override
           public void onImageSaved(@NonNull  ImageCapture.OutputFileResults outputFileResults) {
               startNextActivity(Uri.fromFile(photo));
           }

           @Override
           public void onError(@NonNull  ImageCaptureException exception) {
               Log.e(TAG,"Error while saving image:"+exception);

           }
       });
    }
    private void startNextActivity(Uri uri){
        Intent i =new Intent(this,Processor.class);
        i.putExtra(Constant.URI_TRANSFER_NAME,uri.toString());
        startActivity(i);
        finish();


    }
    private ImageCaptureConfig getimagecaptureconfig(){
        //ImageCaptureConfig config=
        return null;
    }
}