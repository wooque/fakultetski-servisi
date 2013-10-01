package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@SessionScoped
@ManagedBean(name = "teacher")
public class Teacher implements Serializable{
    
    private String username;
    private String name;
    private String surname;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
        
    public  List<Teacher> completeTeacher(String query) {
        LinkedList<Teacher> teachers = new LinkedList<Teacher>();
        try {
             teachers = App.getInstance().completeTeacher(query);
        } catch (DBError dbe){}
        return teachers;
    }

}
