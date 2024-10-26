package com.example.slacks_lottoevent;


import android.os.Bundle;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class FullscreenQrScanner extends com.journeyapps.barcodescanner.CaptureActivity {


    @Override
    protected DecoratedBarcodeView initializeContent() {
        // Set the custom layout for the scanner
        setContentView(R.layout.activity_fullscreen_qr_scanner);
        // Return the DecoratedBarcodeView from the custom layout

        return findViewById(R.id.zxing_barcode_scanner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle back arrow click to finish the activity
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> finish());
    }
}
