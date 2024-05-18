package com.example.cse3masassignment;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import com.opencsv.CSVReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class DBfiller {
    final private String TAG = "DB";
    final private String EVENTS_COLLECTION_PATH = "Events";
    final private String VENUE_COLLECTION_PATH = "Venue";
    final private String BANDS_COLLECTION_PATH = "Bands";

    private FirebaseFirestore db;
    private Context context;

    private List<String[]> eventsData;
    private List<String[]> bandsData;
    private List<String[]> venuesData;

    public DBfiller(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.eventsData = readCSVFromAssets("events.csv");
        this.bandsData = readCSVFromAssets("bands.csv");
        this.venuesData = readCSVFromAssets("venues.csv");
    }

    protected List<String[]> readCSVFromAssets(String filename) {

        List<String[]> data = null;

        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader csvReader = new CSVReader(inputStreamReader);

            data = csvReader.readAll();

            Log.d(TAG, "Read file: " + filename);

            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "ERROR READING FILE: " + filename);
        }

        return data;
    }
    protected void fill() {
        for (String[] row : eventsData) {
            addEvent(row);
        }
    }
    protected void addEvent(String[] row) {
        String eventID = row[0];

        // Define event data
        Map<String, Object> event = new HashMap<>();
        event.put("eventName", row[1]);
        event.put("eventDate", row[2]);
        event.put("eventLocation", row[3]);
        event.put("eventGeoPoint", new GeoPoint(Float.parseFloat(row[4]), Float.parseFloat(row[5])));

        // Add event document
        db.collection("Events").document(eventID)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event added with ID: " + eventID);
                    addBands(eventID);
                    addVenues(eventID);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event", e));
    }

    protected void addBands(String eventID) {
        CollectionReference bandsRef = db.collection(EVENTS_COLLECTION_PATH)
                .document(eventID)
                .collection(BANDS_COLLECTION_PATH);

        Log.d(TAG, "Adding bands...");

        for (String[] row : bandsData) {
            if ((row[0].equals(eventID)) && (row.length == 5)) {
                Map<String, Object> band = new HashMap<>();
                band.put("bandName", row[2]);
                band.put("bandGenre", row[3]);
                band.put("bandWebsite", row[4]);

                bandsRef.document(row[1]).set(band)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Band added: " + row[2]);
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding band", e));
            }
        }
    }

    protected void addVenues(String eventID) {
        CollectionReference venueRef = db.collection(EVENTS_COLLECTION_PATH)
                .document(eventID)
                .collection(VENUE_COLLECTION_PATH);

        Log.d(TAG, "Adding venues...");

        for (String[] row : venuesData) {
            if ((row[0].equals(eventID)) && (row.length == 5)) {
                Map<String, Object> venue = new HashMap<>();
                venue.put("venueName", row[2]);
                venue.put("venueEmail", row[3]);
                venue.put("venueWebsite", row[4]);


                venueRef.document(row[1]).set(venue)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Venue added: " + row[2]);
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding venue", e));
            }
        }
    }
}
