package com.example.adminapplication.faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.adminapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class updateFaculty extends AppCompatActivity {
    private FloatingActionButton adFaculty;
    private RecyclerView cs,mec,ele,se;
    private LinearLayout nocs,nomec,noele,nose;
    private List<teacherData> list1,list2,list3,list4;
    private DatabaseReference reference,dBref;
    private TeacherAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);
        adFaculty=findViewById(R.id.updateFacultyButt);
        cs=findViewById(R.id.csDepartment);
        mec=findViewById(R.id.mechDepartment);
        ele=findViewById(R.id.eeDepartment);
        se=findViewById(R.id.seDepartment);
        nocs=findViewById(R.id.csnoData);
        nomec=findViewById(R.id.mecNoData);
        noele=findViewById(R.id.eenoData);
        nose=findViewById(R.id.senoData);
        reference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference().child("teacher");
        csDepartment();
        mechDepartment();
        eleDepartment();
        seDepartment();
        adFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(updateFaculty.this,addFaculty.class));
            }
        });
    }

    private void csDepartment() {
        dBref=reference.child("Computer Science");
        dBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1= new ArrayList<>();
                if(!dataSnapshot.exists()){
                    nocs.setVisibility(View.VISIBLE);
                    cs.setVisibility(View.GONE);
                }else{
                    cs.setVisibility(View.VISIBLE);
                    nocs.setVisibility(View.GONE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        teacherData td=snapshot.getValue(teacherData.class);
                        list1.add(td);
                    }
                    cs.setHasFixedSize(true);
                    cs.setLayoutManager(new LinearLayoutManager(updateFaculty.this));
                    adapter=new TeacherAdapter(list1,updateFaculty.this,"Computer Science");
                    cs.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(updateFaculty.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void mechDepartment() {
        dBref=reference.child("Mechanical Engineering");
        dBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list2= new ArrayList<>();
                if(!dataSnapshot.exists()){
                    nomec.setVisibility(View.VISIBLE);
                    mec.setVisibility(View.GONE);
                }else{
                    mec.setVisibility(View.VISIBLE);
                    nomec.setVisibility(View.GONE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        teacherData td=snapshot.getValue(teacherData.class);
                        list2.add(td);
                    }
                    mec.setHasFixedSize(true);
                    mec.setLayoutManager(new LinearLayoutManager(updateFaculty.this));
                    adapter=new TeacherAdapter(list2,updateFaculty.this,"Mechanical Engineering");
                    mec.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(updateFaculty.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void eleDepartment() {
        dBref=reference.child("Electronics Engineering");
        dBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3= new ArrayList<>();
                if(!dataSnapshot.exists()){
                    noele.setVisibility(View.VISIBLE);
                    ele.setVisibility(View.GONE);
                }else{
                    ele.setVisibility(View.VISIBLE);
                    noele.setVisibility(View.GONE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        teacherData td=snapshot.getValue(teacherData.class);
                        list3.add(td);
                    }
                    ele.setHasFixedSize(true);
                    ele.setLayoutManager(new LinearLayoutManager(updateFaculty.this));
                    adapter=new TeacherAdapter(list3,updateFaculty.this,"Electronics Engineering");
                    ele.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(updateFaculty.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void seDepartment() {
        dBref=reference.child("Software Engineering");
        dBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list4= new ArrayList<>();
                if(!dataSnapshot.exists()){
                    nose.setVisibility(View.VISIBLE);
                    se.setVisibility(View.GONE);
                }else{
                    se.setVisibility(View.VISIBLE);
                    nose.setVisibility(View.GONE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        teacherData td=snapshot.getValue(teacherData.class);
                        list4.add(td);
                    }
                    se.setHasFixedSize(true);
                    se.setLayoutManager(new LinearLayoutManager(updateFaculty.this));
                    adapter=new TeacherAdapter(list4,updateFaculty.this,"Software Engineering");
                    se.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(updateFaculty.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}