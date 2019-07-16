package com.example.meranattendancesystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
    private List<String> EmpNames;//DELETE?
    private List<String> EmpUIDs;
    private DatabaseReference Ref;
    private int EmpIndex;//DELETE?
    private String EmpUID;//DELETE?

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.employees_attendanc, null);

        Ref = FirebaseDatabase.getInstance().getReference();

        //Setting Spinner
        droplist = (Spinner) v.findViewById(R.id.e_a_emplist);
        EmpNames = new ArrayList<String>();//DELETE?
        SetEmployeesList();

        //List of UID
        EmpUIDs = new ArrayList<String>();
        EmpUIDs = EmpUIDsList();

        //Line chart
        EmpChart = (LineChart) v.findViewById(R.id.e_a_chart);
        EmpChart.animateX(1000);

        //Spinner selected item & find UID & set chart data
        droplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EmpIndex = i;//DELETE?
                EmpUID = EmpUIDs.get(i);//DELETE?
                setChart(EmpUIDs.get(i));
            }
        });

        return v;
    }

    private List<String> EmpUIDsList() {
        final List<String> list = new ArrayList<String>();
        //orderByChild("Name") ??
        Ref.child("Employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ss: dataSnapshot.getChildren()) {
                    String UID = ss.getKey();
                    list.add(UID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return list;
    }

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
                for (DataSnapshot ss: dataSnapshot.getChildren()) {
                    int day = Integer.parseInt(ss.getKey());
                    float timeIn = TimeToFloat(ss.child("In").getValue(String.class));
                    float timeOut = TimeToFloat(ss.child("Out").getValue(String.class));
                    InList.add(new Entry(day, timeIn));
                    OutList.add(new Entry(day, timeOut));
                }
                LineDataSet InSet = new LineDataSet(InList, "الحضور");
                LineDataSet OutSet = new LineDataSet(OutList, "الانصراف");
                LineData chartData = new LineData(InSet, OutSet);
                EmpChart.setData(chartData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private float TimeToFloat(String tmpHours) throws NumberFormatException {
        float result = 0;
        tmpHours = tmpHours.trim();

        // Try converting to float first
        try
        {
            result = new Float(tmpHours);
        }
        catch(NumberFormatException nfe)
        {
            // OK so that didn't work.  Did they use a colon?
            if(tmpHours.contains(":"))
            {
                int hours = 0;
                int minutes = 0;
                int locationOfColon = tmpHours.indexOf(":");
                try {
                    hours = new Integer(tmpHours.substring(0, locationOfColon-1));
                    minutes = new Integer(tmpHours.substring(locationOfColon+1));
                }
                catch(NumberFormatException nfe2) {
                    //need to do something here if they are still formatted wrong.
                    //perhaps throw the exception to the user to the UI to force the user
                    //to put in a correct value.
                    throw nfe2;
                }

                //add in partial hours (ie minutes if minutes are greater than zero.
                if(minutes > 0) {
                    result = minutes / 60;
                }

                //now add in the full number of hours.
                result += hours;
            }
        }

        return result;
    }

}
