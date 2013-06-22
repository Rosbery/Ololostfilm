package com.tryrosberry.ololostfilm.ui.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Season implements Serializable {

    public String name;
    public ArrayList<Series> series = new ArrayList<Series>();
    public int seriesCounter = 0;

    public Season(){}

    public void setSeries(ArrayList<Series> series){
        this.series = series;
    }

}
