package com.example.cse3masassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateReviewScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String selectedEventID;

    private EditText nameEditText;
    private EditText reviewEditText;
    private Button postReviewBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_review_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Get Firebase Instance Auth and db Shit
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        selectedEventID = getIntent().getStringExtra("SELECTED_EVENT");

        //Initialising Vars
        nameEditText = findViewById(R.id.nameEditText);
        reviewEditText = findViewById(R.id.reviewEditText);
        postReviewBtn = findViewById(R.id.postReviewBtn);

        //Set UserName to Name Edit Test
        nameEditText.setText(auth.getCurrentUser().getDisplayName());

        //Post Review Button Listener
        postReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = reviewEditText.getText().toString().trim();
                String user = nameEditText.getText().toString().trim();

                //Test If Review has any data if not let user no they cant do stupid stuff :)
                if (user.isEmpty()) {
                    Toast.makeText(CreateReviewScreen.this, "Please Enter A Name.", Toast.LENGTH_SHORT).show();
                    nameEditText.requestFocus();
                    return;
                }
                if (text.isEmpty()){
                    Toast.makeText(CreateReviewScreen.this, "Please Enter A Review.", Toast.LENGTH_SHORT).show();
                    reviewEditText.requestFocus();
                    return;
                }
                Map<String,Object> data = new HashMap<>();
                data.put("reviewComment",text);
                data.put("reviewUserName", user);
                data.put("reviewRating", 0);
                data.put("timestamp", FieldValue.serverTimestamp());
                nameEditText.getText().clear();
                reviewEditText.getText().clear();
                db.collection("Events").document(selectedEventID).collection("Reviews").add(data);
                ohShitGoBack();
            }
        });
        //Back Button Return to Previous Page And Give Back Select Event ID
        ImageButton btn_back = findViewById(R.id.btn_Back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ohShitGoBack();
            }
        });
    }
    private void ohShitGoBack(){
        Intent eventScreen = new Intent(CreateReviewScreen.this, ReviewScreen.class);
        eventScreen.putExtra("SELECTED_EVENT",selectedEventID);
        startActivity(eventScreen);
    }
}