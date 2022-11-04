package com.example.adminapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.adminapplication.Notice.addNotice;
import com.example.adminapplication.Notice.deleteNotice;
import com.example.adminapplication.faculty.updateFaculty;

public class MainActivity extends AppCompatActivity{
    CardView addNoticeCard;
    CardView uploadImageGalleryCard;
    CardView uploadPdfAct;
    CardView upFaculty;
    CardView deleteNoticeCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNoticeCard=findViewById(R.id.addNotice);
        uploadImageGalleryCard=findViewById(R.id.addImage);
        uploadPdfAct=findViewById(R.id.addEbook);
        upFaculty=findViewById(R.id.addFaculty);
        deleteNoticeCard=findViewById(R.id.deleteNotice);
        deleteNoticeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, deleteNotice.class));
            }
        });
        upFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, updateFaculty.class));
            }
        });
        uploadPdfAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UploadPdfActivity.class));
            }
        });
        uploadImageGalleryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,uploadGallery.class));
            }
        });
        addNoticeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, addNotice.class));
            }
        });
    }


}