package com.example.ywang.speaky_jumbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class account extends AppCompatActivity {

    ImageButton b_back;
    ImageButton b_fin;
    Spinner s_gen;
    Spinner s_nat;
    Spinner s_lrn;
    EditText e_con;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");
    String gen;
    String nat;
    String lrn;
    String con;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        b_fin = (ImageButton)findViewById(R.id.finish);
        b_back = (ImageButton)findViewById(R.id.back);
        s_gen = (Spinner)findViewById(R.id.gender);
        s_nat = (Spinner)findViewById(R.id.acc_n_lan);
        s_lrn = (Spinner)findViewById(R.id.acc_s_lan);
        e_con = (EditText)findViewById(R.id.contact);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String user = postSnapshot.getKey().toString();
                    if(user.equals(username)) {
                        String gen = postSnapshot.child("gender").getValue().toString();
                        String lrn = postSnapshot.child("lrn_language").getValue().toString();
                        String nat = postSnapshot.child("nat_language").getValue().toString();
                        String con = postSnapshot.child("contact").getValue().toString();
                        setSpinnerItemSelectedByValue(s_gen,gen);
                        setSpinnerItemSelectedByValue(s_lrn,lrn);
                        setSpinnerItemSelectedByValue(s_nat,nat);
                        e_con.setText(con);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        back();
        save();
    }

    public static void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter adapter= spinner.getAdapter(); //得到Spinner Adapter对象
        int count= adapter.getCount();
        for(int i=0;i<count;i++){
            if(value.equals(adapter.getItem(i).toString())){
                spinner.setSelection(i,true);// 默认选中项
                break;
            }
        }
    }

    void back() {
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent todo = new Intent(account.this, menu.class);
                todo.putExtra("username",username);
                startActivity(todo);
            }
        });
    }

    void save() {
        b_fin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                gen = s_gen.getSelectedItem().toString();
                nat = s_nat.getSelectedItem().toString();
                lrn = s_lrn.getSelectedItem().toString();
                con = e_con.getText().toString();

                myRef.child(username).child("gender").setValue(gen);
                myRef.child(username).child("lrn_language").setValue(lrn);
                myRef.child(username).child("nat_language").setValue(nat);
                myRef.child(username).child("contact").setValue(con);

                Intent todo = new Intent(account.this, menu.class);
                todo.putExtra("username",username);
                startActivity(todo);

                //user u_class = new user(p1);
                //myRef.child(u).setValue(u_class);
            }
        });
    }
}
