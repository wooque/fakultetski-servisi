package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.LinkedList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean(name = "admin")
public class Admin implements Serializable{
    
    private LinkedList<Signup> signups;

    public LinkedList<Signup> getSignups() { return signups; }
    public void setSignups(LinkedList<Signup> signups) { this.signups = signups; }
    
    public String getSignUpsNum() { return Integer.toString(signups.size()); }
    
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
    
    public String addCourse(Course course) {
        boolean outcome;
        try {
            outcome = App.getInstance().add(course);
        } catch (DBError dbe) {
            return "error";
        }
        if (!outcome) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Course exist", "IGNORED"));
        } else {
            course.clear();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Course added", "IGNORED"));
        }
        return "start";
    }
}
