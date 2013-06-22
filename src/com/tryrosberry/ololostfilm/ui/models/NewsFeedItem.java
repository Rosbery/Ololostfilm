package com.tryrosberry.ololostfilm.ui.models;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsFeedItem implements Serializable {

    public String title;
    public String description;
    public String image;
    public String link;
    ArrayList<NewsDetails> details = new ArrayList<NewsDetails>();

    public NewsFeedItem(){}

    public void setNewsDetails(ArrayList<NewsDetails> details){
        this.details = details;
    }

}
