package com.example.meranattendancesystem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.net.MalformedURLException;

public class GenerateQR extends Fragment {
    private EditText g_qrtxt;
    private Button g_qrbtn;
    private Button g_savebtn;
    private TextView g_msgtxt;
    private ImageView g_imageview;
    private String text2QR;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scanqr, null);

        g_qrtxt = (EditText) v.findViewById(R.id.g_qrtxt);
        g_qrbtn = (Button) v.findViewById(R.id.g_qrbtn);
        g_savebtn = (Button) v.findViewById(R.id.g_savebtn);
        g_msgtxt = (TextView) v.findViewById(R.id.g_msgtxt);
        g_imageview = (ImageView) v.findViewById(R.id.g_imageview);

        g_qrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text2QR = g_qrtxt.getText().toString().trim();
                MultiFormatWriter mltiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = mltiFormatWriter.encode("txt2QR", BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    g_imageview.setImageBitmap(bitmap);


                }
                catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }
}
