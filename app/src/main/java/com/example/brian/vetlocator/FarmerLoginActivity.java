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

public class FarmerLoginActivity extends AppCompatActivity {

    private EditText femail, fpassword;
    private Button flogin, fregister;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthstatelistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthstatelistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(FarmerLoginActivity.this, CustmapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        femail = (EditText) findViewById(R.id.femail);
        fpassword = (EditText) findViewById(R.id.fpassword);

        flogin = (Button) findViewById(R.id.flogin);
        fregister = (Button) findViewById(R.id.fregister);

        fregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = femail.getText().toString();
                final String password = fpassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(FarmerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(FarmerLoginActivity.this, "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                        }else {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Farmers").child(user_id);
                            current_user_db.setValue(true);
                        }
                    }
                });
            }
        });

        flogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = femail.getText().toString();
                final String password = fpassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(FarmerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(FarmerLoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
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
