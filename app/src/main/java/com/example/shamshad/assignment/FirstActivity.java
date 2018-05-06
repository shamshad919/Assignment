package com.example.shamshad.assignment;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.SecureCacheResponse;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.jar.*;
import java.util.jar.Manifest;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private Button signout;
    private Button submit;
    private Button next;
    private ImageView currentlocation;
    Place address;
    private TextView text;
    Geocoder geocoder;

    private Button recordbtn;
    private TextView recordlabel;

    Double lat;
    Double lon;
    List<Address> addressList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int MY_PERMISSION_CODE = 7171;
    private static final int PLAY_SERICES_RESOLUTION_REQUEST = 7172;
    boolean mRequestingLocaionUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private MediaRecorder mRecorder;
    private String mFileName=null;
    private static final String LOG_TAG="Record log";
    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private DatabaseReference mRef;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getSupportActionBar().hide();

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        signout = (Button) findViewById(R.id.signout);
        submit= (Button) findViewById(R.id.submit);
        next= (Button) findViewById(R.id.next);
        currentlocation= (ImageView) findViewById(R.id.currentlocation);
        signout.setOnClickListener(this);
        currentlocation.setOnClickListener(this);
        text = (TextView) findViewById(R.id.text_view);
        recordbtn= (Button) findViewById(R.id.recordbtn);
        recordlabel= (TextView) findViewById(R.id.recordlabel);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                }
            }
        };



        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter the Address");
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHintTextColor(Color.parseColor("#000000"));
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.rounded_edittext_button));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                address = place;

            }

            @Override
            public void onError(Status status) {

            }
        });

        mFileName= Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        mStorage= FirebaseStorage.getInstance().getReference();
        mProgress=new ProgressDialog(this);

        recordbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){

                    startRecording();
                    recordlabel.setText("Recording started.....");

                }else if(event.getAction()==MotionEvent.ACTION_UP){

                    stopRecording();
                    recordlabel.setText("Recording stopped......");

                }

                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                mRef=FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).push();
                uploadAudio();
                mRef.child("address").setValue(address.getName().toString()+","+address.getAddress().toString());
                ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText(null);


            }
        });
        next.setOnClickListener(this);



    }
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;


    }

    private void uploadAudio() {

        mProgress.setMessage("Uploadig...");
        mProgress.show();
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        Random random=new Random();
        int aNumber = (int) (20 * Math.random()) + 1;
        StorageReference filepath=mStorage.child("Audio").child(user.getUid()).child(user.getUid()+aNumber+".mp3");
        Uri uri=Uri.fromFile(new File(mFileName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                mRef.child("audio").setValue(downloadUrl.toString());
                recordlabel.setText("Uploading finished");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                recordlabel.setText("Failed"+e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                mProgress.setMessage("Uploading");

            }
        });
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "This Device is not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                    }
                }
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
       /* if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        /*LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        /*if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }*/
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
        if (v == currentlocation) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, MY_PERMISSION_CODE);
            } else {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                    createLocationRequest();
                }
            }

            displayLocation();
        }
        if(v==next){
            Intent intent=new Intent(this,SecondActivity.class);
            startActivity(intent);
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            herelocation(lat,lon);
        } else {
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            Toast.makeText(this, "Could not get the location", Toast.LENGTH_SHORT).show();
        }
    }

    private void herelocation(Double lat, Double lon) {

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            String city = addressList.get(0).getAddressLine(0);
            String area = addressList.get(0).getLocality();
            String village = addressList.get(0).getAdminArea();
            String country = addressList.get(0).getCountryName();
            String post = addressList.get(0).getPostalCode();

            String fulladdress = city + "," + area + "," + village + "," + country + "," + post;
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText(fulladdress);
            ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
            ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.rounded_edittext_button));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    address = place;
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("address");
                    mRef.setValue(address);

                }

                @Override
                public void onError(Status status) {

                }
            });


            text.setText(fulladdress);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        if (mRequestingLocaionUpdates) {
            startLocationUpdate();
        }
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        displayLocation();
    }
}
