package com.nishan.testimageupload;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageUpload;
    Button btnChoose,btnUpload;

    final int CODE_GALLERY_REQUEST=999;
    String URL_upload="http://192.168.43.202/AyurvedaApp/upload.php";
    Bitmap bitmap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChoose=findViewById(R.id.btnChoose);
        btnUpload=findViewById(R.id.btnUpload);
        imageUpload=findViewById(R.id.imageUpload);

 btnChoose.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                 CODE_GALLERY_REQUEST);
     }
 });

        btnUpload.setOnClickListener(new View.OnClickListener() {
     @Override
             public void onClick(View v) {
         StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_upload, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 Toast.makeText(getApplicationContext(),"Error" + error.toString(),Toast.LENGTH_SHORT).show();

             }
         }){
             @Override
             protected Map<String, String> getParams() throws AuthFailureError {
                 Map<String ,String> params= new HashMap<>();
                 String imageData=imagetoString(bitmap);

                 params.put("image",imageData);

               return params;
             }
         };
         RequestQueue requestQueue=Volley.newRequestQueue(MainActivity.this);
         requestQueue.add(stringRequest);
     }
 });
    }
    //image upload work
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CODE_GALLERY_REQUEST){
            if(grantResults.length >0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"select image"),CODE_GALLERY_REQUEST);

            }
            else{
                Toast.makeText(getApplicationContext(),"you dont have permission",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //image upload work
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==CODE_GALLERY_REQUEST && requestCode== RESULT_OK && data!=null)
        {
            Uri filepath=data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                 bitmap=BitmapFactory.decodeStream(inputStream);
                imageUpload.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String imagetoString(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imagebyte=outputStream.toByteArray();
        String encodedImage=Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encodedImage;
    }


}
