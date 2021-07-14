package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    private ParkingLot parkingLot;
    private Boolean[] spotArray;
    private List<Boolean> spotList;
    private int totalSpots, currentSpot, availableSpots;
    private double credit;
    private String userID, startTime;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private DocumentReference lotRef, userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get instances of firebase auth and firestore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //grab current users ID and reference that user
        if (fAuth.getCurrentUser().getUid() != null) {
            userID = fAuth.getCurrentUser().getUid();
        }
        userRef = fStore.collection("users").document(userID);

        //create reference for parking lot
        lotRef = fStore.collection("parkingLots").document("parkingLot");
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
                        }
                        //if there is an error display to user
                        else {
                            Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //if there is an error display to user
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                    }
                });

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //getting fields as strings from document
                            startTime = documentSnapshot.getString("lastSession");
                            credit = documentSnapshot.getDouble("credit");
                        }
                        //if there is an error display to user
                        else {
                            Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //if there is an error display to user
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //log user out
    public void logout (View view) {
        //sign out from firebase
        FirebaseAuth.getInstance().signOut();
        //go to login activity
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    //bs function to create parking lot document in firestore, keeping just in case
/*    public void createParkingLot() {
        spotArray = new Boolean[50];
        Arrays.fill(spotArray, true);
        List<Boolean> spotList = Arrays.asList(spotArray);
        parkingLot = new ParkingLot(50, 50, spotList);

        DocumentReference documentReference = fStore.collection("parkingLots").document("parkingLot");
        //set lot to firestore docref
        documentReference.set(parkingLot).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Lot created.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


    }*/

    //park function
    public void park (View view) {
        //get info from parking lot
        lotRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            //set info to variables
                            availableSpots = documentSnapshot.getLong("availableSpots").intValue();
                            totalSpots = documentSnapshot.getLong("totalSpots").intValue();
                            spotList = (List<Boolean>) documentSnapshot.get("spots");

                            //check for lowest available spot
                            for (int currentSpot = 0; currentSpot < spotList.size(); currentSpot++) {
                                if (spotList.get(currentSpot) == true) {
                                    Toast.makeText(MainActivity.this, "You are given spot " + (currentSpot + 1), Toast.LENGTH_LONG).show();
                                    //set the given spot to unavailable
                                    spotList.set(currentSpot, false);

                                    //update lot
                                    lotRef.update("spots", spotList);
                                    lotRef.update("availableSpots", availableSpots - 1);
                                    //get current time
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    String currentTime = simpleDateFormat.format(timestamp);
                                    double rate = 1.0;
                                    //start new session
                                    Session session = new Session(userID, currentTime, (currentSpot + 1), rate);
                                    //update users lastSession and parked fields
                                    userRef.update("lastSession", currentTime);
                                    userRef.update("parked", true);

                                    //set session to sessions collection using userID and start time
                                    DocumentReference sessionRef = fStore.collection("sessions").document(userID + currentTime);
                                    sessionRef.set(session);
                                    //break out of loop when spot is given and set
                                    break;
                                }
                            }

                        }
                        //if there is an error display to user
                        else {
                            Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                //if there is an error display to user
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                    }
                });





    }

    //unpark function
    public void unpark (View view) {

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //get users last session start time
                            startTime = documentSnapshot.getString("lastSession");
                            //reference session using user id and start time
                            DocumentReference sessionRef = fStore.collection("sessions").document(userID + startTime);

                            //access session document
                            //inside if statement for user document because code was running synchronously when i put it after
                            sessionRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                //get spot number from session document
                                                currentSpot = documentSnapshot.getLong("spotNumber").intValue();
                                                startTime = documentSnapshot.getString("startTime");
                                                //get current time
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                String currentTime = simpleDateFormat.format(timestamp);

                                                // charge by calling find difference function using start and current time
                                                double rate = 1.0;
                                                double timeDifference = findDifference(startTime, currentTime);
                                                //multiply time in minutes by rate
                                                double total = rate * timeDifference;
                                                //create new session instance
                                                Session session = new Session(userID, startTime, currentTime, (currentSpot + 1), rate, total);
                                                //set session to firestore
                                                sessionRef.set(session);
                                                //create credit variable and subtract total
                                                double newCredit = credit - total;
                                                //get info from parking lot
                                                lotRef.get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot.exists()) {

                                                                    //set variables to fields
                                                                    availableSpots = documentSnapshot.getLong("availableSpots").intValue();
                                                                    totalSpots = documentSnapshot.getLong("totalSpots").intValue();
                                                                    spotList = (List<Boolean>) documentSnapshot.get("spots");

                                                                    sessionRef.get()
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                    if (documentSnapshot.exists()) {
                                                                                        //update spot so that it becomes available
                                                                                        currentSpot = documentSnapshot.getLong("spotNumber").intValue();
                                                                                        currentSpot = currentSpot - 2;
                                                                                        spotList.set(currentSpot, true);
                                                                                        Toast.makeText(MainActivity.this, "Current spot is " + currentSpot, Toast.LENGTH_LONG).show();

                                                                                        //update fields
                                                                                        lotRef.update("availableSpots", availableSpots + 1);
                                                                                        lotRef.update("spots", spotList);
                                                                                        //update users credit and parked fields
                                                                                        userRef.update("credit", newCredit);
                                                                                        userRef.update("parked", false);

                                                                                    }
                                                                                    //if there is an error display to user
                                                                                    else {
                                                                                        Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                }
                                                                            });

                                                                }
                                                                //if there is an error display to user
                                                                else {
                                                                    Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        })
                                                        //if there is an error display to user
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });




                                            }
                                            //if there is an error display to user
                                            else {
                                                Toast.makeText(MainActivity.this, "Error loading document 1.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    //if there is an error display to user
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Error loading document 2.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        //if there is an error display to user
                        else {
                            Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                //if there is an error display to user
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error loading document.", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //take user to profile activity
    public void profile (View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }
    public void availability (View view) {
        startActivity(new Intent(getApplicationContext(), AvailabilityActivity.class));
    }
    public void reservation (View view) {
        startActivity(new Intent(getApplicationContext(), ReservationActivity.class));
    }

    // function to find difference for total calculation
    static double findDifference(String start_date,
                               String end_date)
    {
        // format to make date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        long difference_In_Minutes = 0;


        try {

            // parse date from date string
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // calculate difference in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            //calculate difference in minutes
            difference_In_Minutes
                    = TimeUnit
                    .MILLISECONDS
                    .toMinutes(difference_In_Time)
                    % 60;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        //cast time to double for firestore
        double time = (double)difference_In_Minutes;
        //return difference
        return time;
    }

}