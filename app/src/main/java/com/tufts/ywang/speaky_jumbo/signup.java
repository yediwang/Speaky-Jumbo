package com.tufts.ywang.speaky_jumbo;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class signup extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference myRef = database.getReference("user");
    StorageReference stoRef = storage.getReference();
    String u;
    String p1;
    String p2;
    String Storage_Path = "user/";
    String avatar_url = "";
    Uri FilePathUri;
    int Image_Request_Code = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        back();
        choose();
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
    private ImageButton i_ch;

    public void choose() {
        i_ch = (ImageButton)findViewById(R.id.choose);
        i_ch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
    }

    public void finish() {

        e_u = (EditText)findViewById(R.id.usr);
        e_p = (EditText)findViewById(R.id.psw);
        e_cp = (EditText)findViewById(R.id.cpsw);

        ImageButton b_fin = (ImageButton)findViewById(R.id.finish);
        b_fin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                u = e_u.getText().toString();
                p1 = e_p.getText().toString();
                p2 = e_cp.getText().toString();
                if(avatar_url.length()==0) {
                    Toast.makeText(signup.this, "Please upload your profile picture!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
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
                    user u_class = new user(p1, avatar_url);
                    myRef.child(u).setValue(u_class);

                    Intent todo = new Intent(signup.this, menu.class);
                    todo.putExtra("username", u);
                    System.out.println("signup username : " + u);
                    startActivity(todo);
                }
                else {
                    // if the two passwords don't match up, send an error message
                    Toast.makeText(signup.this, "Password doesn't match!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
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
                i_ch.setImageBitmap(bitmap);

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
                            Toast.makeText(signup.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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

            Toast.makeText(signup.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
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
