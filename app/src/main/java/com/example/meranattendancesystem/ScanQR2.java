package com.example.meranattendancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScanQR2 extends AppCompatActivity {
    private TextView s2_viewtxt;
    private Button s2_scanbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr2);
        s2_viewtxt = (TextView)findViewById(R.id.s2_viewtxt);
        s2_scanbtn = (Button)findViewById(R.id.s2_scanbtn);
        s2_scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
