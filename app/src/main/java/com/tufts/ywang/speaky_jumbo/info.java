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
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class info extends AppCompatActivity {

    private class getBitmapFromURL extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            icon.setImageBitmap(getCircleBitmap(result));
        }
    }

    ImageButton icon;
    TextView t_name;
    TextView t_gen;
    TextView t_nat;
    TextView t_lrn;
    TextView t_con;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");
    String gen;
    String nat;
    String lrn;
    String con;
    String avatar_url = "";

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        icon = (ImageButton)findViewById(R.id.add);
        t_name = (TextView)findViewById(R.id.name);
        t_gen = (TextView)findViewById(R.id.gender);
        t_nat = (TextView)findViewById(R.id.acc_s_lan);
        t_lrn = (TextView)findViewById(R.id.acc_n_lan);
        t_con = (TextView)findViewById(R.id.contact);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot postSnapshot = dataSnapshot.child(username);
                System.out.println(username);
                gen = postSnapshot.child("gender").getValue().toString();
                lrn = postSnapshot.child("lrn_language").getValue().toString();
                nat = postSnapshot.child("nat_language").getValue().toString();
                con = postSnapshot.child("contact").getValue().toString();
                avatar_url = postSnapshot.child("url").getValue().toString();
                t_name.setText(username);
                t_gen.setText(gen);
                t_lrn.setText(lrn);
                t_nat.setText(nat);
                t_con.setText(con);
                /////////////////////////
                getBitmapFromURL getbitmap = new getBitmapFromURL();
                getbitmap.execute(avatar_url);
                /////////////////////////
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
