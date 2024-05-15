package com.example.cse3masassignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private FirebaseAuth auth;
    private FirebaseFirestore db;


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
        //Initializing
        eventHeadingTxV = findViewById(R.id.event_heading);
        eventInfoTxV = findViewById(R.id.event_info);
        venueNameTxV = findViewById(R.id.venue_name);
        venueWebsiteTxV = findViewById(R.id.venue_website);
        venueEmailTxV = findViewById(R.id.venue_email);
        venuePhoneNumberTxV = findViewById(R.id.venue_phonenumber);
        ImageButton btn_back = findViewById(R.id.btn_Back);
        //=========================================================
        Log.d("TESTFUNCTIOn", "onCreate: TestFuctionRun");
        testFunctionWrite();
        testFunctionRead();
        Log.d("TESTFUNCTIOn", "onCreate: TestFuctionRun");












        //Back Button to Home Screen (onClick)
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeScreen = new Intent(EventScreen.this, HomeScreen.class);
                startActivity(homeScreen);
            }
        });
    }


    void testFunctionRead(){
        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("READTESTFIREBASE", document.getId()+ " => " + document.getData());
                        //TODO  do check for selected event via passed through event info from homescreen when implemented
                        Map<String,Object> event = document.getData();

                        eventHeadingTxV.setText(document.getString("eventName"));
                        eventInfoTxV.setText(event.get("eventLocation").toString() + " | " + event.get("eventDate").toString());
                    }
                }else {
                    Log.w("READTESTFIREBASE", "Error getting data", task.getException());
                }
            }
        });
    }
    void testFunctionWrite(){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("FIREBASEDEBUG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FIREBASEDEBUG", "Error adding document", e);
                    }
                });
    }
}



