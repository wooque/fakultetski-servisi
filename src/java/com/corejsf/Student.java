package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "student")
@SessionScoped
public class Student implements Serializable{
    
    private String department;
    private Integer year;
    private Float GPA;
    private LinkedList<Course> availableCourses;
    private LinkedList<Course> selectedCourses;
    private boolean applied;

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Float getGPA() { return GPA; }
    public void setGPA(Float GPA) { this.GPA = GPA; }
    
    public LinkedList<Course> getAvailableCourses() { return availableCourses; }
    public void setAvailableCourses(LinkedList<Course> availableCourses) { this.availableCourses = availableCourses; }
    
    public LinkedList<Course> getSelectedCourses() { return selectedCourses; }
    public void setSelectedCourses(LinkedList<Course> selectedCourses) { this.selectedCourses = selectedCourses; }
    
    public boolean isApplied() { return applied; }
    public void setApplied(boolean applied) { this.applied = applied; }
    
    public String loadSurveys(Nav nav){
        try{
            App.getInstance().loadSurveys(this);
        } catch (DBError dbe){
            return "error";
        }
        nav.setPage("/sections/start/surveys.xhtml");
        return "start";
    }
    
    public String apply(User user, Nav nav){
        try{
            App.getInstance().apply(user, this);
        } catch (DBError dbe){
            return "error";
        }
        setApplied(true);
        nav.setPage("/sections/start/applied.xhtml");
        return "start";
    }
}
