package com.leuenroo.pleaze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set views
        logoutButton = (Button) findViewById(R.id.logoutButton);
        profileButton = (Button) findViewById(R.id.profileButton);






    }

    //log user out
    public void logout (View view) {
        //sign out from firebase
        FirebaseAuth.getInstance().signOut();
        //go to login activity
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    //take user to profile activity
    public void profile (View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }
}