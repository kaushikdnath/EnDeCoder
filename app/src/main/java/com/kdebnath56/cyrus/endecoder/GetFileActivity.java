package com.kdebnath56.cyrus.endecoder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;

public class GetFileActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private ImageView imgView;
    private Button saveBtn;
    private Button encodeBtn;
    private Button decodeBtn;
    private EditText encodedText;
    private Bitmap selectedImg;
    private String encodedImg;
    private Context thisActivity=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_file);

        saveBtn=(Button) findViewById(R.id.saveBtn);
        encodeBtn=(Button)findViewById(R.id.encodeBtn);
        encodedText=(EditText) findViewById(R.id.encodedText);
        imgView=(ImageView) findViewById(R.id.imageView);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        SELECT_PICTURE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FileOutputStream out = null;
                if(checkPermission()) {

                    try {
                        Date date=new Date();
                        File yourFile = new File("/sdcard/EnDeCoder/IMG_"+date.getYear()+date.getMonth()+date.getDay()+date.getHours()+
                                date.getMinutes()+date.getSeconds()+".jpg");
                        yourFile.createNewFile();
                        out = new FileOutputStream (yourFile);
                        selectedImg.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        encodeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                encodedImg=encodeToBase64(selectedImg,Bitmap.CompressFormat.JPEG, 100);
                encodedText.setText(encodedImg);
            }
        });
//        decodeBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                imgView.setImageBitmap(decodeBase64(encodedText.getText().toString()));
//            }
//        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                InputStream imgstrm=null;
                try{
                    imgstrm=getContentResolver().openInputStream(selectedImageUri);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                selectedImg= BitmapFactory.decodeStream(imgstrm);
                imgView.setImageBitmap(selectedImg);
            }
        }
    }
     private boolean checkPermission(){
         if(Build.VERSION.SDK_INT>=23){
             if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 == PackageManager.PERMISSION_GRANTED) {
             return true;
         }else{
             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
             return true;
         }
         }else{
             return true;
         }

     }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
