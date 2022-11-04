package com.example.adminapplication;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class uploadGallery extends AppCompatActivity {

    private Spinner imgCategory;
    private Button uploadImgButt;
    private ImageView imgView;
    private CardView cardView;
    Bitmap bitmap;
    final int REQ=1;
    String category;
    ProgressDialog pd;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gallery);
        imgCategory=findViewById(R.id.ImageCategory);
        cardView=findViewById(R.id.addGalleryImage);
        imgView=findViewById(R.id.galaryImage);
        uploadImgButt=findViewById(R.id.uploadImageButton);
        pd=new ProgressDialog(this);
        reference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference().child("gallery");
        storageReference= FirebaseStorage.getInstance().getReference().child("gallery");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary();
            }
        });
        uploadImgButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap==null){
                    Toast.makeText(uploadGallery.this, "Please upload image", Toast.LENGTH_SHORT).show();
                }else if(category.equals("Select Category")){
                    Toast.makeText(uploadGallery.this, "Please Select Category", Toast.LENGTH_SHORT).show();
                }else{
                    pd.setMessage("Uploading...");
                    pd.show();
                    UploadImage();
                }
            }
        });
        String[] items=new String[]{"Select Category","Convocation","Orientation","Farewell","Welcome"};
        imgCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));
        imgCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=imgCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void UploadImage() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImg=baos.toByteArray();
        final StorageReference filepath;
        filepath=storageReference.child(finalImg+"jpg");
        final UploadTask uploadTask=filepath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(uploadGallery.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(uploadGallery.this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        reference=reference.child(category);
        final String uniqueKey=reference.push().getKey();
        reference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(uploadGallery.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(uploadGallery.this, "Sorry, Image Uploaded UnSuccessfully", Toast.LENGTH_SHORT).show();
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
            imgView.setImageBitmap(bitmap);
        }
    }
}