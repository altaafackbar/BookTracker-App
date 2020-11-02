package com.example.booktracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

public class ScanBarcodeActivity extends Activity {
    SurfaceView cameraPreview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        cameraPreview = (SurfaceView)findViewById(R.id.camera_preview);

    }
}
