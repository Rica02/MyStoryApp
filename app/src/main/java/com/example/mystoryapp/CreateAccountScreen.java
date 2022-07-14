package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountScreen extends AppCompatActivity {

    // Message strings
    public static final String EXTRA_MESSAGE = "com.example.mystoryapp.MESSAGE";
    private static final String TAG = "MessageLogs";

    // Firebase declarations
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access views of layout
    private EditText editTextCreateUsername, editTextCreateEmail, editTextCreatePassword, editTextCreateConfirm;
    private TextView textViewValidationError;

    // For storing account details
    private String username, email, password, confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);

        // Access views of layout
        editTextCreateUsername = findViewById(R.id.editTextCreateUsername);
        editTextCreateEmail = findViewById(R.id.editTextCreateEmail);
        editTextCreatePassword = findViewById(R.id.editTextCreatePassword);
        editTextCreateConfirm = findViewById(R.id.editTextCreateConfirm);
        textViewValidationError = findViewById(R.id.textViewValidationError);

        // Hide error label
        textViewValidationError.setVisibility(View.GONE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // REGISTER BTN
    public void clickRegister(View view){
        // Get details
        email = editTextCreateEmail.getText().toString();
        password = editTextCreatePassword.getText().toString();
        username = editTextCreateUsername.getText().toString();
        confirm = editTextCreateConfirm.getText().toString();

        // Check if any fields were left empty
        if(editTextCreateEmail.getText().toString().trim().length() <= 0 || editTextCreatePassword.getText().toString().trim().length() <= 0 || editTextCreateConfirm.getText().toString().trim().length() <= 0) {
            // If so, show error label
            textViewValidationError.setVisibility(View.VISIBLE);
            textViewValidationError.setText(getResources().getString(R.string.errorFillIn));
          // Check if passwords are matching
        } else if (!editTextCreatePassword.getText().toString().equals(editTextCreateConfirm.getText().toString())){
            // If not, show error label
            textViewValidationError.setVisibility(View.VISIBLE);
            textViewValidationError.setText(getResources().getString(R.string.errorPasswordNotMatching));
        } else {
            // If all good, proceed with account creation
            createAccount(email, password);
        }
    }

    // Register account process with Firebase
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If successful sign up, login to main menu
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Also set the username (displayName)
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            // Put the user in another separate collection which will be used for managing entries
                            setDocument(user);

                            // Let the user know the account was successfully created
                            GeneralFunctions.showAlert(CreateAccountScreen.this, getResources().getString(R.string.successRegister), Constants.AlertType.ALERT_CANCEL.getInt());
                            // Updated interface (login)
                            updateUI(user);
                        } else {
                            // If failed sign up, show error label
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            textViewValidationError.setVisibility(View.VISIBLE);
                            textViewValidationError.setText(getResources().getString(R.string.errorEmailPasswordValidation));
                            updateUI(null);
                        }
                    }
                });
    }

    // Update interface after authentication (user NOT null)
    private void updateUI(FirebaseUser user) {
        if(user != null){
            Intent intent = new Intent(this, MainMenuScreen.class);
            intent.putExtra(EXTRA_MESSAGE, email);
            startActivity(intent);
        }
    }

    // Method to create a collection of users in FireBase
    private void setDocument(FirebaseUser user){
        // Put email address in for easier identification
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());

        // Put user's unique UID in the collection
        db.collection("users").document(user.getUid())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot of user successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document of user", e);
                    }
                });
    }
}