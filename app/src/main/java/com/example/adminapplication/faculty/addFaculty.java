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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class addFaculty extends AppCompatActivity {

    private ImageView profilePicture;
    private EditText teacherName,teacherEmail,teacherPost;
    private Spinner teacherCategory;
    private Button uploadButton;
    private final int REQ=1;
    private Bitmap bitmap=null;
    private String category;
    private String Name,Email,Post,downloadUrl="";
    private DatabaseReference databaseReference,dBRef;
    private StorageReference storageReference;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        profilePicture=findViewById(R.id.profileImg);
        teacherName=findViewById(R.id.teacherName);
        teacherEmail=findViewById(R.id.teacherEmail);
        teacherPost=findViewById(R.id.teacherPost);
        teacherCategory=findViewById(R.id.AddTeacherCategory);
        uploadButton=findViewById(R.id.addTeacherButton);
        databaseReference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference().child("teacher");
        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidations();
            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary();
            }
        });
        String[] items=new String[]{"Select Category","Computer Science","Mechanical Engineering","Software Engineering","Electronics Engineering"};
        teacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));
        teacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=teacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void checkValidations() {
        Name=teacherName.getText().toString();
        Email=teacherEmail.getText().toString();
        Post=teacherPost.getText().toString();
        if(Name.equals(null)){
            teacherEmail.setError("Empty");
            teacherEmail.requestFocus();
        }
        else if(Email.equals(null)){
            teacherName.setError("Empty");
            teacherName.requestFocus();
        }
        else if(Post.equals(null)){
            teacherPost.setError("Empty");
            teacherPost.requestFocus();
        }else if(category.equals("Select Category")){
            Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
        }
        else if(bitmap==null){
            insertDataFaculty();
        }
        else {
            uploadImage();
        }
    }
    private void insertDataFaculty() {
        dBRef=databaseReference.child(category);
        final String uniqueKey=dBRef.push().getKey();

        teacherData nd=new teacherData(Name,Email,Post,downloadUrl,uniqueKey);
        dBRef.child(uniqueKey).setValue(nd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(addFaculty.this, "Teacher Added Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addFaculty.this, "Notice Added UnSuccessfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
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
        uploadTask.addOnCompleteListener(addFaculty.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    insertDataFaculty();
                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(addFaculty.this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void insertData() {
    }

    private void uploadProfImg() {
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
            profilePicture.setImageBitmap(bitmap);
        }
    }
}