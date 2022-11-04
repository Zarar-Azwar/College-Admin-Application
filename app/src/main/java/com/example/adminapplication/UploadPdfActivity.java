package com.example.adminapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdfActivity extends AppCompatActivity {

    private CardView cardView;
    private EditText editText;
    private Button UploadButt;
    private TextView txtview;
    private String filename;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String downloadUrl="";
    private String title;
    private ProgressDialog pd;
    private Uri pdfData;
    private final int REQ=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        cardView=findViewById(R.id.addPdf);
        editText=findViewById(R.id.bookTitle);
        UploadButt=findViewById(R.id.uploadBookButton);
        txtview=findViewById(R.id.fileNameTextView);
        databaseReference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);
        UploadButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=editText.getText().toString();
                if(title.equals("")){
                    editText.requestFocus();
                    editText.setError("Empty");
                    Toast.makeText(UploadPdfActivity.this, "Please Insert pdf title", Toast.LENGTH_SHORT).show();
                }else if(pdfData==null){
                    Toast.makeText(UploadPdfActivity.this, "Please Upload Pdf", Toast.LENGTH_SHORT).show();
                }else{
                    uploadPDF();
                }
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void uploadPDF() {
        pd.setTitle("Please Wait");
        pd.setMessage("Uploading Pdf");
        pd.show();
        StorageReference reference=storageReference.child("pdf/"+filename+"-"+System.currentTimeMillis());
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri uri=uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadPdfActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void uploadData(String downloadUrl) {
        String uniqueKey=databaseReference.child("pdf").push().getKey();
        HashMap data=new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);
        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPdfActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
                txtview.setText("");
                editText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdfActivity.this, "Sorry! Pdf Uploaded UnSuccessfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent,"Select Pdf File"),REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK){
            pdfData=data.getData();
            if(pdfData.toString().startsWith("content://")){
                try {
                    Cursor cursor=null;
                    cursor=UploadPdfActivity.this.getContentResolver().query(pdfData,null,null,null,null);
                    if(cursor!=null &&cursor.moveToFirst()){
                        filename=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(pdfData.toString().startsWith("file://")){
                filename=new File(pdfData.toString()).getName();
            }
            txtview.setText(filename);
        }
    }
}