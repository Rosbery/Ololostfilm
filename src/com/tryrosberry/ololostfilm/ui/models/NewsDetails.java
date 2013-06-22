package com.tryrosberry.ololostfilm.ui.models;


import java.io.Serializable;

public class NewsDetails implements Serializable {

    public static enum detType{TEXT,PICTURE,UNKNOWN};

    public detType type = detType.UNKNOWN;
    public String content;

    public NewsDetails(){}

}
