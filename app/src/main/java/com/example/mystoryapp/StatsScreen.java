package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StatsScreen extends AppCompatActivity {

    // Message strings
    private static final String TAG = "MessageLogs";

    // Declarations for Firebase
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access views from layout
    TextView textViewTotalEntries, textViewLocationsRecorded, textViewImagesUploaded;

    // Entries from Firebase will be stored in this array
    ArrayList<EntryModel> entryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Access views from layout
        textViewTotalEntries = findViewById(R.id.textViewTotalEntries);
        textViewLocationsRecorded = findViewById(R.id.textViewLocationsRecorded);
        textViewImagesUploaded = findViewById(R.id.textViewImagesUploaded);

        // Get entry info
        readData(new ViewEntriesScreen.FirestoreCallback() {
            @Override
            public void onCallback(List<EntryModel> list) {
                // Get number of total entries
                textViewTotalEntries.setText("Total entries: " + entryList.size());

                // Count unique locations and display it on the textview
                HashSet uniqueLocations = new HashSet();
                for (int i = 0; i < entryList.size(); i++){
                    uniqueLocations.add(entryList.get(i).getLocation());
                }
                textViewLocationsRecorded.setText("Total locations recorded: " + uniqueLocations.size());

                // Count unique images and display it on the textview (unavailable)
                /* HashSet uniqueImages = new HashSet();
                for (int i = 0; i < entryList.size(); i++){
                    uniqueLocations.add(entryList.get(i).getLocation());
                }
                textViewImagesUploaded.setText("Total images uploaded: " + uniqueImages.size()); */

                textViewImagesUploaded.setText("Total images uploaded: (unavailable)");
            }
        });
    }

    // Method to read the data from Firestore and store it into the local ArrayList
    private void readData(ViewEntriesScreen.FirestoreCallback firestoreCallback) {
        // Access entry collection
        db.collection("users").document(user.getUid()).collection("entries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Cast document into an EntryModel instance
                                EntryModel entry = document.toObject(EntryModel.class);
                                entryList.add(entry);
                            }
                            // To use the entryList outside of onComplete, do onCallBack
                            firestoreCallback.onCallback(entryList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // Needed for the above method so we can use the ArrayList outside of onComplete()
    public interface FirestoreCallback {
        void onCallback(List<EntryModel> list);
    }
}