package com.example.brian.vetlocator;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class History implements ClusterItem {


    private String  animals_treated;
    private double lat,lng;
//    private String test2 = "testing....";


        public String getAnimals_treated() {
            return animals_treated;
        }

    public Double getLat() {
        return lat;
//        return test2;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }



    public void setAnimals_treated(String animals_treated) {
        this.animals_treated = animals_treated;
    }



    public History(String animals_treated, Double lat, Double lng){
        this.animals_treated = animals_treated;
        this.lat = lat;
        this.lng = lng;

    }



    public History(){

    }

    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}


