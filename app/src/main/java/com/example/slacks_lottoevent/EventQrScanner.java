package com.example.slacks_lottoevent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import java.util.concurrent.ExecutionException;

/*
*
*
*
* Relevant Documentation and Resources
* https://medium.com/deuk/android-camera-permission-essentials-streamlining-with-baseactivity-13be6d296224
* https://developer.android.com/media/camera/camerax/architecture
* https://developer.android.com/reference/androidx/camera/lifecycle/ProcessCameraProvider
* https://github.com/google/guava/wiki/ListenableFutureExplained
*
* */

public class EventQrScanner extends AppCompatActivity {
    private PreviewView cameraPreview;
    private static final int CAMERA_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_qr_scanner);
        cameraPreview = findViewById(R.id.cameraPreview);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            // User already granted permission is already granted, user can start camera.
            startCamera();
        }
        Button readyButton = findViewById(R.id.readyButton);
        readyButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventQrScanner.this, FullscreenQrScanner.class);
            startActivity(intent);
        });


    }
    private void startCamera(){
        // Getting an instance of ProcessCameraProvider.
        com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Adding a listener to the cameraProviderFuture to execute when the future is complete.
        cameraProviderFuture.addListener(() -> {
            try {
                // getting the camera provider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider); // Binding the preview to the camera to display camera preview
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this)); // The Executor for the listener is the Main Thread

    }
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // Creating a new preview use case.
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA; // Selecting to use back camera.
        // Setting the preview's surface provider to be the cameraPreview UI element, camera frames are sent and displayed at the cameraPreview element.
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        try {
            // Unbinding any use cases of the camera.
            cameraProvider.unbindAll();
            // Binding the camera's lifecycle to current actvity
            // lifecycle Owner is this activity, using the back camera and the use case is the preview cameraPreview of the scanner which is bound to the lifecycle.
            cameraProvider.bindToLifecycle(this, cameraSelector, preview);
        } catch (Exception e) {

        }
    }

}
