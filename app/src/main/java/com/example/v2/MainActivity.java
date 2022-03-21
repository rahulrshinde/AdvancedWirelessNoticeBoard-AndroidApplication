package com.example.v2;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.*;
import java.util.HashMap;
import java.util.Map;

// Connecting activities
public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting objects of fields with xml using findViewByID function
        // Creating objects of fields
        EditText enter = findViewById(R.id.Enter);
        TextView send = findViewById(R.id.sendBtn);

        // onClickListener for send button
        send.setOnClickListener(v -> {
            String text = enter.getText().toString();
            Intent mintent;
            mintent = getIntent();
            String uname = mintent.getStringExtra(Login.VSEND);

            // Using Hashmap to store data in Firestore
            long dt = System.currentTimeMillis();
            Map<String ,Object> user =new HashMap<>();

            // Using put function to store data in Firestore
            user.put("Text", text);
            user.put("uid", uname);
            user.put("DT", dt);
            enter.getText().clear();

            db.collection("users").document()
                    .set(user)
                    .addOnSuccessListener(documentReference -> Toast.makeText(getApplicationContext(),"Successful", LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),"Unsuccessful", LENGTH_SHORT).show());
        });

        // Logout Button
        ImageButton logout = findViewById(R.id.signOut);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent mainActivity = new Intent(MainActivity.this, Login.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainActivity);
            finish();
        });
    }

    // Back Button
    @Override
    public void onBackPressed() {
        Toast.makeText(MainActivity.this, "Please Click on Logout Button", LENGTH_SHORT).show();
    }
}