package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "student")
@SessionScoped
public class Student implements Serializable{
    
    private String department;
    private Integer year;
    private Float GPA;
    private LinkedList<Course> availableCourses;
    private LinkedList<Course> selectedCourses;
    private boolean applied;
    private LinkedList<Lab> invitations;
    private LinkedList<Lab> acceptedInvitations = new LinkedList<Lab>();
    private String rejectedComment;
    private Lab currLab;

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
    
    public LinkedList<Lab> getInvitations() { return invitations; }
    public void setInvitations(LinkedList<Lab> invitation) { this.invitations = invitation; }

    public LinkedList<Lab> getAcceptedInvitations() { return acceptedInvitations; }
    public void setAcceptedInvitations(LinkedList<Lab> acceptedInvitations) { this.acceptedInvitations = acceptedInvitations; }
    
    public String getRejectedComment() { return rejectedComment; }
    public void setRejectedComment(String rejectedComment) { this.rejectedComment = rejectedComment; }
    
    public Lab getCurrLab() { return currLab; }
    public void setCurrLab(Lab currLab) { this.currLab = currLab; }
     
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
    
    public String acceptLab(String username, Lab lab) {
        invitations.remove(lab);
        acceptedInvitations.add(lab);
        try{
            App.getInstance().acceptLab(username, this, lab);
        } catch(DBError dbe){
            return "error";
        }
        return "start";
        
    }
    
    public String rejectLab(String username) {
        invitations.remove(currLab);
        try{
            App.getInstance().rejectLab(username, this, currLab);
        } catch(DBError dbe){
            return "error";
        }
        RequestContext.getCurrentInstance().execute("dlg.hide()");
        return "start";
    }
}
