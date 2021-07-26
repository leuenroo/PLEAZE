package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReservationActivity extends AppCompatActivity {


    private EditText dateTimeInput;
    private String dateAndTime, date, time, userID;
    private int spotNumber;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DocumentReference userRef, reservationRef;
    private Boolean datePicked;
    private Reservation reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        datePicked = false;

        //get instances
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        userRef = fStore.collection("users").document(userID);

        //set up date time input
        dateTimeInput = findViewById(R.id.dateTimeInput);

        dateTimeInput.setInputType(InputType.TYPE_NULL);

        dateTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReservation(dateTimeInput);
            }
        });

    }

    //show user a date picker and create a reservation on that date/time
    private void getReservation(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                //date picker
                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        //create different date formats to separate time differently
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat simpleDateFormat3=new SimpleDateFormat("HH:mm");

                        dateTimeInput.setText(simpleDateFormat.format(calendar.getTime()));

                        dateAndTime = dateTimeInput.getText().toString();
                        dateAndTime += ":00";

                        //get date and time separately
                        date = simpleDateFormat2.format(calendar.getTime());
                        time = simpleDateFormat3.format(calendar.getTime());

                        //user has picked date
                        datePicked = true;
                    }
                };

                new TimePickerDialog(ReservationActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(ReservationActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    //take user to main activity
    public void home (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //set reservation
    public void setReservation (View view) {
        //make sure date and time have been picked
        if (datePicked == true) {

            //create the dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Enter desired spot number");
            final EditText input = new EditText(this);
            //only allow numbers to be input
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            alert.setView(input);
            //set add and cancel buttons
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //if they click ok add the credit to their account
                    //convert editText to int
                    spotNumber = Integer.parseInt(input.getText().toString());
                    reservation = new Reservation(userID, date, time, spotNumber, false);
                    reservationRef = fStore.collection("reservations").document(date + spotNumber);
                    userRef.update("reservation", dateAndTime);
                    reservationRef.set(reservation);

                    Toast.makeText(ReservationActivity.this, "Reservation set.", Toast.LENGTH_LONG).show();

                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //nothing
                }
            });
            alert.show();

        }

        //if date and time arent picked, ask user for them
        else {
            Toast.makeText(ReservationActivity.this, "Please pick a date.", Toast.LENGTH_LONG).show();
        }
    }

}