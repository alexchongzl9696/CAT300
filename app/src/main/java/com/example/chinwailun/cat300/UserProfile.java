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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText userName;
    private EditText Password;
    private EditText Email;
    private EditText Age;
    Spinner spinner;
    String userID;
    String userPassword;
    String email;
    String age;
    String itemname1;
    String itemname;
    int j=0;
    List<String> categories = new ArrayList<String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.userName);
        Password = findViewById(R.id.Password);
        Email = findViewById(R.id.Email);
        Age = findViewById(R.id.Age);

        call();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        categories.add("aquarium");
        categories.add("lodging");
        categories.add("bar");
        categories.add("cafe");
        categories.add("night_club");
        ArrayAdapter<String> adapter1= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
        spinner.setAdapter(adapter1);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemname1 = parent.getItemAtPosition(position).toString();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    public void call(){


        Intent get = getIntent();
        if (get.hasExtra("ID")){
            userID = getIntent().getExtras().getString("ID");
        }
        if (get.hasExtra("Password")){
            userPassword = getIntent().getExtras().getString("Password");
        }

        db.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    email = task.getResult().get("Email").toString();
                    Email.setText(email);
                    age = task.getResult().get("Age").toString();
                    itemname = task.getResult().get("Preference").toString();
                    for(int k=1;k<categories.size();k++){
                        if(itemname.equals(categories.get(k-1))){
                            break;
                        }
                        j++;
                    }
                    Age.setText(age);
                    userName.setText(userID);
                    Password.setText(userPassword);
                    spinner.setSelection(j);
                }
            }
        });
    }

    public void save(View v){
        String age = "Age";
        String email = "Email";
        String password = "Password";
        String preference = "Preference";
        String inputUser = userName.getText().toString();
        String inputPassword = Password.getText().toString();
        String inputAge = Age.getText().toString();
        String inputEmail = Email.getText().toString();

        Map<String, String> newUser = new HashMap< >();

        DocumentReference user= db.collection("Users").document(inputUser);
        user.update(age,inputAge);
        user.update(email,inputEmail);
        user.update(preference,itemname1);
        user.update(password,inputPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UserProfile.this,"Update Successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Intent change = new Intent(UserProfile.this,Main.class);
        change.putExtra("ID",userID);
        change.putExtra("Password",userPassword);
        startActivity(change);
    }

    public void deleteUser(View v){
        db.collection("Users").document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UserProfile.this, "Data deleted !",
                        Toast.LENGTH_SHORT).show();

                Intent gg = new Intent(UserProfile.this,MainActivity.class);
                startActivity(gg);

            }
        });
    }


}
