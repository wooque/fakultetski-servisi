package com.corejsf;

import java.io.Serializable;
import java.util.Date;

public class Payment implements Serializable{
    
    private Lab lab;
    private Date date;
    private Float amount;

    public Lab getLab() { return lab; }
    public void setLab(Lab lab) { this.lab = lab; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }
}
