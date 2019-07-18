package com.example.meranattendancesystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Check Internet Connection
        if(!isOnline())
            Toast.makeText(getActivity().getApplicationContext(), "يرجى الاتصال بالانترنت",
                    Toast.LENGTH_SHORT).show();



        //Firebase Auth
        currentuser = FirebaseAuth.getInstance();

        //Firebase root ref
        ref = FirebaseDatabase.getInstance().getReference();

        //User already logged in
        if (currentuser.getCurrentUser() != null) {
            ref.child("AdminEmail").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Admin = dataSnapshot.getValue(String.class);
                    DirectToSuitablePage(Admin, currentuser.getCurrentUser().getEmail());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
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

                    //Get Admin's email & move to next page
                    ref.child("AdminEmail").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Admin = dataSnapshot.getValue(String.class);
                            DirectToSuitablePage(Admin, currentuser.getCurrentUser().getEmail());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
                else {
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthUserCollisionException e){
                        emailtxt.setError("البريد الالكتروني مسجل مسبقا");
                        emailtxt.requestFocus();
                    } catch (Exception e) {
                        CheckReason(e);
                    }
                }
            }
        });
    }

    private void CheckReason(Exception e) {
        if (!isOnline())
            Toast.makeText(getActivity().getApplicationContext(),
                    "يرجى الاتصال بالانترنت",
                    Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity().getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void DirectToSuitablePage(String AdminEmail, String CurrentEmail) {
        //move user to the suitable page
        if(CurrentEmail.equalsIgnoreCase(AdminEmail)){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new AdminMain())
                    .commit();
        }
        else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new ScanQR())
                    .commit();
        }
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
