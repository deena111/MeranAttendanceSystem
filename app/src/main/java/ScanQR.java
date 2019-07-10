import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meranattendancesystem.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class ScanQR extends Fragment {

    private EditText s_warningtxt;
    private Button s_inbtn;
    private Button s_outbtn;
    private SurfaceView s_camera;
    private BarcodeDetector barcodeDetector;
    private CameraSource  cameraSource;
    int RequestCameraPermissionID = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scanqr, null);

        s_inbtn = (Button) v.findViewById(R.id.s_inbtn);
        s_outbtn = (Button) v.findViewById(R.id.s_outbtn);

        s_camera = (SurfaceView)v.findViewById(R.id.s_camera);
        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(getActivity().getApplicationContext(),barcodeDetector)
                .setRequestedPreviewSize(640,480)
                .build();
        s_camera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                cameraSource.start()
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        return v;}
}
