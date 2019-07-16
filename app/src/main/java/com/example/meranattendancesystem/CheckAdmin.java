package com.example.meranattendancesystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckAdmin extends Fragment {

    private FirebaseAuth currentuser;
    private DatabaseReference ref;
    private String Admin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_check, null);

        //Firebase Auth
        currentuser = FirebaseAuth.getInstance();

        //Firebase root ref
        ref = FirebaseDatabase.getInstance().getReference();

        //Getting Admin's Email
        ref.child("AdminEmail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admin = dataSnapshot.getValue(String.class);
                Toast.makeText(getContext(), "EMAIL"+Admin, Toast.LENGTH_LONG).show();
                DirectToSuitablePage(Admin);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return v;
    }

    private void DirectToSuitablePage(String Email) {
        //If user already logged in move to the suitable page
        if(Email == currentuser.getCurrentUser().getEmail()){
            Toast.makeText(getContext(), "YOUUUUUUUUUUUUUUU", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new AdminMain())
                    .addToBackStack(null)
                    .commit();
        }
        else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new ScanQR())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
