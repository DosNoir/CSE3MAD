package com.example.cse3masassignment;

import java.net.IDN;
import java.util.Map;

public class Band {
    private String bandID;
    private String bandName;
    private String bandGenre;
    private String bandWebsite;
    private String bandPicture;
    public Band(String bandID, String bandName, String bandGenre, String bandWebsite){
        this.bandID = bandID;
        this.bandName = bandName;
        this.bandGenre = bandGenre;
        this.bandWebsite = bandWebsite;

        //Default Picture here
        bandPicture = null;
    }
    public Band(String bandID, String bandName, String bandGenre, String bandWebsite, String bandPicture){
        this.bandID = bandID;
        this.bandName = bandName;
        this.bandGenre = bandGenre;
        this.bandWebsite = bandWebsite;
        this.bandPicture = bandPicture;
    }

    public void setBandGenre(String bandGenre) {
        this.bandGenre = bandGenre;
    }
    public void setBandName(String bandName) {
        this.bandName = bandName;
    }
    public void setBandWebsite(String bandWebsite) {
        this.bandWebsite = bandWebsite;
    }
    public String getBandName(){
        return this.bandName;
    }
    public String getBandGenre() {
        return bandGenre;
    }
    public String getBandWebsite() {
        return bandWebsite;
    }
}
