package com.tufts.ywang.speaky_jumbo;

import android.content.ContentResolver;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class account extends AppCompatActivity {

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
    ImageButton b_back;
    ImageButton b_fin;
    Spinner s_gen;
    Spinner s_nat;
    Spinner s_lrn;
    EditText e_con;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference myRef = database.getReference("user");
    StorageReference stoRef = storage.getReference();
    String gen;
    String nat;
    String lrn;
    String con;
    String Storage_Path = "user/";
    String avatar_url = "";
    Uri FilePathUri;
    int Image_Request_Code = 7;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        icon = (ImageButton)findViewById(R.id.add);
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
                        String imageUrl = postSnapshot.child("url").getValue().toString();
                        avatar_url = imageUrl;
                        setSpinnerItemSelectedByValue(s_gen,gen);
                        setSpinnerItemSelectedByValue(s_lrn,lrn);
                        setSpinnerItemSelectedByValue(s_nat,nat);
                        e_con.setText(con);
                        /////////////////////////
                        getBitmapFromURL getbitmap = new getBitmapFromURL();
                        getbitmap.execute(imageUrl);
                        /////////////////////////
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
        choose();
    }

    public void choose() {
        icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                bitmap = getCircleBitmap(bitmap);
                // Setting up bitmap selected image into ImageView.
                icon.setImageBitmap(bitmap);

                UploadImageFileToFirebaseStorage();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {
        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {
            // Creating second StorageReference.
            StorageReference storageReference2nd = stoRef.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            avatar_url = taskSnapshot.getDownloadUrl().toString();
                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println("failure");
                            // Hiding the progressDialog.
                            //progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(account.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("progress");
                            // Setting progressDialog Title.
                            //progressDialog.setTitle("Image is Uploading...");
                        }
                    });
        }
        else {
            Toast.makeText(account.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
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
                myRef.child(username).child("url").setValue(avatar_url);

                Intent todo = new Intent(account.this, menu.class);
                todo.putExtra("username",username);
                startActivity(todo);

                //user u_class = new user(p1);
                //myRef.child(u).setValue(u_class);
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
