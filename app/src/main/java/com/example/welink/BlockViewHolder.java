package com.example.welink;


import android.app.Application;
import android.content.ReceiverCallNotAllowedException;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockViewHolder extends RecyclerView.ViewHolder {

    TextView nametv,unblocktv;
    CircleImageView imageView;

    public BlockViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setBlockList(Application application, String url,String name, String text,String uid,String seen){

        nametv = itemView.findViewById(R.id.name_bl);
        imageView = itemView.findViewById(R.id.iv_bl);
        unblocktv = itemView.findViewById(R.id.unlock_bl);


        nametv.setText(name);
        Picasso.get().load(url).into(imageView);


    }
}
