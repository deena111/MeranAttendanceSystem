package com.example.meranattendancesystem;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeesAttendance extends Fragment{

    private Spinner droplist;
    private LineChart EmpChart;
    private List<String> EmpUIDs;
    private DatabaseReference Ref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.employees_attendanc, null);

        Ref = FirebaseDatabase.getInstance().getReference();

        //Setting Spinner
        droplist = (Spinner) v.findViewById(R.id.e_a_emplist);
        SetEmployeesList();

        //List of UID
        EmpUIDs = new ArrayList<String>();
        EmpUIDs = EmpUIDsList();

        //Line chart
        EmpChart = (LineChart) v.findViewById(R.id.e_a_chart);
        setChartDesign();

        //Spinner selected item & Set Title & find UID & set chart data
        droplist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = (TextView) v.findViewById(R.id.e_a_title);
                t.setText(droplist.getSelectedItem().toString());
                setChart(EmpUIDs.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        return v;
    }

    private void setChartDesign() {
        EmpChart.getDescription().setEnabled(false);
        EmpChart.setNoDataText("البيانات غير متوفرة");
        EmpChart.setNoDataTextColor(Color.parseColor("#3D646B"));
        EmpChart.setDrawGridBackground(true);
        EmpChart.animateX(1000);
        SetAxises();
    }

    private void SetAxises() {
        XAxis xAxis = EmpChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisR = EmpChart.getAxisRight();
        yAxisR.setDrawLabels(false);
    }

    //Return list of UIDs
    private List<String> EmpUIDsList() {
        final List<String> list = new ArrayList<String>();
        Ref.child("Employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ss: dataSnapshot.getChildren()) {
                    String UID = ss.getKey();
                    list.add(UID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return list;
    }

    //Set the spinner with employees' names
    private void SetEmployeesList() {
        List<String> list = new ArrayList<String>();

        Ref.child("Employees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ss: dataSnapshot.getChildren()) {
                    String emp = ss.child("Name").getValue(String.class);
                    list.add(emp);
                }

                //Setting values of spinner
                ArrayAdapter<String> Adapter =
                        new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                android.R.layout.simple_spinner_item, list);
                Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                droplist.setAdapter(Adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Set chart with data
    private void setChart(String UID){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String month = formatter.format(date).substring(3,5);
        String year = formatter.format(date).substring(6,10);

        List<Entry> InList = new ArrayList<Entry>();
        List<Entry> OutList = new ArrayList<Entry>();
        Ref.child("Employees").child(UID).child("Attendance").child(year).child(month)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float timeIn, timeOut;
                for (DataSnapshot ss: dataSnapshot.getChildren()) {
                    int day = Integer.parseInt(ss.getKey());
                    if(ss.child("In").getValue(String.class) == null
                            || ss.child("Out").getValue(String.class) == null)
                        continue;
                    else {
                        timeIn = ToFloat(ss.child("In").getValue(String.class));
                        timeOut = ToFloat(ss.child("Out").getValue(String.class));
                        InList.add(new Entry(day, timeIn));
                        OutList.add(new Entry(day, timeOut));
                    }
                }
                LineDataSet InSet = new LineDataSet(InList, "الحضور");
                LineDataSet OutSet = new LineDataSet(OutList, "الانصراف");

                InSet.setColor(Color.parseColor("#3D646B"));
                InSet.setLineWidth(2);
                InSet.setDrawCircles(false);
                InSet.setValueTextSize(8);

                OutSet.setColor(Color.parseColor("#a58a4e"));
                OutSet.setLineWidth(2);
                OutSet.setDrawCircles(false);
                OutSet.setValueTextSize(8);

                LineData chartData = new LineData(InSet, OutSet);
                EmpChart.setData(chartData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private float ToFloat(String time) {
        String resultS = "";
        resultS = resultS + time.substring(0, 2);
        double min = Double.parseDouble(time.substring(3))/60;
        resultS = resultS + Double.toString(min).substring(1);
        return Float.parseFloat(resultS);
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
