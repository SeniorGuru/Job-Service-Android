package com.wakeapp.gigsfinder.employeer.jobs.models;

import java.util.ArrayList;

public class subcatDiloglist {

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String>ids=new ArrayList<>();
    private ArrayList<Boolean>hasChild=new ArrayList<>();
    private ArrayList<Boolean>hasTemplate=new ArrayList<>();
    private ArrayList<Boolean>hasSelected=new ArrayList<>();
    private String id;
    private String name;

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public ArrayList<Boolean> getHasChild() {
        return hasChild;
    }

    public void setHasChild(ArrayList<Boolean> hasChild) {
        this.hasChild = hasChild;
    }

    public ArrayList<Boolean> getHasTemplate() {
        return hasTemplate;
    }

    public void setHasTemplate(ArrayList<Boolean> hasTemplate) {
        this.hasTemplate = hasTemplate;
    }

    public ArrayList<Boolean> getHasSelected() {
        return hasSelected;
    }

    public void setHasSelected(ArrayList<Boolean> hasSelected) {
        this.hasSelected = hasSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
