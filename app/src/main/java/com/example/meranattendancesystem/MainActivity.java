package com.example.meranattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText emailtxt;
    private EditText passtxt ;
    private EditText nametxt ;
    private Button login ;
    private Button signup ;
    private FirebaseAuth currentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nametxt = (EditText) findViewById(R.id.m_nametxt);
        passtxt = (EditText) findViewById(R.id.m_passtxt);
        emailtxt = (EditText) findViewById(R.id.m_emailtxt);
        login = (Button) findViewById(R.id.m_loginbtn);
        signup = (Button) findViewById(R.id.m_signbtn);
        currentuser = FirebaseAuth.getInstance();

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
        currentuser.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
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
}
