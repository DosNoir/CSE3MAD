package com.example.cse3masassignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.CollectionReference;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EventScreen extends AppCompatActivity {
    private  TextView eventHeadingTxV;
    private TextView eventInfoTxV;
    private TextView venueNameTxV;
    private TextView venueWebsiteTxV;
    private TextView venueEmailTxV;
    private TextView venuePhoneNumberTxV;
    private Button setListBtn;
    private Button reviewBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedEventID;
    private Event selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Get Firebase Instance Auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        selectedEventID = getIntent().getStringExtra("SELECTED_EVENT");
        getSelectedEvent();

        //Initializing UI Elements
        setListBtn = findViewById(R.id.set_list_btn);
        reviewBtn = findViewById(R.id.reviews_btn);
        eventHeadingTxV = findViewById(R.id.event_heading);
        eventInfoTxV = findViewById(R.id.event_info);
        venueNameTxV = findViewById(R.id.venue_name);
        venueWebsiteTxV = findViewById(R.id.venue_website);
        venueEmailTxV = findViewById(R.id.venue_email);
        venuePhoneNumberTxV = findViewById(R.id.venue_phonenumber);
        ImageButton btn_back = findViewById(R.id.btn_Back);
        //=========================================================



        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewScreen = new Intent(EventScreen.this, ReviewScreen.class);
                reviewScreen.putExtra("SELECTED_EVENT", selectedEventID);
                startActivity(reviewScreen);
            }
        });
        setListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setListScreen = new Intent(EventScreen.this, SetListScreen.class);
                setListScreen.putExtra("SELECTED_EVENT", selectedEventID);
                startActivity(setListScreen);
            }
        });

        //Back Button to Home Screen (onClick)
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeScreen = new Intent(EventScreen.this, HomeScreen.class);
                startActivity(homeScreen);
            }
        });
    }

    //Get updated selected event from database then once it has gotten it update the ui.
    //if you dont do it this way the application will crash because it will set the text views before it has completed, getting data from the data base
    private void getSelectedEvent(){
        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getId().equals(selectedEventID)) {
                            String tempId = document.getId();
                            String tempName = document.getString("eventName");
                            String tempLocation = document.getString("eventLocation");
                            String tempDate = document.getString("eventDate");

                            GeoPoint tempPoint = document.getGeoPoint("eventGeoPoint");
                            LatLng tempLatLng = new LatLng(tempPoint.getLatitude(), tempPoint.getLongitude());

                            // TODO: 15/05/2024 get venue information from form
                            getVenueInfo();
                            
                            selectedEvent = new Event(tempId, tempName, tempLocation, tempDate, tempLatLng);
                            eventHeadingTxV.setText(selectedEvent.getEventName());
                            eventInfoTxV.setText(selectedEvent.getEventLocation() + " | " + selectedEvent.getEventDate());
                            
                        }
                    }
                }else {
                    Log.w("READTESTFIREBASE", "Error getting data", task.getException());
                }
            }
        });
    }
    private void getVenueInfo(){
        db.collection("Events").document(selectedEventID).collection("Venue").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        venueNameTxV.setText(document.getString("venueName"));
                        venueEmailTxV.setText(document.getString("venueEmail"));
                        venuePhoneNumberTxV.setText(document.getString("venuePhone"));
                        venueWebsiteTxV.setText(document.getString("venueWebsite"));
                    }
                } else {
                    Log.d("VENUETAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}



