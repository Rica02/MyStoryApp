package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileScreen extends AppCompatActivity {

    // Firebase declarations
    private static final String TAG = "MessageLogs";
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // For alert dialogs
    AlertDialog.Builder builder;

    // Access views in layout
    TextView textViewUsername, textViewProfileUsername, textViewProfilePassword, textViewProfileEmail;
    Button buttonUpdateUsername, buttonUpdatePassword, buttonUpdateEmail;

    // String to store updated details
    private String newString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // For alert dialogs
        builder = new AlertDialog.Builder(this);

        // Access views in layout
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewProfileUsername = findViewById(R.id.textViewProfileUsername);
        textViewProfilePassword = findViewById(R.id.textViewProfilePassword);
        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        buttonUpdateUsername = findViewById(R.id.buttonUpdateUsername);
        buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);
        buttonUpdateEmail = findViewById(R.id.buttonUpdateEmail);

        // Display CURRENT user details
        textViewUsername.setText(user.getDisplayName());
        textViewProfileUsername.setText("USERNAME\n" + user.getDisplayName());
        textViewProfilePassword.setText("PASSWORD\n******");
        textViewProfileEmail.setText("EMAIL\n" + user.getEmail());
    }

    // UPDATE USERNAME BTN
    public void clickUpdateUsername(View view){
        // Reset newString
        newString = "";

        // Set input type of the dialog's EditText
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set alert title and view
        builder.setTitle("Type a new username");
        builder.setView(input);

        // Set up OK btn
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Store input into newString
                newString = input.getText().toString();
                if (newString != ""){

                    // Update user's username (DisplayName)
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newString)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                        // Tell the user the operation was successful
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.successUsernameUpdate),Toast.LENGTH_SHORT).show();

                                        // Update TextViews with username
                                        textViewUsername.setText(newString);
                                        textViewProfileUsername.setText("USERNAME\n" + newString);
                                    }
                                }
                            });
                } else {
                    // If input was left empty just close the dialog
                    dialog.cancel();
                }
            }
        });
        // Set up Cancel btn
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close dialog
                dialog.cancel();
            }
        });
        // Show interface
        builder.show();
    }

    // UPDATE PASSWORD BTN
    public void clickUpdatePassword(View view){
        // Reset newString
        newString = "";

        // Set input type of the dialog's EditText
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Set alert title and view
        builder.setTitle("Type a new password");
        builder.setView(input);

        // Set up OK btn
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Store input into newString
                newString = input.getText().toString();
                if (newString != ""){
                    // If input is not empty update password
                    user.updatePassword(newString)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User password updated.");
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.successPasswordUpdate),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // If input was left empty just close the dialog
                    dialog.cancel();
                }
            }
        });
        // Set up Cancel btn
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close dialog
                dialog.cancel();
            }
        });
        // Show interface
        builder.show();
    }

    // UPDATE EMAIL BTN
    public void clickUpdateEmail(View view){
        // Reset newString
        newString = "";

        // Set input type of the dialog's EditText
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set alert title and view
        builder.setTitle("Type a new email");
        builder.setView(input);

        // Set up OK btn
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Store input into newString
                newString = input.getText().toString();
                if (newString != ""){
                    // If input is not empty update email
                    user.updateEmail(newString)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                        // Tell the user the operation was successful
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.successEmailUpdate),Toast.LENGTH_SHORT).show();  }
                                }
                            });
                    // Also update the email in the user collection
                    db.collection("users").document(user.getUid())
                            .update("email", newString)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot of user successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                } else {
                    // If input was left empty just close the dialog
                    dialog.cancel();
                }
            }
        });
        // Set up Cancel btn
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close dialog
                dialog.cancel();
            }
        });
        // Show interface
        builder.show();
    }
}