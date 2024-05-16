package com.example.cse3masassignment;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ReviewScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedEventID;
    private ArrayList<Review> reviewsList =  new ArrayList<Review>();

    private Button reviewBtn;

    private ListView reviewListView;
    private ReviewListAdapter adapter;


    //debug counter
    int counter = 0 ;

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

        //Initialising the UI Elements
        reviewBtn = findViewById(R.id.reviews_btn);
        reviewListView = findViewById(R.id.reviewsListView);
        adapter = new ReviewListAdapter(ReviewScreen.this, reviewsList);
        reviewListView.setAdapter(adapter);

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createReviewScreen = new Intent(ReviewScreen.this, CreateReviewScreen.class);
                createReviewScreen.putExtra("SELECTED_EVENT", selectedEventID);
                startActivity(createReviewScreen);
            }
        });



        //Back Button Return to Previous Page And Give Back Select Event ID
        ImageButton btn_back = findViewById(R.id.btn_Back);
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
        db.collection("Events").document(selectedEventID).collection("Reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userName = document.getString("reviewUserName");
                        String comment =  document.getString("reviewComment");
                        int rating =  Integer.parseInt(document.get("reviewRating").toString());
                        Review tempR = new Review(userName,comment, rating);
                        adapter.add(tempR);
                        Log.i("REVIEW-GET DATA", "onComplete: " + document.getData());
                    }
                } else {
                    Log.d("REVIEW-ERROR", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}