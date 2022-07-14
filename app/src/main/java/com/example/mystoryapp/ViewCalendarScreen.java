package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewCalendarScreen extends AppCompatActivity {

    // Message strings
    public static final String EXTRA_MESSAGE = "com.example.mystoryapp.MESSAGE";
    private static final String TAG = "MessageLogs";

    // Firebase declarations
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access views from layout
    CalendarView calendar;

    // For storing selected entry
    String selectedDate, entryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Access calendar view from layout
        calendar = findViewById(R.id.calendarView);

        // Use the calendar to check if there's an entry for the selected date
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Store the selected date as a string
                selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;

                // Query to search if entry exists on the selected date
                db.collection("users").document(user.getUid()).collection("entries")
                    .whereEqualTo("date", selectedDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                // If an entry was found, save the entryId
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    entryId = document.getId();
                                    Log.d(TAG, "Entry found successfully: " + entryId + " => " + document.getData());
                                }

                                // If entry was found, open it, otherwise show a message
                                if (entryId != "") {
                                    openEntry();
                                } else {
                                    GeneralFunctions.showAlert(ViewCalendarScreen.this, getResources().getString(R.string.errorEntryNotFound), Constants.AlertType.ALERT_CANCEL.getInt());
                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            }
        });
    }

    // SEE ALL ENTRIES BTN
    public void clickSeeAllEntries(View view) {
        // Open ViewEntriesScreen activity
        Intent intent = new Intent(this,ViewEntriesScreen.class);
        startActivity(intent);
    }

    // Method to open the entry on the selected date
    public void openEntry() {
        // Open ReadEntryScreen activity and give entry date
        Intent intent = new Intent(this, ReadEntryScreen.class);
        intent.putExtra(EXTRA_MESSAGE, entryId);
        startActivity(intent);
    }
}