package com.example.cse3masassignment;

import android.provider.CalendarContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DBhandler {
    private FirebaseFirestore db;
    private ArrayList<Event> events = new ArrayList<Event>();

    public DBhandler(FirebaseFirestore db)
    {
        this.db =db;
        loadData();
    }

    private void loadData(){
        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("READTESTFIREBASE", document.getId()+ " => " + document.getData());
                        //TODO  do check for selected event via passed through event info from home vscreen when implemented
                         String tempId = document.getId();
                         String tempName = document.getString("eventName");
                         String tempLocation = document.getString("eventLocation");
                         String tempDate = document.getString("eventDate");

                         GeoPoint tempPoint = document.getGeoPoint("eventGeoPoint");
                         LatLng tempLatLng = new LatLng(tempPoint.getLatitude(), tempPoint.getLongitude());

                        // TODO: 15/05/2024 When database implementation agreement has been agree'd
                        //  on implement add bands to setlist and venue to this shit glhf homie or future me using
                        //  Event(String eventName, String eventLocation, String eventDate, LatLng geoPoint, Map<String, Object> eventSetList)

                         Event tempEvent= new Event(tempId, tempName, tempLocation, tempDate, tempLatLng);
                         events.add(tempEvent);
                    }
                }else {
                    Log.w("READTESTFIREBASE", "Error getting data", task.getException());
                }
            }
        });
    }


    /*void testFunctionRead(){
        db.collection("Events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("READTESTFIREBASE", document.getId()+ " => " + document.getData());
                        //TODO  do check for selected event via passed through event info from homescreen when implemented
                        Map<String,Object> event = document.getData();

                        eventHeadingTxV.setText(event.get("eventName").toString());
                        eventInfoTxV.setText(event.get("eventLocation").toString() + " | " + event.get("eventDate").toString());
                    }
                }else {
                    Log.w("READTESTFIREBASE", "Error getting data", task.getException());
                }
            }
        });
    }*/

    public ArrayList<Event> getEvents() {
        return events;
    }
    //Returns Event if EventFound Returns Null if Not Found
    public Event getEvent(String eventID) {
        for(Event x : events){
            if(x.getEventID().equals(eventID)){
                return x;
            }
        }
        //Item is not found
        return null;
    }
}
