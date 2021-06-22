package com.wakeapp.gigsfinder.guest.search.models;

public class Nokri_CandidateSearchModel {

    private String title;
    private String location;
    private String type;
    private String available;
    private boolean isSearchOnly;

    public boolean isSearchOnly() {
        return isSearchOnly;
    }

    public void setSearchOnly(boolean searchOnly) {
        isSearchOnly = searchOnly;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Nokri_CandidateSearchModel{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", available='" + available + '\'' +
                ", isSearchOnly=" + isSearchOnly +
                '}';
    }
}
