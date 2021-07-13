package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    //variables
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private EditText emailET, passwordET, firstNameET, lastNameET, phoneET;
    private Button createAccountButton;
    private String email, password, firstName, lastName, phone, userID;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //set views
        emailET = (EditText) findViewById(R.id.editTextEmail);
        passwordET = (EditText) findViewById(R.id.editTextPassword);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);
        firstNameET = (EditText) findViewById(R.id.editTextFirstName);
        lastNameET = (EditText) findViewById(R.id.editTextLastName);
        phoneET = (EditText) findViewById(R.id.editTextPhone);

        //get firebase and firestore instances
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //set create account button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get values from fields
                email = emailET.getText().toString().trim();
                password = passwordET.getText().toString().trim();
                firstName = firstNameET.getText().toString();
                lastName = lastNameET.getText().toString();
                phone = phoneET.getText().toString();

                //check fields
                if (isValidEmail(email) && isValidName(firstName) && isValidName(lastName)) {
                    createAccount();
                }


                else {
                    Toast.makeText(RegisterActivity.this, "Error with fields", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    //create account function
    public void createAccount() {
        //create user in firebase auth
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if successful add info to firestore
                            Log.d(TAG, "createUserWithEmail:success");
                            userID = fAuth.getCurrentUser().getUid();
                            //create account object with appropriate values
                            account = new Account(firstName, lastName, phone, email);
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            //set user to firestore docref
                            documentReference.set(account).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            });
                            FirebaseUser fUser = fAuth.getCurrentUser();
                        } else {
                            // if sign up fails notify user
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
}
