package com.tufts.ywang.speaky_jumbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {

    ImageButton b_signup;
    ImageButton b_signin;
    EditText e_usr;
    EditText e_psw;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //check();

        b_signup = (ImageButton)findViewById(R.id.signup);
        b_signin = (ImageButton)findViewById(R.id.signin);
        b_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent todo = new Intent(Login.this, signup.class);
                startActivity(todo);
            }
        });

        b_signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myRef.addValueEventListener((new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean flag = false;
                        e_usr = (EditText)findViewById(R.id.usr);
                        e_psw = (EditText)findViewById(R.id.psw);
                        String u = e_usr.getText().toString();
                        String p = e_psw.getText().toString();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            String user = postSnapshot.getKey().toString();
                            if(u.length()==0) {
                                Toast.makeText(Login.this, "Please input your username!",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(p.length()==0) {
                                Toast.makeText(Login.this, "Please input your password!",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(user.equals(u)) {
                                flag = true;
                                String password = postSnapshot.child("password").getValue().toString();
                                if(p.equals(password)) {
                                    Intent todo = new Intent(Login.this, menu.class);
                                    todo.putExtra("username",u);
                                    startActivity(todo);
                                }
                                else {
                                    Toast.makeText(Login.this, "Wrong password!",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }
                        if(!flag) {
                            Toast.makeText(Login.this, "Username doesn't exsit!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Failed to read value." + databaseError.toException());
                    }
                }));
            }
        });

    }

    void check() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");

        // Read from the database

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String value = postSnapshot.getKey();
                    System.out.println("value is: " + value);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value." + error.toException());
            }
        });
    }
}
