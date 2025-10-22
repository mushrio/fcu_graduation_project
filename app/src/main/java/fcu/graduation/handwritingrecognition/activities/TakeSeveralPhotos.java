package fcu.graduation.handwritingrecognition.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fcu.graduation.handwritingrecognition.R;

public class TakeSeveralPhotos extends AppCompatActivity {

    private final List<Uri> capturedImageUris = new ArrayList<>();
    ProcessCameraProvider cameraProvider;
    private PreviewView pvvSeveralPhotos;
    private ImageCapture imageCapture;
    private MaterialButton mbtnTakeSeveralPhotos;
    private MaterialButton mbtnTakePhotoComplete;
    private TextView tvPhotoed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_several_photos);

        pvvSeveralPhotos = findViewById(R.id.pvv_several_photos);
        mbtnTakeSeveralPhotos = findViewById(R.id.mbtn_take_several_photos);
        mbtnTakePhotoComplete = findViewById(R.id.mbtn_take_photo_complete);
        tvPhotoed = findViewById(R.id.tv_photoed);

        mbtnTakeSeveralPhotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        mbtnTakePhotoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capturedImageUris.isEmpty()) {
                    Toast.makeText(TakeSeveralPhotos.this, "尚未拍攝任何照片", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 傳 Uri list 到下個 Activity
                ArrayList<String> uriStrings = new ArrayList<>();
                for (Uri uri : capturedImageUris) {
                    uriStrings.add(uri.toString());
                }

                Intent intent = new Intent(TakeSeveralPhotos.this, IdentifyingImages.class);
                intent.putStringArrayListExtra("image_uris", uriStrings);
                startActivity(intent);
            }
        });

        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture =  ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderListenableFuture.get();

                    startCameraX(cameraProvider);
                } catch (ExecutionException e) {
                    throw new RuntimeException();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null)
            return;

        File photoFile = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        Uri savedImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // ✅ 存進 list
                capturedImageUris.add(savedImageUri);
                Toast.makeText(TakeSeveralPhotos.this, "已拍照", Toast.LENGTH_SHORT).show();
                tvPhotoed.setText("已拍攝" + capturedImageUris.size() + "張照片");
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(pvvSeveralPhotos.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().build();

        try {
            cameraProvider.unbindAll();

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}