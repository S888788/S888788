package com.example.inspire_section.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.inspire_section.Adpter.myviewholder;
import com.example.inspire_section.MainActivity;
import com.example.inspire_section.Model.InspireModelClass;
import com.example.inspire_section.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EntrepreurialPodcastsActivity extends AppCompatActivity {
RecyclerView recyclerView;
LinearLayoutManager linearLayoutManager;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrepreurial_podcasts);
        inits();
        linearLayoutManager=new LinearLayoutManager(EntrepreurialPodcastsActivity.this);

        recyclerView.setLayoutManager(linearLayoutManager);
    ProgressDialog progressDialog=new ProgressDialog(this);
    progressDialog.setMessage("please wait..");
    progressDialog.show();
        FirebaseRecyclerOptions<InspireModelClass> options =
                new FirebaseRecyclerOptions.Builder<InspireModelClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("uploadvideo"), InspireModelClass.class)
                        .build();


        FirebaseRecyclerAdapter<InspireModelClass, myviewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<InspireModelClass, myviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull InspireModelClass model) {
                String url=model.getVurl();
                Log.d("url",url);
                String title=model.getTitle();
                Log.d("tutle",title);
                holder.setExoplayer(getApplication(),model.getTitle(),model.getVurl());
                progressDialog.dismiss();
            }

            @NonNull
            @Override
            public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.entre_podcast_recy_layout_file,parent,false);
                return new myviewholder(view);

            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }


    private void inits() {
        recyclerView=(RecyclerView)findViewById(R.id.recy_entre_podcast);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(EntrepreurialPodcastsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}