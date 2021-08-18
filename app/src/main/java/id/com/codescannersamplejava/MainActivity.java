package id.com.codescannersamplejava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    // Define Global Variable
    private static final int RC_CAMERA = 120;
    private CodeScanner mCodeScanner;
    private CodeScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Scanner View from Layout
        mScannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, mScannerView);

        // Set Response when code detected when scanning
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show decoded result using toast
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Add Action When Scanner View Clicked
        mScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code Scanner start using camera for preview
                startPreviewCamera();
            }
        });
    }

    @AfterPermissionGranted(RC_CAMERA)
    public void startPreviewCamera(){
        if (hasCameraPermission()) {
            // Already have permission, do the thing
            mCodeScanner.startPreview();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your camera so you can scan barcode",
                    RC_CAMERA,
                    Manifest.permission.CAMERA);
        }
    }

    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When resume activity start preview code scanner
        startPreviewCamera();
    }

    @Override
    protected void onPause() {
        // When pause release code scanner resource
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}