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
    private int totalSpots, currentSpot, availableSpots, premiumSpots;
    private double credit;
    private String userID, startTime, date, reservation;
    private boolean parked, premium;
    private Button parkButton, unparkButton, premiumButton;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private DocumentReference lotRef, userRef, reservationRef;


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
        //set views
        parkButton = findViewById(R.id.parkButton);
        unparkButton = findViewById(R.id.unparkButton);
        premiumButton = findViewById(R.id.premiumButton);

        //get date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        date = simpleDateFormat.format(timestamp);

        //set buttons invisible by default
        parkButton.setVisibility(View.GONE);
        unparkButton.setVisibility(View.GONE);
        premiumButton.setVisibility(View.GONE);

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
                            premiumSpots = documentSnapshot.getLong("premiumSpots").intValue();
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
                            reservation = documentSnapshot.getString("reservation");
                            parked = documentSnapshot.getBoolean("parked");
                            credit = documentSnapshot.getDouble("credit");

                            //if user is parked, show unpark button. if unparked, show park button
                            if (parked == true) {
                                unparkButton.setVisibility(View.VISIBLE);
                            }
                            else {
                                parkButton.setVisibility(View.VISIBLE);
                                premiumButton.setVisibility(View.VISIBLE);
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

        if (credit > 0 ) {
            lotRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                //set info to variables
                                availableSpots = documentSnapshot.getLong("availableSpots").intValue();
                                totalSpots = documentSnapshot.getLong("totalSpots").intValue();
                                spotList = (List<Boolean>) documentSnapshot.get("spots");

                                //check for lowest available non-premium spot
                                for (currentSpot = premiumSpots; currentSpot < spotList.size(); currentSpot++) {

                                    if (spotList.get(currentSpot) == true ) {
                                        premium = false;
                                        //update lot
                                        parkUpdate();
                                        //break out of loop when spot is given and set
                                        break;
                                    }

                                    //if current spot is last spot, let them know they cannot park
                                    else if (currentSpot == (spotList.size() - 1)) {
                                        Toast.makeText(MainActivity.this, "No spots available. Please try again later.", Toast.LENGTH_LONG).show();
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

        else {
            Toast.makeText(MainActivity.this, "Please add funds to your account.", Toast.LENGTH_LONG).show();
        }

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
                            credit = documentSnapshot.getDouble("credit");
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
                                                premium = documentSnapshot.getBoolean("premium");
                                                //get current time
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                String currentTime = simpleDateFormat.format(timestamp);

                                                double rate = 0;
                                                // charge by calling find difference function using start and current time
                                                if (premium = true) {
                                                    rate = .08;
                                                }
                                                else {
                                                    rate = .05;
                                                }
                                                double timeDifference = findDifference(startTime, currentTime);
                                                //multiply time in minutes by rate
                                                double total = rate * timeDifference;
                                                //create new session instance
                                                Session session = new Session(userID, startTime, currentTime, (currentSpot + 1), rate, total, premium);
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
                                                                                        Toast.makeText(MainActivity.this, "You have been charged " + total + ". Thank you for parking with PLeaze!", Toast.LENGTH_LONG).show();

                                                                                        //update fields
                                                                                        lotRef.update("availableSpots", availableSpots + 1);
                                                                                        lotRef.update("spots", spotList);
                                                                                        //update users credit and parked fields
                                                                                        userRef.update("credit", newCredit);
                                                                                        userRef.update("parked", false);

                                                                                        parkButton.setVisibility(View.VISIBLE);
                                                                                        premiumButton.setVisibility(View.VISIBLE);
                                                                                        unparkButton.setVisibility(View.GONE);

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

    public void parkPremium (View view) {
        //get info from parking lot

        if (credit > 0) {
            lotRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                //set info to variables
                                availableSpots = documentSnapshot.getLong("availableSpots").intValue();
                                totalSpots = documentSnapshot.getLong("totalSpots").intValue();
                                spotList = (List<Boolean>) documentSnapshot.get("spots");

                                //check for lowest available premium spot
                                for (currentSpot = 0; currentSpot < premiumSpots; currentSpot++) {

                                    if (spotList.get(currentSpot) == true ) {
                                        premium = true;
                                        //update lot
                                        parkUpdate();
                                        //break out of loop when spot is given and set
                                        break;
                                    }

                                    //if current spot is last spot, let them know they cannot park
                                    else if (currentSpot == (premiumSpots - 1)) {
                                        Toast.makeText(MainActivity.this, "No premium spots available. Please try again later.", Toast.LENGTH_LONG).show();
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

        else {
            Toast.makeText(MainActivity.this, "Please add funds to your account.", Toast.LENGTH_LONG).show();
        }

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

    public void parkUpdate () {
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
        Session session = new Session(userID, currentTime, (currentSpot + 1), rate, premium);
        //update users lastSession and parked fields
        userRef.update("lastSession", currentTime);
        userRef.update("parked", true);

        parkButton.setVisibility(View.GONE);
        premiumButton.setVisibility(View.GONE);
        unparkButton.setVisibility(View.VISIBLE);

        //set session to sessions collection using userID and start time
        DocumentReference sessionRef = fStore.collection("sessions").document(userID + currentTime);
        sessionRef.set(session);
    }

}