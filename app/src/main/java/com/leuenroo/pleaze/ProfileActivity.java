package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
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
    private TextView emailTV, creditTV;
    private Button updateProfileButton;
    private boolean parked;
    private DocumentReference userRef;
    private Account account;
    private double credit, addedCredit;
    private String email, firstName, lastName, phone, userID, creditString;

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
        creditTV = (TextView) findViewById(R.id.creditTextView);

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
                            credit = documentSnapshot.getDouble("credit");
                            creditString = "$" + String.format("%.2f",credit);
                            creditTV.setText(creditString);
                            parked = documentSnapshot.getBoolean("parked");

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

        //create account object with appropriate values
        account = new Account(firstName, lastName, phone, email, credit, null, parked);


        userRef = fStore.collection("users").document(userID);

        //check to make sure values are valid and set account object to user reference
        if (isValidEmail(email) && isValidName(firstName) && isValidName(lastName)) {
            userRef.set(account)
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

    //add credit function
    public void addCredit(View view) {
        //create the dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter amount");
        final EditText input = new EditText(this);
        //only allow numbers to be input
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        //set add and cancel buttons
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //if they click ok add the credit to their account
                //convert editText to double
                addedCredit = Double.parseDouble(input.getText().toString());
                //add it
                credit += addedCredit;
                //create string to update credit text view
                creditString = "$" + String.format("%.2f",credit);
                creditTV.setText(creditString);
                //update it in firestore
                userRef = fStore.collection("users").document(userID);
                userRef.update("credit", credit);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //nothing
            }
        });
        alert.show();
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