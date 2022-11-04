package com.example.adminapplication.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.DuplicateFormatFlagsException;
import java.util.HashMap;

public class updateTeacher extends AppCompatActivity {
    ImageView img;
    EditText name,email,post;
    Button delete,update;

    String nameStr,emailStr,postStr,image;
    final int REQ=1;
    Bitmap bitmap=null;
    StorageReference storageReference;
    DatabaseReference dbRef,reference;
    ProgressDialog pd;
    String downloadUrl="",uniqueKey="",category="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        nameStr=getIntent().getStringExtra("name");
        emailStr=getIntent().getStringExtra("email");
        postStr=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("img");

        uniqueKey=getIntent().getStringExtra("key");
        category=getIntent().getStringExtra("dep");
        //Toast.makeText(this,uniqueKey, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,category, Toast.LENGTH_SHORT).show();

        img=findViewById(R.id.updateProfileImg);
        name=findViewById(R.id.updateTeacherName);
        email=findViewById(R.id.updateTeacherEmail);
        post=findViewById(R.id.updateTeacherPost);
        update=findViewById(R.id.UpdateTeacherButton);
        delete=findViewById(R.id.DeleteTeacherButton);
        reference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference().child("teacher");

        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameStr=name.getText().toString();
                emailStr=email.getText().toString();
                postStr=post.getText().toString();
                checkValidation();
            }
        });
        try {
            Picasso.get().load(image).into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        name.setText(nameStr);
        email.setText(emailStr);
        post.setText(postStr);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary();
            }
        });


    }

    private void deleteData() {
        reference.child(category).child(uniqueKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(updateTeacher.this, "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(updateTeacher.this,updateFaculty.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(updateTeacher.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValidation() {
        if(nameStr.isEmpty()){
            name.setError("Empty");
            name.requestFocus();
        }else if(emailStr.isEmpty()){
            email.setError("Empty");
            email.requestFocus();
        }else if(postStr.isEmpty()){
            post.setError("Empty");
            post.requestFocus();
        }else if(bitmap==null){
            updateData(downloadUrl);
        }else{
            uploadImage();
        }
    }

    private void updateData(String st) {
        HashMap map=new HashMap();
        map.put("name",nameStr);
        map.put("email",emailStr);
        map.put("post",postStr);
        map.put("image",st);

        reference.child(category).child(uniqueKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(updateTeacher.this, "Teacher Updated", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(updateTeacher.this,updateFaculty.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(updateTeacher.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
        filepath=storageReference.child("Teachers").child(finalImg+"jpg");
        final UploadTask uploadTask=filepath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(updateTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadUrl);
                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(updateTeacher.this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                }
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
            img.setImageBitmap(bitmap);
        }
    }
}