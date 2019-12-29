package com.avenashp.auratest.ModelClass;

public class LabelsModel {
    String label;
    Float accuracy;

    public LabelsModel(){

    }

    public LabelsModel(String label, Float accuracy) {
        this.label = label;
        this.accuracy = accuracy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }
}
