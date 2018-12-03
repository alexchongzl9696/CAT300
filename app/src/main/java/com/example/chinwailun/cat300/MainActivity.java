package com.example.chinwailun.cat300;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    Button btnSignUp;
    private TextInputLayout username;
    private TextInputLayout password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(MainActivity.this, Main.class);
                startActivity(x);
            }
        });
    }

    //public void btnLogin(View view){
            //userLogin();
       // }
        /*if(view==btnSignUp){
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        }*/



    /*public void userLogin(){
        String getuser = username.getEditText().getText().toString().trim();
        final String getPassword = password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(getuser)) {
            Toast.makeText(this, "Please enter Username", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(getPassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()){
                    int numOfUser = task.getResult().size();
                    for (QueryDocumentSnapshot col : task.getResult()){
                        numOfUser--;
                        final String userDoc = col.getId();
                        if (getuser.equals(userDoc)){
                            db.collection("Users").document(userDoc).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    db.collection("Users").document(userDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().get("Password").equals(getPassword)){

                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }*/
}
