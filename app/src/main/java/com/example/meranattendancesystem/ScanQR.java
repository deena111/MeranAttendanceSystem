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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.meranattendancesystem.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class ScanQR<Final> extends Fragment {

   // private EditText s_warningtxt;

    private TextView s_txtResult;
    private RadioButton s_inrad;
    private RadioButton s_outrad;
    private SurfaceView s_camera;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private DatabaseReference mRoottRef ;
    private DatabaseReference QRcodekey ;
    private DatabaseReference IN;
    private DatabaseReference OUT ;
    private FirebaseUser currentuser;
    private String Data;
    private Date date;

    private final int RequestCameraPermissionID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkSelfPermission(getActivity().getApplicationContext()
                            ,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID);
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
        s_inrad = (RadioButton) v.findViewById(R.id.s_inrad);
        s_outrad = (RadioButton) v.findViewById(R.id.s_outrad);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        mRoottRef= FirebaseDatabase.getInstance().getReference();

        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String day = formatter.format(date).substring(0,2);
        String month = formatter.format(date).substring(3,5);
        String year = formatter.format(date).substring(6,10);
        String time = formatter.format(date).substring(11);

        //mRoottRef.child("Employees").child(currentuser.getUid()).child("Attendance").child(year).child(month).child(day);
        IN = mRoottRef.child("Employees").child(currentuser.getUid()).child("Attendance").child(year).child(month).child(day).child("In");
        OUT= mRoottRef.child("Employees").child(currentuser.getUid()).child("Attendance").child(year).child(month).child(day).child("Out");


        s_camera = (SurfaceView) v.findViewById(R.id.s_camera);
        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource.Builder(getActivity().getApplicationContext(),
                barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        s_camera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            RequestCameraPermissionID);
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
                    Toast.makeText(getActivity().getApplicationContext(), "تم التحضير بنجاح",Toast.LENGTH_LONG).show();

                    Vibrator vibrator = (Vibrator) getActivity().getApplicationContext()
                            .getSystemService(getActivity().getApplicationContext().VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);

                    String Scancode =  qrcodes.valueAt(0).displayValue;
                    Toast.makeText(getActivity().getApplicationContext(), "تم التحضير بنجاح",Toast.LENGTH_LONG).show();
                    /*if(Data.equals(Scancode))
                    {
                        // here we chick radiobutton
                        switch (v.getId()) {
                            case R.id.s_inrad:
                                IN.setValue(time);
                                break;
                                case  R.id.s_outrad:
                                    OUT.setValue(time);
                                    break;
                        }
                    }*/
                }
            }
        });


        QRcodekey = mRoottRef.child("QRcodekey");
        QRcodekey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Data = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


        return v;}
}
