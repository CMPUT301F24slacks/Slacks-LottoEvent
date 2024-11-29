package com.example.slacks_lottoevent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
*
* QR Code Using Zxing
* https://reintech.io/blog/implementing-android-app-qr-code-scanner Implementing The QR Code Scanner
* https://stackoverflow.com/questions/54513936/how-to-change-zxingscannerview-default-appearance Custom Layout
* */

/**
 * This class is responsible for scanning the QR code of the event.
 */
public class EventQrScanner extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private PreviewView cameraPreview;
    private static final int CAMERA_REQUEST_CODE = 100;

    /**
     * This method is responsible for creating the activity.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_qr_scanner);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> finish());

        Button readyButton = findViewById(R.id.readyButton);

        readyButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(FullscreenQrScanner.class);
            integrator.setPrompt("");
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setOrientationLocked(true);

            // Starting scanner
            integrator.initiateScan();
        });
    }

    /**
     * This method is responsible for handling the camera permission request result.
     * @param requestCode The request code.
     * @param resultCode The permissions requested.
     * @param data The results of the permission requests.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            String qrCodeValue = result.getContents();
            Intent intent = new Intent(EventQrScanner.this, JoinEventDetailsActivity.class);
            String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            intent.putExtra("userId", userId);
            intent.putExtra("qrCodeValue", qrCodeValue);
            startActivity(intent);


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
