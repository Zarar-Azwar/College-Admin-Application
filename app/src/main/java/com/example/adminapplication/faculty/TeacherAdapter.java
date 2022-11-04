package com.example.adminapplication.faculty;

import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewAdapter> {
    private List<teacherData> list;
    private Context context;
    private String dep;

    public TeacherAdapter(List<teacherData> list, Context context, String dep) {
        this.list = list;
        this.context = context;

        this.dep = dep;
    }

    @NonNull
    @Override
    public TeacherViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.teacher_item_layout,parent,false);
        return new TeacherViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewAdapter holder, int position) {
        teacherData items=list.get(position);
        holder.name.setText(items.getName());
        holder.email.setText(items.getEmail());
        holder.post.setText(items.getPost());
        try {
            Picasso.get().load(items.getImage()).into(holder.img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.update.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context,updateTeacher.class);
                        intent.putExtra("name",items.getName());
                        intent.putExtra("email",items.getEmail());
                        intent.putExtra("post",items.getPost());
                        intent.putExtra("img",items.getImage());
                        intent.putExtra("key",items.getKey());
                        intent.putExtra("dep",dep);
                        context.startActivity(intent);

                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TeacherViewAdapter extends RecyclerView.ViewHolder {
        private TextView name,email,post;
        private ImageView img;
        private Button update;

        public TeacherViewAdapter(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.txtName);
            email=itemView.findViewById(R.id.txtEmail);
            post=itemView.findViewById(R.id.txtPost);
            img=itemView.findViewById(R.id.techerPic);
            update=itemView.findViewById(R.id.updateInfo);


        }
    }
}
