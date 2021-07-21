package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AvailabilityActivity extends AppCompatActivity {

    private String availabilityString;
    private String [] availabilityStrings;
    private TextView availabilityTV;
    private ParkingLot parkingLot;
    private List<Boolean> spotList;
    private int totalSpots, availableSpots;
    private Button refreshButton;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private DocumentReference lotRef, userRef;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        gridView = findViewById(R.id.gridView);
        availabilityTV = findViewById(R.id.availabilityTextView);
        refreshButton = findViewById(R.id.refreshButton);

        //get instances of firebase auth and firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        lotRef = fStore.collection("parkingLots").document("parkingLot");

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printLot();
            }
        });

        loadLot();
    }

    public void loadLot() {
        //get fields from said lot
        lotRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //set fields to variables
                            availableSpots = documentSnapshot.getLong("availableSpots").intValue();
                            totalSpots = documentSnapshot.getLong("totalSpots").intValue();
                            spotList = (List<Boolean>) documentSnapshot.get("spots");
                            parkingLot = new ParkingLot(totalSpots, availableSpots, spotList);
                            printLot();
                        }
                        //if there is an error display to user
                        else {
                            Toast.makeText(AvailabilityActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //if there is an error display to user
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AvailabilityActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //create a lot of Xs and Os to show which spots are available
    public void printLot() {
        String lotString = "Spots ";
        int last = 0;
        //create strings to show Xs and Os
        availabilityStrings = new String[parkingLot.spots.size()];

        for (int i = 0; i < parkingLot.spots.size(); i++) {
            if (parkingLot.spots.get(i) == true) {
                last = i;
                availabilityStrings[i] = "O";
            }
            else {
                availabilityStrings[i] = "X";
            }
        }

        //add available spots to the lot string
        for (int i = 0; i < parkingLot.spots.size(); i++) {
            if (parkingLot.spots.get(i) == true) {
                if (i == last) {
                    lotString += " and " + (i+1);
                }

                else {
                    lotString += (i + 1) + ", ";
                }
            }
        }

        lotString += " are available.";
        availabilityString = availableSpots + " spots available.";

        Toast.makeText(AvailabilityActivity.this, lotString, Toast.LENGTH_LONG).show();

        //adapt string array and set it to grid
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AvailabilityActivity.this, android.R.layout.simple_list_item_1, availabilityStrings);
        gridView.setAdapter(adapter);
        availabilityTV.setText(availabilityString);
    }

    //take user to main activity
    public void home (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}

