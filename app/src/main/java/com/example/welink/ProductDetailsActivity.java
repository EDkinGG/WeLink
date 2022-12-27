package com.example.welink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView iv_product;
    CircleImageView ib_profile;

    TextView tv_head,tv_location, tv_contact, tv_description, tv_price,tv_product_category;

    Button button;

    DatabaseReference ref;

    String name,userid,url;//for messaging

    DatabaseReference checkVideocallRef;
    String senderuid;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        iv_product = findViewById(R.id.iv_product_product);
        ib_profile = findViewById(R.id.ib_profile_product);

        tv_head = findViewById(R.id.tv_head_product);
        tv_product_category = findViewById(R.id.tv_productCategory_product);
        tv_location = findViewById(R.id.tv_location_product);
        tv_contact = findViewById(R.id.tv_contact_product);
        tv_description = findViewById(R.id.tv_description_product);
        tv_price = findViewById(R.id.tv_price_product);

        checkIncoming();

        button = findViewById(R.id.btn_product);

        ref = FirebaseDatabase.getInstance().getReference().child("All Products");

        String ProductKey = getIntent().getStringExtra("ProductKey");

        ref.child(ProductKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if( snapshot.exists() )
                {
                    name = snapshot.child("name").getValue().toString();
                    url = snapshot.child("url").getValue().toString();
                    userid = snapshot.child("userid").getValue().toString();
                    String key = snapshot.child("key").getValue().toString();
                    String privacy = snapshot.child("privacy").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();
                    String product = snapshot.child("product").getValue().toString();
                    String category = snapshot.child("category").getValue().toString();
                    String productImgUrl = snapshot.child("productImgUrl").getValue().toString();
                    String location = snapshot.child("location").getValue().toString();
                    String contact = snapshot.child("contact").getValue().toString();
                    String price = snapshot.child("price").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();

                    Picasso.get().load(productImgUrl).into(iv_product);
                    Picasso.get().load(url).into(ib_profile);

                    tv_head.setText(product);
                    tv_product_category.setText(category);
                    tv_location.setText(location);
                    tv_contact.setText(contact);
                    tv_description.setText(description);
                    tv_price.setText(price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ib_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, ShowUser.class);
                intent.putExtra("n",name);
                intent.putExtra("u",url);
                intent.putExtra("uid",userid);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, MessageActivity.class);
                intent.putExtra("n",name);
                intent.putExtra("u",url);
                intent.putExtra("uid",userid);
                startActivity(intent);
            }
        });



    }


    public void checkIncoming(){

        checkVideocallRef = database.getReference("vc");


        try {

            checkVideocallRef.child(currentuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){

                        senderuid = snapshot.child("calleruid").getValue().toString();
                        Intent intent = new Intent(ProductDetailsActivity.this,VideoCallinComing.class);
                        intent.putExtra("uid",senderuid );
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){

            //   Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}