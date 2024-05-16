package com.example.cse3masassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedEventID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Get Firebase Instance Auth and db Shit
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        selectedEventID = getIntent().getStringExtra("SELECTED_EVENT");
        getReviews();

        //Initalising the UI Elements

        ImageButton btn_back = findViewById(R.id.btn_Back);


        //Back Button Return to Previous Page And Give Back Select Event ID
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventScreen = new Intent(ReviewScreen.this, EventScreen.class);
                eventScreen.putExtra("SELECTED_EVENT",selectedEventID);
                startActivity(eventScreen);
            }
        });

    }


    private void getReviews(){
        // TODO: 16/05/2024 do shit glhf :)
    }
}