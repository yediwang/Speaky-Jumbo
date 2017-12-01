package com.tufts.ywang.speaky_jumbo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static class MyTaskParams {
        String url;
        ImageView img;
        Bitmap bit;

        MyTaskParams(String url, ImageView img) {
            this.url = url;
            this.img = img;
        }

        MyTaskParams(Bitmap bit, ImageView img) {
            this.bit = bit;
            this.img = img;
        }
    }

    private class getBitmapFromURL extends AsyncTask<MyTaskParams, Void, MyTaskParams> {

        @Override
        protected MyTaskParams doInBackground(MyTaskParams... params) {
            try {
                URL url = new URL(params[0].url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                MyTaskParams temp = new MyTaskParams(myBitmap, params[0].img);
                return temp;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }

        protected void onPostExecute(MyTaskParams result) {
            result.img.setImageBitmap(getCircleBitmap(result.bit));
        }
    }

    LinearLayout LL;
    String username;
    Spinner s_nat;
    Spinner s_lrn;
    String check_lrn;
    String check_nat;
    boolean lrnflag;
    boolean natflag;
    ImageView avatar;

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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child(username).child("url").getValue().toString();
                avatar = (ImageView)findViewById(R.id.icon);
                getBitmapFromURL getbitmap = new getBitmapFromURL();
                MyTaskParams temp = new MyTaskParams(imageUrl, avatar);
                getbitmap.execute(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    String imageUrl = postSnapshot.child("url").getValue().toString();
                    View v = createCard(u,nat,lrn,imageUrl);
                    LL.addView(v);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    View createCard(String username, String nat, String lrn, String imageUrl) {
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
                calculateDpToPx(100), calculateDpToPx(100)
        );
        avatar_lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        avatar_lp.addRule(RelativeLayout.ALIGN_START, card.getId());
        avatar_lp.setMargins(0,65,0,0);
        avatar_lp.setMarginStart(calculateDpToPx(19));
        /////////////////////////
        getBitmapFromURL getbitmap = new getBitmapFromURL();
        MyTaskParams temp = new MyTaskParams(imageUrl, avatar);
        getbitmap.execute(temp);
        /////////////////////////
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

        final String u = username;
        rl.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent todo = new Intent(menu.this, info.class);
                todo.putExtra("username", u);
                startActivity(todo);
            }
        });

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
                    //init();
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
                                    String imageUrl = postSnapshot.child("url").getValue().toString();
                                    LL.addView(createCard(u, nat, lrn, imageUrl));
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
                                String imageUrl = postSnapshot.child("url").getValue().toString();
                                if (nat.equals(check_nat)&&lrn.equals(check_lrn)) {
                                    String u = postSnapshot.getKey().toString();
                                    natflag = true;
                                    lrnflag = true;
                                    LL.addView(createCard(u, nat, lrn, imageUrl));
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
                    //init();
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
                                    String imageUrl = postSnapshot.child("url").getValue().toString();
                                    LL.addView(createCard(u, nat, lrn, imageUrl));
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
                                    String imageUrl = postSnapshot.child("url").getValue().toString();
                                    natflag = true;
                                    lrnflag = true;
                                    LL.addView(createCard(u, nat, lrn, imageUrl));
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

    public static Bitmap cropBitmap(Bitmap bitmap) {//从中间截取一个正方形
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长

        return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2,
                (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {//把图片裁剪成圆形
        if (bitmap == null) {
            return null;
        }
        bitmap = cropBitmap(bitmap);//裁剪成正方形
        try {
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circleBitmap);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            float roundPx = 0.0f;
            roundPx = bitmap.getWidth();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return circleBitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }
}
