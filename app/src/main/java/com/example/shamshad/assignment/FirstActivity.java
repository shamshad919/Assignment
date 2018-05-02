package com.example.shamshad.assignment;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.jar.*;
import java.util.jar.Manifest;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener{


    private Button signout;
    private Button currentlocation;
    Place address;
    private TextView text;
    Geocoder geocoder;

    Double lat;
    Double lon;
    private LocationManager locationmanager;
    List<Address> addressList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getSupportActionBar().hide();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        signout = (Button) findViewById(R.id.signout);
        currentlocation = (Button) findViewById(R.id.currentlocation);
        signout.setOnClickListener(this);
        currentlocation.setOnClickListener(this);
        text = (TextView) findViewById(R.id.text_view);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                }
            }
        };

        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION} ,MY_LOCATION_REQUEST_CODE);
        }else{
            Location location = locationmanager.getLastKnownLocation(locationmanager.NETWORK_PROVIDER);
            if(location!=null){
                lat=location.getLatitude();
                lon=location.getLongitude();
                text.setText(lat+" "+lon);
            }else{
                Toast.makeText(this, "Unable to find", Toast.LENGTH_SHORT).show();
            }
        }




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
        if (v == signout) {
            signout();
        }
       /* if (v == currentlocation) {
            herelocation(lat,lon);
        }*/
    }

    private void herelocation(Double lat, Double lon) {

        geocoder=new Geocoder(this,Locale.getDefault());

        try {
            addressList=geocoder.getFromLocation(lat,lon,1);
            String city=addressList.get(0).getAddressLine(0);
            String area=addressList.get(0).getLocality();
            String village=addressList.get(0).getAdminArea();
            String country=addressList.get(0).getCountryName();
            String post=addressList.get(0).getPostalCode();

            String fulladdress=city+","+area+","+village+","+country+","+post;

            text.setText(fulladdress);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_LOCATION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION} ,MY_LOCATION_REQUEST_CODE);
                }else{
                    Location location = locationmanager.getLastKnownLocation(locationmanager.NETWORK_PROVIDER);
                    if(location!=null){
                        lat=location.getLatitude();
                        lon=location.getLongitude();
                        text.setText(lat+" "+lon);
                    }else{
                        Toast.makeText(this, "Unable to find", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }
}
