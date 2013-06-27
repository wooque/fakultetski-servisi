package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@SessionScoped
@ManagedBean(name = "search")
public class Search implements Serializable{
    
    private String name;
    private String surname;
    private boolean allCourses;
    private boolean allMyCourses;
    private LinkedList<Course> courses;
    private LinkedList<Elem> result;
    
    public static final class Elem {
        private User user;
        private Student student;

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }

        public Student getStudent() { return student; }
        public void setStudent(Student student) { this.student = student; }
        
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public boolean isAllCourses() { return allCourses; }
    public void setAllCourses(boolean allCourses) { this.allCourses = allCourses; }

    public boolean isAllMyCourses() { return allMyCourses; }
    public void setAllMyCourses(boolean allMyCourses) { this.allMyCourses = allMyCourses; }

    public LinkedList<Course> getCourses() { return courses; }
    public void setCourses(LinkedList<Course> courses) { this.courses = courses; }
    
    public LinkedList<Elem> getResult() { return result; }
    public void setResult(LinkedList<Elem> result) { this.result = result; }
    
    public String searchDemostrators(LinkedList<Course> courses) {
        try{
            App.getInstance().searchDemonstrators(this);
        } catch (DBError dbe){
            return "error";
        }
        return "start";
    }
    
}
