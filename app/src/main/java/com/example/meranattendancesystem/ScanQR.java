package com.example.meranattendancesystem;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.meranattendancesystem.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanQR<Final> extends Fragment {

   // private EditText s_warningtxt;

    private TextView s_txtResult;
    private Button s_inbtn;
    private Button s_outbtn;
    private SurfaceView s_camera;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext() ,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                        return;
                    }
                    try {
                        cameraSource.start(s_camera.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scanqr, null);

        s_txtResult=(TextView)v.findViewById(R.id.s_txtresult);
        s_inbtn = (Button) v.findViewById(R.id.s_inbtn);
        s_outbtn = (Button) v.findViewById(R.id.s_outbtn);

        s_camera = (SurfaceView) v.findViewById(R.id.s_camera);
        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(getActivity().getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        s_camera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext() ,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(s_camera.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size()!= 0)
                {
                    s_txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            s_txtResult.setText(qrcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        return v;}
}
