package com.example.chars.chars;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID id;
    private String title;
    private String content;
    private Date date;
    private Boolean isChecked = false;
    private String suspect;

    public String getPhotoFilename(){
        return "IMG" + getId().toString() + ".jpg";
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    public Crime(UUID uuid) {
        id = uuid;
        date = new Date();
    }

    public Crime(){
        this(UUID.randomUUID());
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public Crime setChecked(Boolean checked) {
        isChecked = checked;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public Crime setContent(String content) {
        this.content = content;
        return this;
    }

    public void setDate(Date d) {
        this.date = d;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Crime setTitle(String title) {
        this.title = title;
        return this;
    }
}
