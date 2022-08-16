package com.example.v2;

import static android.widget.Toast.LENGTH_SHORT;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.v2.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.*;
import java.util.HashMap;
import java.util.Map;

// Connecting activities
public class MainActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.ding);
        final MediaPlayer mediaPlayer1 = MediaPlayer.create(MainActivity.this,R.raw.stab);

        // Connecting objects of fields with xml using findViewByID function
        // Creating objects of fields
        EditText enter = findViewById(R.id.Enter);
        TextView send = findViewById(R.id.sendBtn);

        // onClickListener for send button
        send.setOnClickListener(v -> {
            if (TextUtils.isEmpty(enter.getText().toString()))
            {
                Toast.makeText(MainActivity.this,
                        "Empty field not allowed!",
                        LENGTH_SHORT).show();
                mediaPlayer1.start();
            } else {

                String text = enter.getText().toString();
                Intent mintent;
                mintent = getIntent();
                String uname = mintent.getStringExtra(Login.VSEND);

                // Using Hashmap to store data in Firestore
                long dt = System.currentTimeMillis();
                Map<String, Object> user = new HashMap<>();

                // Using put function to store data in Firestore
                user.put("Text", text);
                user.put("uid", uname);
                user.put("DT", dt);
                enter.getText().clear();

                db.collection("data").document()
                        .set(user)
                        .addOnSuccessListener(documentReference -> Toast.makeText(getApplicationContext(), "Successful", LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Unsuccessful", LENGTH_SHORT).show());
                mediaPlayer.start();
            }
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

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}