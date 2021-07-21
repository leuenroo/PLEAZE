package com.leuenroo.pleaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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


    private EditText date_time_in;
    private String dateString, userID;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DocumentReference userRef;
    private Boolean datePicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        datePicked = false;

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        userRef = fStore.collection("users").document(userID);

        date_time_in=findViewById(R.id.date_time_input);

        date_time_in.setInputType(InputType.TYPE_NULL);

        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReservation(date_time_in);
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

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));

                        dateString = date_time_in.getText().toString();
                        dateString += ":00";

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

    //update
    public void update (View view) {
        if (datePicked == true) {
            userRef.update("reservation", dateString);

            Toast.makeText(ReservationActivity.this, "Reservation set.", Toast.LENGTH_LONG).show();
        }

        else {
            Toast.makeText(ReservationActivity.this, "Please pick a date.", Toast.LENGTH_LONG).show();
        }
    }






}