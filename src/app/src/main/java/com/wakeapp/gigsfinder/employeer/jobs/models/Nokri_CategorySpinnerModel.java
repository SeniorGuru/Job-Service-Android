package com.wakeapp.gigsfinder.employeer.jobs.models;

import java.util.ArrayList;

/**
 * Created by Glixen Technologies on 01/03/2018.
 */

public class Nokri_CategorySpinnerModel {
    private ArrayList<String>names;
    private ArrayList<String>ids;
    private ArrayList<Boolean>hasChild;
    private ArrayList<Boolean>hasTemplate;
    private String id;
    private String name;



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

    public ArrayList<Boolean> getHasTemplate() {
        return hasTemplate;
    }

    public void setHasTemplate(ArrayList<Boolean> hasTemplate) {
        this.hasTemplate = hasTemplate;
    }

    public Nokri_CategorySpinnerModel() {

        names = new ArrayList<>();
        ids = new ArrayList<>();
        hasChild = new ArrayList<>();
        hasTemplate=new ArrayList<>();

    }

    public ArrayList<Boolean> getHasChild() {
        return hasChild;
    }

    public void setHasChild(ArrayList<Boolean> hasChild) {
        this.hasChild = hasChild;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void addNames(String name){
       names.add(name);
    }
    public void addIds(String id){
        ids.add(id);
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
}
