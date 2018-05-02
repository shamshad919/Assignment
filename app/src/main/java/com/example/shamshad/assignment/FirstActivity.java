package com.example.shamshad.assignment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener{


    private Button signout;
    Place address;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getSupportActionBar().hide();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        signout= (Button) findViewById(R.id.signout);
        signout.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(new Intent(FirstActivity.this,MainActivity.class));
                }
            }
        };


        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter the Address");
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHintTextColor(Color.parseColor("#000000"));
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.rounded_edittext_button));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                address=place;
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("address");
                mRef.setValue(address);

            }

            @Override
            public void onError(Status status) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void signout() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
    }


    @Override
    public void onClick(View v) {
        if(v==signout){
            signout();
        }
    }
}
