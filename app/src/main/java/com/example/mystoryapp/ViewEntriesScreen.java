package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewEntriesScreen extends AppCompatActivity {

    // Message strings
    private static final String TAG = "MessageLogs";

    // Declarations for Firebase
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Entries will be displayed in this dynamic view
    RecyclerView recyclerViewEntries;

    // Entries from Firebase will be stored in this array
    ArrayList<EntryModel> entryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entries_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Access RecyclerView from layout
        recyclerViewEntries = findViewById(R.id.recyclerViewEntries);

        // Entries are stored into local ArrayList with readData() method
        // Show each entry in the dynamic RecyclerView
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(List<EntryModel> list) {
                // Create adapter passing in the sample user data
                CustomAdapter adapter = new CustomAdapter(entryList);
                // Attach the adapter to the recyclerview to populate items
                recyclerViewEntries.setAdapter(adapter);
                // Set layout manager to position the items
                recyclerViewEntries.setLayoutManager(new LinearLayoutManager(ViewEntriesScreen.this));
            }
        });
    }

    // Method to read the data from Firestore and store it into the local ArrayList
    private void readData(FirestoreCallback firestoreCallback) {
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

    // NEW ENTRY BTN
    public void clickNewEntry(View view) {
        // Open NewEntryScreen activity
        Intent intent = new Intent(this, NewEntryScreen.class);
        startActivity(intent);
    }
}

