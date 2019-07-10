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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentuser = FirebaseAuth.getInstance();
        if (currentuser.getCurrentUser() != null) {
            /*getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.m_container, new **Login()**)
                    .addToBackStack(null)
                    .commit();*/
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
            nametxt.setError("Name Empty");
            nametxt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Pass)) {
            passtxt.setError("Pass Empty");
            passtxt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Email)) {
            emailtxt.setError("Email Empty");
            emailtxt.requestFocus();
            return;
        }
        if (Pass.length()<6) {
            passtxt.setError("Pass Invalid");
            passtxt.requestFocus();
            return;
        }
        if (!isValidEmail(Email)) {
            emailtxt.setError("Email Invalid");
            return;
        }

         pd.setMessage("REGISTRATION");
         pd.show();


            currentuser.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "ok",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "not ok",Toast.LENGTH_LONG).show();
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
