package com.example.meranattendancesystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMain extends Fragment {

    private BottomNavigationView nav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_main, null);

        //Go to ScanPage
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.a_m_container, new AttendanceScanner())
                .commit();

        //NavigationBar
        nav = (BottomNavigationView) v.findViewById(R.id.a_m_nav);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.n_scan:
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.a_m_container, new AttendanceScanner())
                                .commit();
                        break;
                    case R.id.n_charts:
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.a_m_container, new EmployeesAttendance())
                                .commit();
                        break;
                    case R.id.n_generate:
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.a_m_container, new GenerateQR())
                                .commit();
                        break;
                }
                return true;
            }
        });
        return v;
    }
}
