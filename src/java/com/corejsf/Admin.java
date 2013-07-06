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

@SessionScoped
@ManagedBean(name = "admin")
public class Admin implements Serializable{
    
    private LinkedList<Signup> signups;
    private LinkedList<Lab> labs;
    private Lab currlab;
    private Date beginAccountDate;
    private Date endAccountDate;
    private LinkedList<Demonstrator> demonstrators;

    public LinkedList<Signup> getSignups() { return signups; }
    public void setSignups(LinkedList<Signup> signups) { this.signups = signups; }
    
    public String getSignUpsNum() { return Integer.toString(signups.size()); }
    
    public LinkedList<Lab> getLabs() { return labs; }
    public void setLabs(LinkedList<Lab> labs) { 
        this.labs = labs; 
        beginAccountDate = getMinDate();
        endAccountDate = getMaxDate();
    }

    public Lab getCurrlab() { return currlab; }
    public void setCurrlab(Lab currlab) { this.currlab = currlab; }

    public Date getBeginAccountDate() { return beginAccountDate; }
    public void setBeginAccountDate(Date beginAccountDate) { this.beginAccountDate = beginAccountDate; }

    public Date getEndAccountDate() { return endAccountDate; }
    public void setEndAccountDate(Date endAccountDate) { this.endAccountDate = endAccountDate; }
    
    public LinkedList<Demonstrator> getDemonstrators() { return demonstrators; }
    public void setDemonstrators(LinkedList<Demonstrator> demonstrators) { this.demonstrators = demonstrators; }
    
    public void accept(Signup signup) {
        signups.remove(signup);
        try {
            App.getInstance().accept(signup);
        } catch (DBError dbe) {
        }
    }
    
    public void reject(Signup signup) {
        signups.remove(signup);
        try {
            App.getInstance().reject(signup);
        } catch (DBError dbe) {
        }
    }
    
    public void add(Signup signup) {
        try {
            App.getInstance().accept(signup);
        } catch (DBError dbe) {
        }
        signup.clear();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User successfully added", "IGNORED"));
    }
    
    public String loadSignUps(Nav nav){
        try{
            App.getInstance().loadSignUps(this);
        } catch (DBError dbe){
            return "error";
        }
        nav.setPage("/sections/start/requests.xhtml");
        return "start";
    }
    
    public String addCourse(Course course, Nav nav) {
        boolean outcome;
        try {
            outcome = App.getInstance().add(course);
        } catch (DBError dbe) {
            return "error";
        }
        if (!outcome) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Course exist", "IGNORED"));
        } else {
            nav.setPage("/sections/start/courseTeachers.xhtml");
        }
        return "start";
    }
    public String addTeachersToCourse(Course course, Nav nav) {
        try {
            App.getInstance().addTeachers(course);
        } catch (DBError dbe) {
            return "error";
        }
        course.clear();
        nav.setPage("/sections/start/courses.xhtml");
        return "start";
    }
    
    public String loadLabs(Nav nav) {
        try{
            App.getInstance().loadLabs(this);
        } catch (DBError dbe){
            return "error";
        }
        nav.setPage("/sections/start/accountLabs.xhtml");
        return "start";
    }
    
    public void showLab(Lab lab, Nav nav) {
        setCurrlab(lab);
        nav.setPage("/sections/start/accountLabInfo.xhtml");
    }
    
    public Date getMinDate(){
        Date min = new Date();
        for(Lab lab: labs){
            if(lab.getBegin().before(min)){
                min = lab.getBegin();
            }
        }
        return new Date((min.getTime()/(24*3600*1000))*24*3600*1000);
    }
    
    public Date getMaxDate(){
        Date max = new Date(0);
        for(Lab lab: labs){
            if(lab.getBegin().after(max)){
                max = lab.getBegin();
            }
        }
        return new Date(max.getTime() + 24*3600*1000);
    }
    
    public Date getMinEndDate(){
        return getBeginAccountDate();
    }
    
    public LinkedList<Lab> getSelectedLabs(){
        LinkedList<Lab> selected = new LinkedList<Lab>();
        for(Lab lab: labs){
            if(lab.getBegin().after(beginAccountDate) && lab.getBegin().before(endAccountDate)){
                 selected.add(lab);
            }
        }
        return selected;
    }
    
    public String account(Nav nav) {
        try{
            App.getInstance().account(this);
        } catch (DBError dbe){
            return "error";
        }
        nav.setPage("/sections/start/account.xhtml");
        return "start";
    }
}
