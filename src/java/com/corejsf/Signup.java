package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean(name = "signup")
public class Signup implements Serializable {

    private String username;
    private String password;
    private String type;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String department;
    private Integer year;
    private Float GPA;
    
    private static String[] types = {"student", "teacher"};
    private static String[] departments = {"IR", "OT", "OS", "OG", "OE", "OF", "SI"};
    private static String[] years = {"1", "2", "3", "4", "M"};
    
    public String[] getTypes() { return types; }
    public String[] getYears() { return years; }
    public String[] getDepartments() { return departments; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
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
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getYearString() { return year == null? "": Integer.toString(year); }
    public void setYearString(String year) {
        if(year.equals("M")){
            this.year = 5;
        } else {
            this.year = Integer.parseInt(year);
        }
    }

    public Float getGPA() { return GPA; }
    public void setGPA(Float GPA) { this.GPA = GPA; }
    
    public String getGPAString() { return GPA == null? "": Float.toString(GPA); }
    public void setGPAString(String GPA) { this.GPA = Float.parseFloat(GPA); }
   
    public void checkUsername(){
        boolean outcome;
        try {
            outcome = App.getInstance().checkUsername(this);
        } catch (DBError dbe) {
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        if (outcome) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Username available", "IGNORED"));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username is not available", "IGNORED"));
        }
    }
    
    public String signUp(){
        try {
            App.getInstance().signUp(this);
        } catch (DBError dbe) {
            return "error";
        }
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "signUpMsg";
    }

    public void clear() {
        username = null;
        password = null;
        type = null;
        name = null;
        surname = null;
        phone = null;
        email = null;
        department = null;
        year = null;
        GPA = null;
    }
}
