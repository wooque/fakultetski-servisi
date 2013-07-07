package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@SessionScoped
@ManagedBean(name = "search")
public class Search implements Serializable{
    
    private String name;
    private String surname;
    private Course selectedCourse;
    private boolean specific;
    private boolean myCourses;
    private LinkedList<Demonstrator> result;
    private static SelectItem[] modes = new SelectItem[]{
        new SelectItem("A", "All courses"),
        new SelectItem("M", "My courses"),
        new SelectItem("S", "Specific courses")
    };    

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Course getSelectedCourse() { return selectedCourse; }
    public void setSelectedCourse(Course selectedCourse) { this.selectedCourse = selectedCourse; }

    public boolean isSpecific() { return specific; }
    public void setSpecific(boolean specific) { this.specific = specific; }

    public boolean isMyCourses() { return myCourses; }
    public void setMyCourses(boolean myCourses) { this.myCourses = myCourses; }
    
    public LinkedList<Demonstrator> getResult() { return result; }
    public void setResult(LinkedList<Demonstrator> result) { this.result = result; }
    
    public SelectItem[] getModes() { return modes; }
    
    public void setMode(String mode) {
        myCourses = false;
        specific = false;
        if(mode.equals("M")){
            myCourses = true;
        } else if(mode.equals("S")) {
            specific = true;
        }
    }
    
    public String getMode() {
        if(myCourses){
            return "M";
        } else if(specific){
            return "S";
        } else {
            return "A";
        }
    }
    
    public String searchDemostrators(User user, Teacher teacher) {
        try{
            App.getInstance().searchDemonstrators(user, teacher, this);
        } catch (DBError dbe){
            return "error";
        }
        return "start";
    }

}
