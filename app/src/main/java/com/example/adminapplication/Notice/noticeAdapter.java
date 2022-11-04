package com.example.adminapplication.Notice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class noticeAdapter extends RecyclerView.Adapter<noticeAdapter.noticeViewAdapter> {
    private Context context;
    private ArrayList<noticeData> list;


    public noticeAdapter(Context context, ArrayList<noticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public noticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.newsitemlayout,parent,false);
        return new noticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull noticeViewAdapter holder, int position) {
        noticeData noticeData=list.get(position);
        holder.delTitle.setText(noticeData.getTitle());
        try {
            if(noticeData.getImage()!=null){
                Picasso.get().load(noticeData.getImage()).into(holder.delImageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.delNoticeButt.setOnClickListener(


                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder= new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure to delete Notice");
                        builder.setCancelable(true);
                        builder.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference reference= FirebaseDatabase.getInstance("https://adminapp-5be5d-default-rtdb.firebaseio.com/").getReference().child("Notice");
                                        reference.child(noticeData.getKey()).removeValue().addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(context, "Notice Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                        ).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        notifyItemRemoved(position);

                                    }
                                }
                        );
                        builder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }
                        );
                        AlertDialog dialog=null;
                        try {
                            dialog=builder.create();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(dialog!=null){
                            dialog.show();
                        }
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class noticeViewAdapter extends RecyclerView.ViewHolder {
        Button delNoticeButt;
        ImageView delImageView;
        TextView delTitle;
        public noticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            delNoticeButt=itemView.findViewById(R.id.deleteNoticeButton);
            delImageView=itemView.findViewById(R.id.deleteNoticeImage);
            delTitle=itemView.findViewById(R.id.deleteNoticeTitle);
        }
    }
}
