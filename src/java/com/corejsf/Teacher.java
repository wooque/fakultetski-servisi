package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean(name = "teacher")
public class Teacher implements Serializable{
    
    private String username;
    private String name;
    private String surname;
    private LinkedList<Course> courses;
    private Course currentCourse;
    private LinkedList<LinkedList<Apply>> applies;
    private boolean showCoursesInMenu;
    private LinkedList<Lab> labs;
    private Lab currlab;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public LinkedList<Course> getCourses() { return courses; }
    public void setCourses(LinkedList<Course> courses) { 
        this.courses = courses;
    }

    public LinkedList<LinkedList<Apply>> getApplies() { return applies; }
    public void setApplies(LinkedList<LinkedList<Apply>> applies) { this.applies = applies; }
    
    public boolean isShowCoursesInMenu() { return showCoursesInMenu; }
    public void setShowCoursesInMenu(boolean showCoursesInMenu) { this.showCoursesInMenu = showCoursesInMenu; }
    
    public Course getCurrentCourse() { return currentCourse; }
    public void setCurrentCourse(Course currentCourse, Nav nav) { 
        this.currentCourse = currentCourse; 
        nav.setPage("/sections/start/applies.xhtml");
    }
    
    public LinkedList<Lab> getLabs() { return labs; }
    public void setLabs(LinkedList<Lab> labs) { this.labs = labs; }
    
    public Lab getCurrlab() { return currlab; }
    public void setCurrlab(Lab currlab) { this.currlab = currlab; }
    
    public String getAppliesNum(){
        int num = 0;
        for(int i = 0; i < applies.size(); i++){
            num += applies.get(i).size();
        }
        return Integer.toString(num);
    }
    public String getCourseApplies(Course c){
        for(int i = 0; i < applies.size(); i++){
            Course courseTemp = courses.get(i);
            if(courseTemp.getDepartment().equals(c.getDepartment()) && (courseTemp.getTeachingYear() == c.getTeachingYear())
                && courseTemp.getCode().equals(c.getCode())){
                return Integer.toString(applies.get(i).size());
            }
        }
        return "ERROR";
    }
    
    public LinkedList<Apply> getCurrentApplies() {
        int i = courses.indexOf(currentCourse);
        return applies.get(i);
    }
        
    public  List<Teacher> completeTeacher(String query) {
        LinkedList<Teacher> teachers = new LinkedList<Teacher>();
        try {
             teachers = App.getInstance().completeTeacher(query);
        } catch (DBError dbe){}
        return teachers;
    }
    
    public String loadApplies(Nav nav){
        try{
            App.getInstance().loadApplies(this);
        } catch (DBError dbe){
            return "error";
        }
        showCoursesInMenu = !showCoursesInMenu;
        nav.setPage("/sections/start/appliesStart.xhtml");
        return "start";
    }
    
    public void accept(Apply apply) {
        getCurrentApplies().remove(apply);
        try {
            App.getInstance().accept(apply);
        } catch (DBError dbe) {
        }
    }
    
    public void reject(Apply apply) {
        getCurrentApplies().remove(apply);
        try {
            App.getInstance().reject(apply);
        } catch (DBError dbe) {
        }
    }
    
    public String saveLab(Lab lab, Nav nav){
        boolean outcome;
        try {
             outcome = App.getInstance().saveLab(lab);
        } catch (DBError dbe){
            return "error";
        }
        if(outcome){
            nav.setPage("/sections/start/labs.xhtml");
            labs.add(lab);
        } else {
            nav.setPage("/sections/start/addLab.xhtml");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Lab practice already exist", "IGNORED"));
        }
        return "start";
    }

    
    public void showLab(Lab lab, Nav nav) {
        currlab = lab;
        nav.setPage("/sections/start/labInfo.xhtml");
    }
    
    public String loadLabs(Nav nav) {
        try{
            App.getInstance().loadLabs(this);
        } catch (DBError dbe) {
            return "error";
        }
        nav.setPage("/sections/start/labs.xhtml");
        return "start";
    }
    
    @Override
    public String toString() {
        return new TeacherConverter().getAsString(null, null, this);
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.surname != null ? this.surname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Teacher other = (Teacher) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.surname == null) ? (other.surname != null) : !this.surname.equals(other.surname)) {
            return false;
        }
        return true;
    }
}
