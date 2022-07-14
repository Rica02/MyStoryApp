package com.example.mystoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginScreen extends AppCompatActivity {

    // Message strings
    private static final String TAG = "MessageLogs";
    private static final int RC_SIGN_IN = 9001;
    private static final String EMAIL = "email";

    // Firebase declaration
    private FirebaseAuth mAuth;

    // Google Sign In declarations
    GoogleSignInClient mGoogleSignInClient;
    SignInButton googleSignInBtn;

    // Facebook Sign in declarations
    LoginButton facebookSignInBtn;
    private CallbackManager mCallbackManager;

    // Access views in layout
    EditText editTextUsername, editTextPassword;

    // To store user inputs
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Access views in layout
        editTextUsername = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Google Sign In btn
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        googleSignInBtn.setSize(SignInButton.SIZE_STANDARD);

        // Set Google Sign in onClick
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.googleSignInBtn:
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        break;
                }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        facebookSignInBtn = findViewById(R.id.facebookSignInBtn);
        facebookSignInBtn.setPermissions("email", "public_profile");
        facebookSignInBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // NOTE: only test users I added in my Facebook developer page can log in with Facebook
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                // If login successful, register the Facebook user in Firebase authentication
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    // LOGIN BTN
    public void clickLogin(View view) {

        // Store user input into strings
        email = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        // Check if fields were left empty, alert the user if so
        if (editTextUsername.getText().toString().trim().length() <= 0 || editTextPassword.getText().toString().trim().length() <= 0) {
            GeneralFunctions.showAlert(this, getResources().getString(R.string.errorFillIn), Constants.AlertType.ALERT_CANCEL.getInt());
        } else {
            // Authentication with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If successful sign in, open Main Menu activity
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If failed sign in, display error message
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                GeneralFunctions.showAlert(LoginScreen.this, getResources().getString(R.string.errorWrongCredentials), Constants.AlertType.ALERT_CANCEL.getInt());
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    // Update interface after authentication
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainMenuScreen.class);
            startActivity(intent);
        }
    }

    // CREATE NEW ACCOUNT BTN
    public void clickCreateAccount(View view) {
        // Open CreateAccountScreen activity
        Intent intent = new Intent(this, CreateAccountScreen.class);
        startActivity(intent);
    }

    // Handles Google sign in
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // If Google Sign In successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // If Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            // If request code is not RC_SIGN_IN it must be Facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Firebase Google authentication:
    // Get access token, exchange it for a Firebase credential, and authenticate with Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    // Firebase Facebook authentication:
    // Get access token, exchange it for a Firebase credential, and authenticate with Firebase
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}