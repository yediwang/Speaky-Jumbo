package com.example.ywang.speaky_jumbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        back();
        finish();
    }

    public void back() {
        ImageButton b_back = (ImageButton)findViewById(R.id.back);
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent todo = new Intent(signup.this, Login.class);
                startActivity(todo);
            }
        });
    }

    private EditText e_u;
    private EditText e_p;
    private EditText e_cp;
    MySQLiteOpenHelper helper;

    public void finish() {

        e_u = (EditText)findViewById(R.id.usr);
        e_p = (EditText)findViewById(R.id.psw);
        e_cp = (EditText)findViewById(R.id.cpsw);
        ImageButton b_fin = (ImageButton)findViewById(R.id.finish);
        b_fin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String u = e_u.getText().toString();
                String p1 = e_p.getText().toString();
                String p2 = e_cp.getText().toString();
                if(u.length()==0) {
                    Toast.makeText(signup.this, "Please input your username!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(p1.length()==0) {
                    Toast.makeText(signup.this, "Please input your password!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(p1.equals(p2)) {
                    user u_class = new user(p1);
                    myRef.child(u).setValue(u_class);

                    Intent todo = new Intent(signup.this, menu.class);
                    todo.putExtra("username",u);
                    System.out.println("signup username : "+u);
                    startActivity(todo);
                }
                else {
                    // if the two passwords don't match up, send an error message
                    Toast.makeText(signup.this, "Password doesn't match!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
