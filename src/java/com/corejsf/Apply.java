package com.corejsf;

import java.io.Serializable;

public class Apply implements Serializable{
    
    private String username;
    private Integer CourseID;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private Integer year;
    private Float GPA;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Float getGPA() { return GPA; }
    public void setGPA(Float GPA) { this.GPA = GPA; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getCourseID() { return CourseID; }
    public void setCourseID(Integer CourseID) { this.CourseID = CourseID; }
}
