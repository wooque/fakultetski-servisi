package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

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
    private static SelectItem[] types = new SelectItem[]{
                                                            new SelectItem("student", "Student"),
                                                            new SelectItem("teacher", "Teacher")
                                                        };
    public static SelectItem[] years;
    public static SelectItem[] departments;

    static {
        years = new SelectItem[6];
        years[0] = new SelectItem(null, "Select year", "", false, false, true);
        for(int i = 1; i < 6; i++) {
            years[i] = new SelectItem(i, Integer.toString(i));
        }
        departments = new SelectItem[8];
        departments[0] = new SelectItem(null, "Select one", "", false, false, true);
        departments[1] = new SelectItem("IR", "IR");
        departments[2] = new SelectItem("OT", "OT");
        departments[3] = new SelectItem("OS", "OS");
        departments[4] = new SelectItem("OG", "OG");
        departments[5] = new SelectItem("OE", "OE");
        departments[6] = new SelectItem("OF", "OF");
        departments[7] = new SelectItem("SI", "SI");
    };
    
    public SelectItem[] getTypes() { return types; }
    public SelectItem[] getYears() { return years; }
    public SelectItem[] getDepartments() { return departments; }

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

    public Float getGPA() { return GPA; }
    public void setGPA(Float GPA) { this.GPA = GPA; }
   
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
