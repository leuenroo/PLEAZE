package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    //set variables
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private EditText firstNameET, lastNameET, phoneET;
    private TextView emailTV;
    private Button updateProfileButton;
    private DocumentReference userRef;
    private String email, password, firstName, lastName, phone, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //set views
        emailTV = (TextView) findViewById(R.id.textViewEmail);
        firstNameET = (EditText) findViewById(R.id.editTextFirstName);
        lastNameET = (EditText) findViewById(R.id.editTextLastName);
        phoneET = (EditText) findViewById(R.id.editTextPhone);
        updateProfileButton = (Button) findViewById(R.id.updateProfileButton);

        //reference current user's document
        userRef = fStore.collection("users").document(userID);

        //set fields to appropriate values
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            //getting fields as strings from document and set them to views
                            firstName = documentSnapshot.getString("firstName");
                            firstNameET.setText(firstName);
                            lastName = documentSnapshot.getString("lastName");
                            lastNameET.setText(lastName);
                            email = documentSnapshot.getString("email");
                            emailTV.setText(email);
                            phone = documentSnapshot.getString("phone");
                            phoneET.setText(phone);

                        }
                        //if there is an error display to user
                        else {
                            Toast.makeText(ProfileActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                //if there is an error display to user
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                    }
                });




    }

    //function to update the users profile
    public void updateUser(View view) {


        //get the current values
        firstName = firstNameET.getText().toString();
        lastName = lastNameET.getText().toString();
        phone = phoneET.getText().toString();


        //put info in user object
        Map<String, Object> user = new HashMap<>();

        //put key/value pairs into user object
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("phone", phone);


        userRef = fStore.collection("users").document(userID);

        //check to make sure values are valid and set user object to user reference
        if (isValidEmail(email) && isValidName(firstName) && isValidName(lastName)) {
            userRef.set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        //if it works let them know it updated
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "User updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        //if it doesnt work let them know
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        //tell them to check fields again
        else {
            Toast.makeText(ProfileActivity.this, "Info validation error. Check all fields", Toast.LENGTH_SHORT).show();
        }



    }

    //check email
    public boolean isValidEmail(String e) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return e.matches(regex);
    }

    //check name
    public boolean isValidName(String n) {
        String regexName = "\\p{Upper}(\\p{Lower}+\\s?)";
        String patternName = "(" + regexName + "){1,2}";
        return n.matches(patternName);
    }

    //take user to main activity
    public void home (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}