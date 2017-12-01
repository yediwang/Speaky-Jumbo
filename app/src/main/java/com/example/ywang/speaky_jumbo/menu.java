package com.example.ywang.speaky_jumbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout LL;
    String username;
    Spinner s_nat;
    Spinner s_lrn;
    String check_lrn;
    String check_nat;
    boolean lrnflag;
    boolean natflag;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        System.out.println("menu_username : " +username);

        LL = (LinearLayout)findViewById(R.id.lnlo);
        s_nat = (Spinner)findViewById(R.id.n_lan);
        s_lrn = (Spinner)findViewById(R.id.s_lan);

        init();
        spinner();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_acc) {
            Intent todo = new Intent(menu.this, account.class);
            todo.putExtra("username",username);
            startActivity(todo);
        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.signout) {
            Intent todo = new Intent(menu.this, Login.class);
            startActivity(todo);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void init() {
        natflag = true;
        lrnflag = true;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String u = postSnapshot.getKey().toString();
                    String nat = postSnapshot.child("nat_language").getValue().toString();
                    String lrn = postSnapshot.child("lrn_language").getValue().toString();
                    LL.addView(createCard(u,nat,lrn));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    View createCard(String username, String nat, String lrn) {
        ImageView card = new ImageView(this);
        ImageView avatar = new ImageView(this);
        TextView lang = new TextView(this);
        RelativeLayout rl = new RelativeLayout(this);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, calculateDpToPx(155));
        rl.setLayoutParams(lp);

        RelativeLayout.LayoutParams card_lp = new RelativeLayout.LayoutParams(
                calculateDpToPx(380), ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        card.setImageResource(R.drawable.card);
        card.setLayoutParams(card_lp);
        card.setId(View.generateViewId());
        rl.addView(card);

        RelativeLayout.LayoutParams avatar_lp = new RelativeLayout.LayoutParams(
                calculateDpToPx(100), ViewGroup.LayoutParams.WRAP_CONTENT
        );
        avatar_lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        avatar_lp.addRule(RelativeLayout.ALIGN_START, card.getId());
        avatar_lp.setMarginStart(calculateDpToPx(19));
        avatar.setImageResource(R.drawable.avatar);
        avatar.setLayoutParams(avatar_lp);
        avatar.setId(View.generateViewId());
        rl.addView(avatar);

        RelativeLayout.LayoutParams lang_lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lang_lp.addRule(RelativeLayout.END_OF, avatar.getId());
        lang_lp.setMarginStart(calculateDpToPx(17));
        lang_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lang.setLayoutParams(lang_lp);
        lang.setText("Username : "+username+"\nNative Language : "+nat
                +"\nLanguage of Study : "+lrn);
        lang.setTextSize(16);
        lang.setLineSpacing(0,(float)1.4);
        rl.addView(lang);

        return rl;
    }

    private int calculateDpToPx(int padding_in_dp){
        final float scale = getResources().getDisplayMetrics().density;
        return  (int) (padding_in_dp * scale + 0.5f);
    }

    void spinner() {
        s_nat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    init();
                    return;
                }
                natflag = false;
                lrnflag = false;
                String[] lang = getResources().getStringArray(R.array.languages);
                check_nat = lang[i];
                check_lrn = s_lrn.getSelectedItem().toString();
                if(check_lrn.equals("None")) {
                    LL.removeAllViews();
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String nat = postSnapshot.child("nat_language").getValue().toString();
                                if (nat.equals(check_nat)) {
                                    String u = postSnapshot.getKey().toString();
                                    natflag = true;
                                    String lrn = postSnapshot.child("lrn_language").getValue().toString();
                                    LL.addView(createCard(u, nat, lrn));
                                }
                            }
                            if(natflag == false && lrnflag == false){
                                Toast.makeText(menu.this, "No result find!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    LL.removeAllViews();
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String nat = postSnapshot.child("nat_language").getValue().toString();
                                String lrn = postSnapshot.child("lrn_language").getValue().toString();
                                if (nat.equals(check_nat)&&lrn.equals(check_lrn)) {
                                    String u = postSnapshot.getKey().toString();
                                    natflag = true;
                                    lrnflag = true;
                                    LL.addView(createCard(u, nat, lrn));
                                }
                            }
                            if(natflag == false && lrnflag == false){
                                Toast.makeText(menu.this, "No result find!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        s_lrn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    init();
                    return;
                }
                natflag = false;
                lrnflag = false;
                String[] lang = getResources().getStringArray(R.array.languages);
                check_lrn = lang[i];
                check_nat = s_nat.getSelectedItem().toString();
                if(check_nat.equals("None")) {
                    LL.removeAllViews();
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String lrn = postSnapshot.child("lrn_language").getValue().toString();
                                if (lrn.equals(check_lrn)) {
                                    String nat = postSnapshot.child("nat_language").getValue().toString();
                                    lrnflag = true;
                                    String u = postSnapshot.getKey().toString();
                                    LL.addView(createCard(u, nat, lrn));
                                }
                            }
                            if(natflag == false && lrnflag == false){
                                Toast.makeText(menu.this, "No result find!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    LL.removeAllViews();
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String nat = postSnapshot.child("nat_language").getValue().toString();
                                String lrn = postSnapshot.child("lrn_language").getValue().toString();
                                if (nat.equals(check_nat)&&lrn.equals(check_lrn)) {
                                    String u = postSnapshot.getKey().toString();
                                    natflag = true;
                                    lrnflag = true;
                                    LL.addView(createCard(u, nat, lrn));
                                }
                            }
                            if(natflag == false && lrnflag == false){
                                Toast.makeText(menu.this, "No result find!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
