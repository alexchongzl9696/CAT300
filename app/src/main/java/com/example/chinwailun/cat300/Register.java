package com.example.chinwailun.cat300;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Register extends AppCompatActivity {
    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout age;
    private TextInputLayout email;
    private static final String TAG = ".Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.UserName);
        password = findViewById(R.id.Password);
        age = findViewById(R.id.Age);
        email = findViewById(R.id.Email);
    }

    private boolean validateEmail() {
        String emailInput = email.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validateAge() {
        String ageInput = age.getEditText().getText().toString().trim();

        if (ageInput.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }
    private boolean validateUsername() {
        String usernameInput = username.getEditText().getText().toString().trim();

        Log.d(TAG, "validateUsername. ");
        if (usernameInput.isEmpty()) {
            Log.d(TAG, "validateUsername: isEmpty ");
            username.setError("Field can't be empty");
            return false;
        } else {
            Log.d(TAG, "validateUsername: nothing ");
            username.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = password.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        }
        else if (passwordInput.length()<12){
            password.setError("Length of password should be 12 or above");
            return false;
        }
        else {
            password.setError(null);
            return true;
        }
    }

    public void confirmInput(View v) {
        if (!validateEmail() | !validateUsername() | !validatePassword()||!validateAge()) {
            return;
        }
        Intent intent = new Intent(Register.this, MainActivity.class);
    }
}
