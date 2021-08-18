package id.com.codescannersamplejava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
                        scanMalware(result);
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

    // Method for do result after scanning
    private void scanMalware(Result result) {
        if(MalwareDetector.compileIsURLSafe(result.getText())){
            // if result Malware
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Url yang dituju merupakan malware, apakah yakin mau lanjut?")
                    .setTitle("Malware Detected!")
                    .setPositiveButton("Ya", (dialog, which) -> resultAction(result.getText()))
                    .setNegativeButton("Kembali", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        } else {
            // if result not malware
            resultAction(result.getText());
        }
    }

    private void resultAction(String result){
        // detect if url redirect to browser, if not show using toast
        if(result.contains("http") || result.contains("www")){
            String url = result;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            Toast.makeText(this, "result is "+result, Toast.LENGTH_SHORT).show();
        }
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