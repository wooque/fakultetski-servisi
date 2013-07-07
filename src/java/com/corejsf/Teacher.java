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
    private LinkedList<LinkedList<Demonstrator>> eligibleStudents;

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
    
    public LinkedList<LinkedList<Demonstrator>> getEligibleStudents() { return eligibleStudents; }
    public void setEligibleStudents(LinkedList<LinkedList<Demonstrator>> eligibleStudents) { this.eligibleStudents = eligibleStudents; }
    
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
        try{
            App.getInstance().loadLabs(this);
        } catch (DBError dbe) {
            return "error";
        }
        if(outcome){
            nav.setPage("/sections/start/labs.xhtml");
            lab.clear();
        } else {
            nav.setPage("/sections/start/addLab.xhtml");
        }
        return "start";
    }

    public String closeLab(Nav nav) {
        currlab.closeLab();
        try {
             App.getInstance().closeLab(currlab);
        } catch (DBError dbe){
            return "error";
        }
        nav.setPage("/sections/start/labs.xhtml");
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
    
    public LinkedList<Lab> getFutureLabs() {
        LinkedList<Lab> future = new LinkedList<Lab>();
        for(Lab lab: labs){
            if(!lab.isPast() && !lab.isClosed()){
                future.add(lab);
            }
        }
        return future;
    }
    
    public LinkedList<Lab> getPastLabs() {
        LinkedList<Lab> past = new LinkedList<Lab>();
        for(Lab lab: labs){
            if(lab.isPast() && !lab.isClosed()){
                past.add(lab);
            }
        }
        return past;
    }
    
    public LinkedList<Lab> getClosedLabs() {
        LinkedList<Lab> closed = new LinkedList<Lab>();
        for(Lab lab: labs){
            if(lab.isClosed()){
                closed.add(lab);
            }
        }
        return closed;
    }
    
    public String loadEligibleStudents(String username, Nav nav) {
        try{
            App.getInstance().loadEligibleStudents(username, this);
        } catch (DBError dbe) {
            return "error";
        }
        nav.setPage("/sections/start/addDemonstrator.xhtml");
        return "start";
    }
        
    public void searchEligibleStudents(Search search) {
        if(search.getSelectedCourse() == null) {
            search.setResult(new LinkedList<Demonstrator>());
            return;
        }
        int selected = -1;
        for(int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getId() == search.getSelectedCourse().getId()){
                selected = i;
            }
        }
        LinkedList<Demonstrator> result = new LinkedList<Demonstrator>();
        if(selected != -1){
            for(Demonstrator d: eligibleStudents.get(selected)){
                boolean nameEq = false;
                boolean surnameEq = false;
                if((search.getName()==null || search.getName().isEmpty())){
                    nameEq = true;
                } else if(search.getName().equals(d.getUser().getName())){
                    nameEq = true;
                }
                if(search.getSurname() == null || search.getSurname().isEmpty()){
                    surnameEq = true;
                } else if(search.getSurname().equals(d.getUser().getSurname())){
                    surnameEq = true;
                }
                if(nameEq && surnameEq){
                    result.add(d);
                }
            }
        }
        search.setResult(result);
    }
    
    public String addNewDemonstrator(Search s, Demonstrator d) {
        try{
            App.getInstance().addNewDemonstrator(s.getSelectedCourse(), d);
        } catch (DBError dbe) {
            return "error";
        }
        int selected = -1;
        for(int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getId() == s.getSelectedCourse().getId()){
                selected = i;
            }
        }
        eligibleStudents.get(selected).remove(d);
        searchEligibleStudents(s);
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
