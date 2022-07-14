package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReadEntryScreen extends AppCompatActivity {

    // Message strings
    public static final String EXTRA_MESSAGE = "com.example.mystoryapp.MESSAGE";
    private static final String TAG = "MessageLogs";

    // Firebase declarations
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access views in layout
    TextView textViewReadDate, textViewReadLocation, textViewReadEntry;

    // For storing selected entry's date
    String entryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_entry_screen);

        // Get the Intent that started this activity and extract the entryId passed
        Intent intent = getIntent();
        entryId = intent.getStringExtra(ViewCalendarScreen.EXTRA_MESSAGE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Access views in layout
        textViewReadDate = findViewById(R.id.textViewReadDate);
        textViewReadLocation = findViewById(R.id.textViewReadLocation);
        textViewReadEntry = findViewById(R.id.textViewReadEntry);

        // Pass the entry's details into the views
        if (entryId != "") {
            // Get() query to get the entry's data
            db.collection("users").document(user.getUid()).collection("entries").document(entryId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            // If get() query is successful, put the entry's data into the views
                            textViewReadDate.setText(document.get("date").toString());
                            textViewReadEntry.setText(document.get("entry").toString());
                            textViewReadLocation.setText(document.get("location").toString());

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "Error: entry not found.");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Log.d(TAG, "Error in retrieving the entry.");
        }
    }

    // EDIT BTN
    public void clickEdit(View view) {
        // Open NewEntryScreen activity with entry's date
        Intent intent = new Intent(this, NewEntryScreen.class);
        intent.putExtra(EXTRA_MESSAGE, entryId);
        startActivity(intent);
    }

    // DELETE BTN
    public void clickDelete(View view){
        deleteDocument(entryId, user);
    }

    // Method to delete an entry
    public void deleteDocument(String entryId, FirebaseUser user){
        // Access document in collection
        db.collection("users").document(user.getUid()).collection("entries").document(entryId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        // Notify the user of successful deletion
                        GeneralFunctions.showAlert(ReadEntryScreen.this, getResources().getString(R.string.successEntryDelete), Constants.AlertType.ALERT_FINISH.getInt());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}