package com.example.brian.vetlocator.historyRecyclerView;

public class HistoryObject {

    private String treatmentId;
    private String time;


    public HistoryObject(String treatmentId, String time){
        this.treatmentId = treatmentId;
        this.time = time;

    }

    public String getTreatmentId() {
        return treatmentId;
    }
    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }
}
