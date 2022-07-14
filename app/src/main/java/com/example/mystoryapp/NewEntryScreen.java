package com.example.mystoryapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewEntryScreen extends AppCompatActivity {

    // Message strings
    private static final String TAG = "MessageLogs";

    // Firebase declarations
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access views in layout
    DatePickerDialog pickerDate;
    EditText editTextDate, editTextEntry;
    TextView textViewLocation;

    // For storing entry data
    EntryModel currentEntry = null;
    String selectedDate = "", entryId = "", selectedLocation = "";

    // For launching map activity
    ActivityResultLauncher<Intent> mapsActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Access views of layout
        editTextEntry = findViewById(R.id.editTextEntry);
        textViewLocation = findViewById(R.id.textViewLocation);
        editTextDate = findViewById(R.id.editTextDatePicker);
        editTextDate.setInputType(InputType.TYPE_NULL);

        // On click, open a calendar dialog
        // KNOWN ISSUE: it needs to be clicked twice to open the calendar popup.
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // To store the date selected
                Calendar chosenDate = Calendar.getInstance();
                int day = chosenDate.get(Calendar.DAY_OF_MONTH);
                int month = chosenDate.get(Calendar.MONTH);
                int year = chosenDate.get(Calendar.YEAR);

                // Calendar pop-up dialog
                pickerDate = new DatePickerDialog(NewEntryScreen.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Set the edit text as the selected date
                                selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                entryId = "";
                                editTextDate.setText(selectedDate);
                                // Check if an entry already exists on this date
                                checkEntryExists(selectedDate);
                            }
                        }, year, month, day);
                pickerDate.show();
            }
        });

        // If coming back after opening MapsActivity, get the selected location
        mapsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            selectedLocation = data.getStringExtra("locationValue");
                            textViewLocation.setText("Location: " + selectedLocation);
                        }
                    }
                });

        // If this activity was called to edit an existing entry, get the entry Id
        Intent intent = getIntent();
        if ((intent.getStringExtra(ViewCalendarScreen.EXTRA_MESSAGE)) != null){
            entryId = intent.getStringExtra(ViewCalendarScreen.EXTRA_MESSAGE);
        }

        // If there is an existing entry, pass the values into the views for editing
        if (entryId != "") {
            loadEntry(entryId);
            // Also disable editing of the date view, since the user is here to edit the entry only
            editTextDate.setEnabled(false);
            editTextDate.setTextColor(Color.parseColor("#BBB5DC"));
        }
    }

    // SAVE BTN
    public void clickSave(View view) {
        // Check if entry is not empty
        if (editTextEntry.getText().toString().trim().length() <= 0) {
            GeneralFunctions.showAlert(this, getResources().getString(R.string.errorNoEntry), Constants.AlertType.ALERT_CANCEL.getInt());
        } else if (editTextDate.getText().toString().trim().length() <= 0) {
            // Check if date is not empty
            GeneralFunctions.showAlert(this, getResources().getString(R.string.errorNoDate), Constants.AlertType.ALERT_CANCEL.getInt());
        } else {
            // Save or update the entry and return to main menu
            currentEntry = new EntryModel(selectedDate, editTextEntry.getText().toString(), selectedLocation);
            setDocument(currentEntry, user);

            // Success message
            GeneralFunctions.showAlert(this, getResources().getString(R.string.successSave), Constants.AlertType.ALERT_FINISH.getInt());
            editTextDate.getText().clear();
            editTextEntry.getText().clear();
        }
    }

    // Add or update entry details a subcollection in FireBase
    private void setDocument(EntryModel entry, FirebaseUser user){
        // Update if it's an existing entry
        if (entryId != "") {
            db.collection("users").document(user.getUid()).collection("entries").document(entryId)
                    .update("entry", entry.getEntry())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot of entry successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        } else {
            // Add a new document if it's a new entry
            Map<String, Object> data = new HashMap<>();
            data.put("date", entry.getDate());
            data.put("entry", entry.getEntry());
            data.put("location", selectedLocation);

            // Name the subcollection "entries" and use the date as the document's name
            db.collection("users").document(user.getUid()).collection("entries").document(entry.getDate())
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot of entry successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document of entry", e);
                        }
                    });
        }
    }

    // Method to check if an entry already exists at a selected date
    private void checkEntryExists(String date){
        // Access entries collection from Firebase
        db.collection("users").document(user.getUid()).collection("entries")
                .whereEqualTo("date", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // If an entry was found, save the entryId
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            entryId = document.getId();
                            Log.d(TAG, "Entry found successfully: " + entryId + " => " + document.getData());
                        }
                    }
                    // If entry was found, let the user know, and load it in
                    if (entryId != "") {
                        GeneralFunctions.showAlert(NewEntryScreen.this, getResources().getString(R.string.alertEntryAlreadyExists), Constants.AlertType.ALERT_CANCEL.getInt());
                        loadEntry(entryId);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Method to load an existing entry into the views
    private void loadEntry(String entryId){
        // Get() query to get the entry's data
        db.collection("users").document(user.getUid()).collection("entries").document(entryId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If get() query is successful, put the entry's data into the views
                        editTextDate.setText(document.get("date").toString());
                        editTextEntry.setText(document.get("entry").toString());
                        textViewLocation.setText("Location: " + document.get("location").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "Error: entry not found.");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // ADD LOCATION BTN
    public void clickAddLocation(View view){
        // Open MapsActivity activity
        Intent intent = new Intent(this, MapsActivity.class);
        mapsActivityResultLauncher.launch(intent);
    }
}