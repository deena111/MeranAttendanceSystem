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

public class SignUp extends Fragment {

    private EditText emailtxt;
    private EditText passtxt ;
    private EditText nametxt ;
    private Button login ;
    private Button signup ;
    private FirebaseAuth currentuser;
    private DatabaseReference ref;
    private String Admin;
    private ProgressDialog pd;//ProgressBar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Firebase Auth
        currentuser = FirebaseAuth.getInstance();

        //Firebase root ref
        ref = FirebaseDatabase.getInstance().getReference();

        //If user already logged in move to the suitable page
        if (currentuser.getCurrentUser() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new CheckAdmin())
                    .addToBackStack(null)
                    .commit();
        }

        //Set the view
        View v = inflater.inflate(R.layout.signup, null);

        nametxt = (EditText) v.findViewById(R.id.m_nametxt);
        passtxt = (EditText) v.findViewById(R.id.m_passtxt);
        emailtxt = (EditText) v.findViewById(R.id.m_emailtxt);
        login = (Button) v.findViewById(R.id.m_loginbtn);
        signup = (Button) v.findViewById(R.id.m_signbtn);

        //Signup Button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { Signup(); }
        });

        //Login Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.m_container, new Login())
                            .addToBackStack(null)
                            .commit();
            }
        });

        return v;
    }

    public void Signup(){
        String Name,Email,Pass;
        Name = nametxt.getText().toString();
        Email = emailtxt.getText().toString();
        Pass = passtxt.getText().toString();

        //Check input
        if (TextUtils.isEmpty(Name)) {
            nametxt.setError("يرجى إدخال الاسم");
            nametxt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Pass)) {
            passtxt.setError("يرجى إدخال كلمة المرور");
            passtxt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Email)) {
            emailtxt.setError("يرجى إدخال البريد الالكتروني");
            emailtxt.requestFocus();
            return;
        }
        if (Pass.length()<6) {
            passtxt.setError("يجب أن تكون كلمة المرور أطول من 6");
            passtxt.requestFocus();
            return;
        }
        if (!isValidEmail(Email)) {
            emailtxt.setError("البريد الالكتروني غير صالح");
            return;
        }

        //Create a new user on firebase
        currentuser.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if creating user was successful inform the user and upload her info
                if(task.isSuccessful()){
                    Toast.makeText(getActivity().getApplicationContext(), "تم التسجيل بنجاح",Toast.LENGTH_LONG).show();

                    //Name
                    ref.child("Employees").child(currentuser.getCurrentUser().getUid())
                            .child("Name").setValue(Name);
                    //Email
                    ref.child("Employees").child(currentuser.getCurrentUser().getUid())
                            .child("Email").setValue(Email);

                    //Go to check
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.m_container, new CheckAdmin())
                            .addToBackStack(null)
                            .commit();
                }
                else
                    Toast.makeText(getActivity().getApplicationContext(), "فشل التسجيل",Toast.LENGTH_LONG).show();
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
