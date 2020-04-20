package com.droyder.flashchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    // TODO: Add member variables here:
    private FirebaseAuth mAuth;
    private String email, password;
    static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // TODO: Grab an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // TODO: Call attemptLogin() here
            attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        finish();
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // TODO: Complete the attemptLogin() method
    private void attemptLogin() {
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        if(!isValid()) return;
        Toast.makeText(this,"Login in progress...",Toast.LENGTH_SHORT).show();

        // TODO: Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FlashChat","signInWithEmailAndPassword() onComplete: "+task.isSuccessful());

                if(!task.isSuccessful()){
                    Log.d("FlashChat","Problem Signing in: "+task.getException());
                    showErrorDialog("There was a problem signing in");
                } else {
                    if (setupDisplayName()) {
                        finish();
                        startActivity(new Intent(LoginActivity.this,MainChatActivity.class));
                    }
                }
            }
        });

    }

    // TODO: Show error on screen with an alert dialog
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private boolean isValid(){
        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || password.length()<6) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!email.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (setupDisplayName()){
            startActivity(new Intent(this, ChatRoomActivity.class));
        }
    }

    private boolean setupDisplayName(){
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            username = user.getDisplayName();
            String email = user.getEmail();
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            Log.d("FlashChat",user +" "+username+" "+email+" "+emailVerified+" "+uid);
            SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
            prefs.edit().putString(RegisterActivity.DISPLAY_NAME_KEY,username).apply();
            return true;
        }
        return false;
    }
}