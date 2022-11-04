package com.example.adminapplication.Notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adminapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class addNotice extends AppCompatActivity {
    CardView cardView;
    private ImageView imageView;
    private final int REQ=1;
    private Bitmap bitmap;
    private EditText noticeTitleTxt;
    private Button uploadNoticeBtn;
    private DatabaseReference reference,dBRef;
    private StorageReference storageReference;
    private String downloadUrl="";
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);
        cardView=findViewById(R.id.addImage);
        imageView=findViewById(R.id.noticeImage);
        noticeTitleTxt=findViewById(R.id.noticeTitle);
        uploadNoticeBtn=findViewById(R.id.uploadNotceButton);
        reference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);
        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeTitleTxt.toString().isEmpty()){
                    noticeTitleTxt.setError("Empty");
                    noticeTitleTxt.requestFocus();

                    Toast.makeText(addNotice.this,"Enter Notification Title",Toast.LENGTH_SHORT).show();
                }else if(bitmap==null){
                    uploadData();
                }else{
                    uploadImage();
                }
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary();
            }
        });
    }

    private void uploadImage() {
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImg=baos.toByteArray();
        final StorageReference filepath;
        filepath=storageReference.child("Notice").child(finalImg+"jpg");
        final UploadTask uploadTask=filepath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(addNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(addNotice.this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadData() {
        dBRef=reference.child("Notice");
        final String uniqueKey=dBRef.push().getKey();

        String title=noticeTitleTxt.getText().toString();
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yy");
        String date=currentDate.format(calForDate.getTime());
        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        String CurrTime=currentTime.format(calForTime.getTime());
        noticeData nd=new noticeData(title,downloadUrl,date,CurrTime,uniqueKey);
        dBRef.child(uniqueKey).setValue(nd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(addNotice.this, "Notice Added Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addNotice.this, "Notice Added UnSuccessfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

    }

    private void openGallary() {
        Intent picImg=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picImg,REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK){
            Uri uri=data.getData();
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        }
    }
}