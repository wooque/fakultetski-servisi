package com.corejsf;

import java.io.Serializable;

public class Classroom implements Serializable{
    
    private Integer id;
    private String location;
    private String type;
    private Integer number;

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
}
