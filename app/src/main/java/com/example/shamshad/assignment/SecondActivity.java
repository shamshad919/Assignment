package com.example.shamshad.assignment;

import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import static com.example.shamshad.assignment.R.drawable.pause;

public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mRef;

    FirebaseRecyclerAdapter<addressDetails, addressViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getSupportActionBar().hide();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<addressDetails, addressViewHolder>(addressDetails.class,
                R.layout.address_listrow,
                addressViewHolder.class,
                mRef) {
            @Override
            protected void populateViewHolder(final addressViewHolder viewHolder, final addressDetails model, final int position) {
                viewHolder.textView.setText(model.address);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayer mediaPlayer=new MediaPlayer();
                        addressDetails lpath= firebaseRecyclerAdapter.getItem(position);
                        String path=lpath.audio;
                        Toast.makeText(SecondActivity.this, path, Toast.LENGTH_LONG).show();

                        try{

                            mediaPlayer.setDataSource(path);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(final MediaPlayer mp) {
                                    mp.start();


                                }
                            });
                            mediaPlayer.prepare();

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                });
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference deleteRef= FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child(firebaseRecyclerAdapter.getRef(position).getKey());
                        deleteRef.removeValue();
                        Toast.makeText(SecondActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class addressDetails {
        String audio;
        String address;

        public addressDetails() {
        }

        public addressDetails(String audio, String address) {
            this.audio = audio;
            this.address = address;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    /**
     * Created by shamshad on 25/3/18.
     */

    public static class addressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        ImageView button;
        ImageView delete;
        public addressViewHolder(View itemView) {
            super(itemView);
            button = (ImageView) itemView.findViewById(R.id.play);
            textView=(TextView) itemView.findViewById(R.id.address_textview_recycler);
            delete=(ImageView) itemView.findViewById(R.id.delete);
        }


        @Override
        public void onClick(View v) {

        }
    }



    }



