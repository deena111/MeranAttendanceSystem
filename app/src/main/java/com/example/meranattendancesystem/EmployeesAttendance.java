package com.example.meranattendancesystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeesAttendance extends Fragment {

    private Spinner droplist;
    private List<String> EmpNames;
    private DatabaseReference Ref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.employees_attendanc, null);

        //Setting Spinner
        droplist = (Spinner) v.findViewById(R.id.e_a_emplist);
        EmpNames = new ArrayList<String>();
        SetEmployeesList();

        return v;
    }

    private void SetEmployeesList() {
        final List<String> list = new ArrayList<String>();

        Ref.child("Employees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ss: dataSnapshot.getChildren()) {
                    String emp = ss.child("Name").getValue(String.class);
                    list.add(emp);
                }
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
}
