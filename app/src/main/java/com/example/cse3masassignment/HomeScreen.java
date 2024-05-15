package com.example.cse3masassignment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.VibrationAttributes;
import android.os.Vibrator;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    MapView mapView;
    GoogleMap googleMap;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocation;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ArrayList<Event> events = new ArrayList<Event>();
    private String markerClickSelectionEventId;

    private ArrayList<Marker> myMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Get The FireBase Auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Display Map and find last location
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        //Back Button SignOut
        ImageButton btn_back = findViewById(R.id.btn_Back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backButtonSignOut();
            }
        });
        ImageButton btn_filter_settings = findViewById(R.id.btn_filter_settings);
        btn_filter_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo Finish This Buttons Implementation (Create Popup and sliders bs)
                //Todo find vector of the real figma icon (glhf)
                //Todo Database connections / query's
                //Todo Populate Map with database and make the on map marker click pop up thing
                //Todo create private function out of this implementation to clean the code base up!
            }
        });
    }

    //region  implements map functions getLastLocation(),  onMapReady(), onRequestPermission()
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocation.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    Log.d("Gigle", "onSuccess: " + location.toString());
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                    assert mapFragment != null;
                    mapFragment.getMapAsync((OnMapReadyCallback) HomeScreen.this);
                }
                if(location == null)
                {
                    Log.e("Gigle","Location = Null");
                }

            }
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        //Current user Location
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

        loadDataAddMarkers();
    }


    private void loadDataAddMarkers(){
        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("READTESTFIREBASE", document.getId()+ " => " + document.getData());
                        String tempId = document.getId();
                        String tempName = document.getString("eventName");
                        String tempLocation = document.getString("eventLocation");
                        String tempDate = document.getString("eventDate");

                        GeoPoint tempPoint = document.getGeoPoint("eventGeoPoint");
                        LatLng tempLatLng = new LatLng(tempPoint.getLatitude(), tempPoint.getLongitude());

                        MarkerOptions markerOptions =  new MarkerOptions();
                        markerOptions.position(tempLatLng);
                        markerOptions.title(tempName);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                        googleMap.addMarker(markerOptions);

                        Event tempEvent= new Event(tempId, tempName, tempLocation, tempDate, tempLatLng);
                        events.add(tempEvent);
                    }
                }else {
                    Log.w("READTESTFIREBASE", "Error getting data", task.getException());
                }
            }
        });
    }
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.i("MARKERTAG", "onMarkerClick: "+ marker.getTitle());
        Event selectedEvent;
        if(!marker.getTitle().equals("Current Location"))
        {
            for (Event x: events) {
                if(x.getEventName().equals(marker.getTitle())){
                    selectedEvent = x;
                    new AlertDialog.Builder(HomeScreen.this)
                            .setTitle(selectedEvent.getEventName())
                            .setMessage("More Info?")
                            .setIcon(android.R.drawable.star_on)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent eventScreen = new Intent(HomeScreen.this, EventScreen.class);
                                    eventScreen.putExtra("SELECTED_EVENT", selectedEvent.getEventID());
                                    startActivity(eventScreen);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    break;
                }
            }

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Location Permission is denied, please allow permission in settings", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //endregion map functions

    //region Back Button Sign Out
    private void backButtonSignOut(){
        new AlertDialog.Builder(HomeScreen.this)
                .setTitle("Sign Out?")
                .setMessage("Do you really want to sign out?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(HomeScreen.this, "Signing Out: " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        GoogleSignInOptions option = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.client_id))
                                .requestEmail()
                                .build();
                        GoogleSignInClient g =  GoogleSignIn.getClient(HomeScreen.this, option);
                        g.signOut();

                        Intent signIn = new Intent(HomeScreen.this, MainActivity.class);
                        startActivity(signIn);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    //endregion


}