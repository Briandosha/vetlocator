package com.example.brian.vetlocator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VetLoginActivity extends AppCompatActivity {

    private EditText vemail, vpassword;
    private Button vlogin, vregister;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthstatelistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthstatelistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(VetLoginActivity.this, VetmapsActivity.class);
                    startActivity(intent);
                    finish();
//                    return;
                }
            }
        };

        vemail = (EditText) findViewById(R.id.vemail);
        vpassword = (EditText) findViewById(R.id.vpassword);

        vlogin = (Button) findViewById(R.id.vlogin);
        vregister = (Button) findViewById(R.id.vregister);

        vregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = vemail.getText().toString();
                final String password = vpassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(VetLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(VetLoginActivity.this, "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                        }else {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(user_id).child("name");
                            current_user_db.setValue(email);
                        }
                    }
                });
            }
        });

        vlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = vemail.getText().toString();
                final String password = vpassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(VetLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(VetLoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthstatelistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthstatelistener);
    }
}
