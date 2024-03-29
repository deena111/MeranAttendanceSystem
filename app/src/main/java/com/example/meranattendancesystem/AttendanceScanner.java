package com.example.meranattendancesystem;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceScanner extends Fragment {

    private SurfaceView surfaceView;
    private CameraSource camera;
    private BarcodeDetector barcodeDetector;
    private RadioButton inbtn;
    private RadioButton outbtn;
    private RadioGroup radiog;
    private DatabaseReference ref;
    private DatabaseReference KeyRef;
    private FirebaseAuth currentuser;
    private String year;
    private String month;
    private String day;
    private String time;

    private final int REQUESR_KEY = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attendance_scanner, null);

        //Set Date
        setDate();

        //Set FirebaseAuth ref
        currentuser = FirebaseAuth.getInstance();

        //Set DB ref
        ref = FirebaseDatabase.getInstance().getReference()
                .child("Employees").child(currentuser.getCurrentUser().getUid()).child("Attendance")
                .child(year).child(month).child(day);
        KeyRef = FirebaseDatabase.getInstance().getReference()
                .child("QRcodeKey");


        //Set SurfaceView invisible at the beginning & Radiobuttons
        surfaceView = (SurfaceView) v.findViewById(R.id.a_s_surfacev);
        surfaceView.setVisibility(View.INVISIBLE);
        inbtn = (RadioButton) v.findViewById(R.id.a_s_in);
        outbtn = (RadioButton) v.findViewById(R.id.a_s_in);
        radiog = (RadioGroup) v.findViewById(R.id.a_s_radiog);

        //When radio button is checked set the surfaceview visible
        radiog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                surfaceView.setVisibility(View.VISIBLE);
            }
        });

        //Set BarcodeDetector
        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();

        //Set CameraSource
        camera = new CameraSource.Builder(getActivity().getApplicationContext(),
                barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        //SurfaceView
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, REQUESR_KEY);
                    return;
                }
                try {
                    camera.start(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                camera.stop();
            }
        });

        //BarcodeDetector
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) (getActivity().getApplicationContext())
                                    .getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            KeyRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String key = dataSnapshot.getValue(String.class);
                                    WriteToDB(key, qrCodes.valueAt(0).displayValue);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    });
                }
            }
        });

        return v;
    }

    private void WriteToDB(String key, String scanResult) {
        if (key.equals(scanResult)) {
            if (inbtn.isChecked()) {
                ref.child("In").setValue(time);
                Toast.makeText(getActivity().getApplicationContext(),
                        "تم التحضير بنجاح",
                        Toast.LENGTH_LONG).show();
            } else if (outbtn.isChecked()) {
                ref.child("Out").setValue(time);
                Toast.makeText(getActivity().getApplicationContext(),
                        "تم التحضير بنجاح",
                        Toast.LENGTH_LONG).show();
            }
        }else
            Toast.makeText(getActivity().getApplicationContext(),
                    "الرمز غير صحيح!",
                    Toast.LENGTH_LONG).show();
    }

    private void setDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        day = formatter.format(date).substring(0,2);
        month = formatter.format(date).substring(3,5);
        year = formatter.format(date).substring(6,10);
        time = formatter.format(date).substring(11);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUESR_KEY: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                try {
                    camera.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
