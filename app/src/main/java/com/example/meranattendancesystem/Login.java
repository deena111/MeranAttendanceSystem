package com.example.meranattendancesystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Login extends Fragment {

    private EditText l_email;
    private EditText l_pass;
    private Button l_login;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, null);

        l_email = (EditText) v.findViewById(R.id.l_emailtxt);
        l_pass = (EditText) v.findViewById(R.id.l_passtxt);
        l_login = (Button) v.findViewById(R.id.l_loginbtn);
        return v;
    }
}
