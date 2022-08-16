package com.example.v2;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContextParams;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.v2.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class Login extends Activity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final String VSEND = "com.example.v2.extra.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText mEmail = findViewById(R.id.lEmail);
        EditText mPassword = findViewById(R.id.lPassword);
        TextView mLoginBtn = findViewById(R.id.loginBtn);
        TextView mPassBtn = findViewById(R.id.fPassword);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        // Login
        mLoginBtn.setOnClickListener(v -> {

            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                mEmail.setError("Email is Required");
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                mPassword.setError("Password is Required");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // authenticate the user
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    String uname = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    DocumentReference docRef = db.collection("users").document(uname);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Toast.makeText(Login.this, "Welcome "+ document.getString("uname") +"!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    //Toast.makeText(Login.this, "Logged in successful!", Toast.LENGTH_SHORT).show();
                    Intent mintent = new Intent(Login.this,MainActivity.class);
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    mintent.putExtra(VSEND, uname);
                    startActivity(mintent);
                }else {
                    Toast.makeText(Login.this, "Invalid Credentials!\n\tPlease try again", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        });

        // GoBack For Find Password
        mPassBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),Register.class)));

    }

    // Back Click For Exit
    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage("Are you sure want to Exit ?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
        Toast.makeText(Login.this, "Enter your credentials", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}
