package com.example.meranattendancesystem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends Fragment {

    private EditText l_email;
    private EditText l_pass;
    private Button l_login;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private ProgressDialog pd;
    private String Admin;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, null);

        l_email = (EditText) v.findViewById(R.id.l_emailtxt);
        l_pass = (EditText) v.findViewById(R.id.l_passtxt);
        l_login = (Button) v.findViewById(R.id.l_loginbtn);
        pd = new ProgressDialog(getActivity().getApplicationContext());
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("AdminEmail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admin = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        l_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        return v;
    }
    public void login(){
        String Email,Pass;
        Email = l_email.getText().toString();
        Pass = l_pass.getText().toString();

        if (TextUtils.isEmpty(Pass)) {
            l_pass.setError("يرجى إدخال كلمة المرور");
            l_pass.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Email)) {
            l_email.setError("يرجى إدخال البريد الالكتروني");
            l_email.requestFocus();
            return;
        }
        if (Pass.length()<6) {
            l_pass.setError("يجب أن تكون كلمة المرور أطول من 6");
            l_pass.requestFocus();
            return;
        }
        if (!isValidEmail(Email)) {
            l_email.setError("البريد الالكتروني غير صالح");
            return;
        }

        //pd.setMessage("LOGIN");
       // pd.show();

        auth.signInWithEmailAndPassword(Email,Pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity().getApplicationContext(), "تم تسجيل الدخول بنجاح",Toast.LENGTH_LONG).show();

                            if(Admin == auth.getCurrentUser().getEmail()) {
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.m_container, new AdminMain())
                                        .addToBackStack(null)
                                        .commit();
                            }
                            else
                            getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.m_container, new ScanQR())
                                        .addToBackStack(null).commit();
                        }
                        else
                        {
                            Toast.makeText(getActivity().getApplicationContext(), "فشل تسجيل الدخول ",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
