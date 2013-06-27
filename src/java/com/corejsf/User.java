package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.Calendar;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean(name = "user")
public class User implements Serializable {

    private String username;
    private String password;
    private String newpassword;
    private String type;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private boolean beginOfTheYear;
    private static boolean surveysActivated;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
 
    public String getNewpassword() { return newpassword; }
    public void setNewpassword(String newpassword) { this.newpassword = newpassword; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean getBeginOfTheYear() { return beginOfTheYear; }
    public synchronized boolean isSurveysActivated() { return surveysActivated; }
    public synchronized void setSurveysActivated(boolean aSurveysActivated) { surveysActivated = aSurveysActivated; }
    
    
    public String logMeIn(Admin admin, Teacher teacher, Student student, Nav nav) {
        boolean outcome;
        try {
            outcome = App.getInstance().login(admin, teacher, student, this);
        } catch (DBError dbe) {
            return "error";
        }
        if (outcome) {
            if(type.equals("admin")) {
                nav.setPage("/sections/start/admin.xhtml");
            } else if(type.equals("student")){
                nav.setPage("/sections/start/student.xhtml");
            } else {
                 nav.setPage("/sections/start/teacher.xhtml");
            }
            int month = Calendar.getInstance().get(Calendar.MONTH);
            // TO DO: set actual month in the release
            beginOfTheYear = (month == 5);
            // quick-and-dirty solution for reseting sureveysActivated
            if ((month != 5) && (isSurveysActivated() == true)){
                setSurveysActivated(false);
            }
            return "start?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong username or password", "IGNORED"));
            return "index";
        }
    }
    
    public String changePassword(Nav nav) {
        boolean outcome;
        try {
            outcome = App.getInstance().changePassword(this);
        } catch (DBError dbe) {
            return "error";
        }
        if (outcome) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password changed", "IGNORED"));
            nav.setPage("/sections/login/loginContent.xhtml");
            return "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong username or password", "IGNORED"));
            return "index";
        }
    }
    
    public String logOut(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index?faces-redirect=true";
    }
    
    public String activateSurveys(Nav nav){
        setSurveysActivated(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Surveys activated", "IGNORED"));
        nav.setPage("/sections/start/admin.xhtml");
        return "start";
    }
    
}
