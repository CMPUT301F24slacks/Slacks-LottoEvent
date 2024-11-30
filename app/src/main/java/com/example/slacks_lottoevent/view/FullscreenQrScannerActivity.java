package com.example.slacks_lottoevent.view;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.slacks_lottoevent.R;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * FullscreenQrScannerActivity is a custom Activity that extends CaptureActivity from the ZXing library.
 * It is used to display a fullscreen QR code scanner with a back arrow button.
 */
public class FullscreenQrScannerActivity extends com.journeyapps.barcodescanner.CaptureActivity {

    /**
     * Initialize the content of the FullscreenQrScannerActivity activity.
     * Set the custom layout for the scanner and return the DecoratedBarcodeView from the custom layout.
     *
     * @return The DecoratedBarcodeView from the custom layout
     */
    @Override
    protected DecoratedBarcodeView initializeContent() {
        // Set the custom layout for the scanner
        setContentView(R.layout.activity_fullscreen_qr_scanner);
        // Return the DecoratedBarcodeView from the custom layout

        return findViewById(R.id.zxing_barcode_scanner);
    }

    /**
     * onCreate method for the FullscreenQrScannerActivity activity.
     * This method initializes the activity and sets up the back arrow button.
     *
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle back arrow click to finish the activity
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> finish());
    }
}
