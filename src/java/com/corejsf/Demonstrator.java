package com.corejsf;

import java.io.Serializable;

public class Demonstrator implements Serializable{
    
    private User user;
    private Student student;
    private boolean rejected;
    private Float payment;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public boolean isRejected() { return rejected; }
    public void setRejected(boolean rejected) { this.rejected = rejected; }

    public Float getPayment() { return payment; }
    public void setPayment(Float payment) { this.payment = payment; }
}
