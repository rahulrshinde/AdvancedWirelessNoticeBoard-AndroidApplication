package com.example.v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText mEmail = findViewById(R.id.rEmail);
        TextView resetBtn = findViewById(R.id.resetBtn);
        TextView backBtn = findViewById(R.id.backBtn);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        // Reset Password
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }

            private void validateData() {
                email = mEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    mEmail.setError("Required");
                }else {
                    forgotPass();
                }
            }

            private void forgotPass() {
                fAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(Register.this, "Check your email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }else {
                                Toast.makeText(Register.this, "Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Back to Login
        backBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),Login.class)));
    }
}
