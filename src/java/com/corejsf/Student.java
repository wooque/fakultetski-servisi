package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
    private LinkedList<Payment> payments;
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
    
    public LinkedList<Payment> getPayments() { return payments; }
    public void setPayments(LinkedList<Payment> payments) { this.payments = payments; }
    
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
        for(Lab acc: acceptedInvitations){
            Date accBegin = acc.getBegin();
            Date accEnd = acc.getEnd();
            Date labBegin = lab.getBegin();
            Date labEnd = lab.getEnd();
            if(((labBegin.compareTo(accBegin)>=0) && (labBegin.compareTo(accEnd)<=0)) ||
                    ((labEnd.compareTo(accEnd)<=0) && (labEnd.compareTo(accBegin)>=0))){
                try{
                    App.getInstance().rejectLab(username, this, lab);
                } catch(DBError dbe){
                    return "error";
                }
                invitations.remove(lab);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "You already accpted lab in that period", "IGNOREd"));
                return "start";
            }
            
        }
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
    
    public Float getTotalPayments() {
        float sum = 0;
        for(Payment payment: payments) {
            sum += payment.getAmount();
        }
        return sum;
    }
}
