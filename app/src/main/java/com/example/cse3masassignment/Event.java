package com.example.cse3masassignment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Event {
    private String eventID;
    private String eventName;
    private LatLng geoPoint;
    private String eventLocation;
    private String eventDate;
    private ArrayList<Band> eventSetList  = new ArrayList<Band>();


    public Event(String eventID,String eventName, String eventLocation, String eventDate, LatLng geoPoint){
        this.eventID = eventID;
        this.eventName = eventName;
        this. eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.geoPoint = geoPoint;
    }
    public Event(String eventName, String eventLocation, String eventDate, LatLng geoPoint, Map<String, Object> eventSetList){
        this.eventName = eventName;
        this. eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.geoPoint = geoPoint;
        // TODO: 15/05/2024
        //  cycle through eventSetList map and add them to the arraylist eventSetList
    }


    //region Encapsulation
    public void addBand(Band band){
        eventSetList.add(band);
    }
    public void addBand(String bandID, String bandName, String bandGenre, String bandWebsite){
        Band tempBand = new Band(bandID, bandName,bandGenre,bandWebsite);
        eventSetList.add(tempBand);
    }
    public ArrayList<Band> getEventSetList() {
        return eventSetList;
    }
    public String getEventID() {
        return eventID;
    }

    public LatLng getGeoPoint() {
        return geoPoint;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setGeoPoint(LatLng geoPoint) {
        this.geoPoint = geoPoint;
    }
    public void setGeoPoint(float Lat, float Lng) {
        this.geoPoint = new  LatLng(Lat,Lng);
    }
    //endregion
}
