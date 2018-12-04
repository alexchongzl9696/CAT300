package com.example.chinwailun.cat300;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText UserName;
    private EditText Password;
    private EditText age;
    private EditText Email;
    private static final String TAG = ".Register";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserName = findViewById(R.id.UserName);
        Password = findViewById(R.id.Password);
        age = findViewById(R.id.Age);
        Email = findViewById(R.id.Email);
    }

    private boolean validateEmail() {
        String emailInput = Email.getText().toString();

        if (emailInput.isEmpty()) {
            Email.setError("Field can't be empty");
            return false;
        } else {
            Email.setError(null);
            return true;
        }
    }

    private boolean validateAge() {
        String ageInput = age.getText().toString();

        if (ageInput.isEmpty()) {
            age.setError("Field can't be empty");
            return false;
        } else {
            age.setError(null);
            return true;
        }
    }
    private boolean validateUsername() {
        String usernameInput = UserName.getText().toString();

        Log.d(TAG, "validateUsername. ");
        if (usernameInput.isEmpty()) {
            Log.d(TAG, "validateUsername: isEmpty ");
            UserName.setError("Field can't be empty");
            return false;
        } else {
            Log.d(TAG, "validateUsername: nothing ");
            UserName.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = Password.getText().toString();

        if (passwordInput.isEmpty()) {
            Password.setError("Field can't be empty");
            return false;
        }
        else if (passwordInput.length()<12){
            Password.setError("Length of password should be 12 or above");
            return false;
        }
        else {
            Password.setError(null);
            return true;
        }
    }

    public void confirmInput(View v) {
        if (!validateEmail() | !validateUsername() | !validatePassword()||!validateAge()) {
            return;
        }
        String Age = "Age";
        String email = "Email";
        String password = "Password";
        String emailInput = Email.getText().toString();
        String ageInput = age.getText().toString();
        String usernameInput = UserName.getText().toString();
        String passwordInput = Password.getText().toString();
        Map <String, String> newUser = new HashMap< >();
        newUser.put(Age,ageInput);
        newUser.put(password,passwordInput);
        newUser.put(email,emailInput);
        db.collection("Users").document(usernameInput).set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Register.this,"User Registered",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,"ERROR"+e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG",e.toString());
                    }
                });
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
    }
}
