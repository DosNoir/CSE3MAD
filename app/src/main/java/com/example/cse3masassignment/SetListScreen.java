package com.example.cse3masassignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SetListScreen extends AppCompatActivity {

    private ListView setListView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedEventID;
    private Event selectedEvent;

    private ArrayList<Band> bandList;
    private ArrayAdapter<Band> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_list_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Get Firebase Instance Auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        selectedEventID = getIntent().getStringExtra("SELECTED_EVENT");

        // Band list
        setListView = findViewById(R.id.set_list_view);
        bandList = new ArrayList<>();
        getSelectedBands();

        ImageButton btn_back = findViewById(R.id.btn_Back);

        //Back Button to Event Screen (onClick)
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventScreen = new Intent(SetListScreen.this, EventScreen.class);
                eventScreen.putExtra("SELECTED_EVENT", selectedEventID);
                startActivity(eventScreen);
            }
        });
    }

    private void getSelectedBands() {
        db.collection("Events").document(selectedEventID).collection("Bands")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String bandName = document.getString("bandName");
                                String bandGenre = document.getString("bandGenre");
                                String bandWebsite = document.getString("bandWebsite");

                                Band band = new Band(document.getId(), bandName, bandGenre, bandWebsite);
                                bandList.add(band);

                                Log.i("BANDTAG", document.getData().toString());
                            }

                            // Add bands from ArrayList to ListView
                            arrayAdapter = new ArrayAdapter<>(SetListScreen.this, android.R.layout.simple_list_item_1, bandList);
                            setListView.setAdapter(arrayAdapter);

                        } else {
                            Log.w("READTESTFIREBASE", "Error getting data", task.getException());
                        }
                    }
                });
    }
}