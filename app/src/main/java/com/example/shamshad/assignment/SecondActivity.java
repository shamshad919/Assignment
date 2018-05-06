package com.example.shamshad.assignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView address;
    private Button playaudio;
    private Button play;
    private Button pause;
    private RecyclerView recyclerView;

    FirebaseRecyclerAdapter<details,detailsViewholder> firebaseRecyclerAdapter;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getSupportActionBar().hide();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference("user").child("2gopiYwqPubgOvRTUdKcmxWR8zT2");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<details,detailsViewholder>(details.class,
                R.layout.details_listrow,
                detailsViewholder.class,
                mRef) {
            @Override
            protected void populateViewHolder(detailsViewholder viewHolder, details model, int position) {
                viewHolder.address.setText(model.Address);
            }
        };
    recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    @Override
    public void onClick(View v) {

    }

   /* private void play() {


        MediaPlayer mediaPlayer=new MediaPlayer();
      //  String path= (String) dataSnapshot.child("Audio").getValue();

               try{

                   mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/assignment-202815.appspot.com/o/Audio%2F2gopiYwqPubgOvRTUdKcmxWR8zT2%2Fnew_audio.mp3?alt=media&token=d4c9bdbc-6406-424c-bbf9-056c1a63b574");
                   mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                       @Override
                       public void onPrepared(final MediaPlayer mp) {
                           mp.start();
                           play.setVisibility(View.GONE);
                           pause.setVisibility(View.VISIBLE);
                           pause.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   mp.pause();
                                   pause.setVisibility(View.GONE);
                                   play.setVisibility(View.VISIBLE);
                               }
                           });

                       }
                   });
                   mediaPlayer.prepare();

               }catch (IOException e){
                   e.printStackTrace();
               }
           }*/

    }



