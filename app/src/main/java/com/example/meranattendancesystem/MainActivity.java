package com.example.meranattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private EditText emailtxt;
    private EditText passtxt ;
    private EditText nametxt ;
    private Button login ;
    private Button signup ;
    private FirebaseAuth currentuser;
    private ProgressDialog pd;
    private DatabaseReference ref;
    private String Admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentuser = FirebaseAuth.getInstance();
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

        if (currentuser.getCurrentUser() != null) {

            if(Admin == currentuser.getCurrentUser().getEmail()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.m_container, new AdminMain())
                        .addToBackStack(null)
                        .commit();
            }else
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new ScanQR())
                    .addToBackStack(null)
                    .commit();
        }
        setContentView(R.layout.activity_main);

        nametxt = (EditText) findViewById(R.id.m_nametxt);
        passtxt = (EditText) findViewById(R.id.m_passtxt);
        emailtxt = (EditText) findViewById(R.id.m_emailtxt);
        login = (Button) findViewById(R.id.m_loginbtn);
        signup = (Button) findViewById(R.id.m_signbtn);
        pd = new ProgressDialog(this);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signup();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Admin == currentuser.getCurrentUser().getEmail()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.m_container, new AdminMain())
                            .addToBackStack(null)
                            .commit();
                }
                else
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.m_container, new Login())
                            .addToBackStack(null)
                            .commit();
            }
        });
    }
    public void Signup(){
        String Name,Email,Pass;
        Name = nametxt.getText().toString();
        Email = emailtxt.getText().toString();
        Pass = passtxt.getText().toString();


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



            currentuser.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "تم التسجيل بنجاح",Toast.LENGTH_LONG).show();
                    //Name
                    ref.child("Employees").child(currentuser.getCurrentUser().getUid())
                            .child("Name").setValue(Name);
                    //Email
                    ref.child("Employees").child(currentuser.getCurrentUser().getUid())
                            .child("Email").setValue(Email);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "فشل التسجيل",Toast.LENGTH_LONG).show();
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
