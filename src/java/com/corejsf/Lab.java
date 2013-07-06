package com.corejsf;

import app.App;
import db.DBError;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@SessionScoped
@ManagedBean(name = "lab")
public class Lab implements Serializable{
    
    private boolean past;
    private boolean closed;
    private Integer id;
    private Course course;
    private String name;
    private Date begin;
    private Date end;
    private Classroom classroom;
    private Integer type;
    private Integer maxDemonstrators;
    private LinkedList<Demonstrator> demonstrators;
    private LinkedList<Demonstrator> selectedDemonstrators = new LinkedList<Demonstrator>();
    private LinkedList<Demonstrator> workingDemonstrators;
    private Demonstrator demonstrator;
    public static final SelectItem[] types = {new SelectItem(1, "Duty hours"),
                                            new SelectItem(2, "Duty hours with grading"),
                                            new SelectItem(3, "Duty hours with questioning and grading"),
                                            new SelectItem(4, "Duty hours on exam")};
    
    public SelectItem[] getTypes() { return types; }
    
    public boolean isPast() { return past; }
    public void setPast(boolean past) { this.past = past; }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Date getBegin() { return begin; }
    public void setBegin(Date begin) { this.begin = begin; }

    public Date getEnd() { return end; }
    public void setEnd(Date end) { this.end = end; }
    
    public Date getAdjustEnd() { return end; }
    public void setAdjustEnd(Date end) { 
        Date temp = new Date((this.end.getTime()/(24*3600*1000))*(24*3600*1000));
        this.end = new Date(temp.getTime() + end.getTime()); 
    }
    
    public Date getTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date retDate = cal.getTime();
        return retDate;
    }
    
    public Date getMaxDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        Date retDate = cal.getTime();
        return retDate;
    }
    
    public Date getMinEnd() {
        return new Date((begin != null? begin.getTime() : getTomorrow().getTime())+ 3600*1000);
    }
    
    public Integer getMinEndHour() {
        if(begin == null) return 9;
        return begin.getHours() + 1;
    }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    
    public String getTypeString() { return types[type-1].getLabel(); }

    public Integer getMaxDemonstrators() { return maxDemonstrators; }
    public void setMaxDemonstrators(Integer maxDemonstrators) { this.maxDemonstrators = maxDemonstrators; }

    public LinkedList<Demonstrator> getDemonstrators() { return demonstrators; }
    public void setDemonstrators(LinkedList<Demonstrator> demonstrators) { this.demonstrators = demonstrators; }
    
    public LinkedList<Demonstrator> getSelectedDemonstrators() { return selectedDemonstrators; }
    public void setSelectedDemonstrators(LinkedList<Demonstrator> selectedDemonstrators) { this.selectedDemonstrators = selectedDemonstrators; }

    public Demonstrator getDemonstrator() { return demonstrator; }
    public void setDemonstrator(Demonstrator demonstrator) { this.demonstrator = demonstrator; }
    
    public LinkedList<Demonstrator> getWorkingDemonstrators() { return workingDemonstrators; }
    public void setWorkingDemonstrators(LinkedList<Demonstrator> workingDemonstrators) { this.workingDemonstrators = workingDemonstrators; }
    
    public LinkedList<Classroom> completeClassroom(String query) {
        LinkedList<Classroom> classrooms = new LinkedList<Classroom>();
        try {
             classrooms = App.getInstance().completeClassroom(query);
        } catch (DBError dbe){}
        return classrooms;
    }
    
    public void addDemonstrator() {
        if(selectedDemonstrators.contains(demonstrator)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Demonstrator already added", "IGNORED"));
        } else {
            selectedDemonstrators.add(demonstrator);
        }
        demonstrator = null;
    }
    
    public void loadDemonstrators(){
        try {
             App.getInstance().loadDemonstrators(this);
        } catch (DBError dbe){}
    }

    public String removeDemonstrators(){
        LinkedList<Integer> indexesForRemoval = new LinkedList<Integer>();
        for (int i = 0; i < workingDemonstrators.size(); i++) {
            Demonstrator d = workingDemonstrators.get(i);
            if (d.isRejected()) {
                indexesForRemoval.add(i);
                try {
                    App.getInstance().removeDemonstrator(this, d);
                } catch (DBError ex) {
                    return "error";
                }
            }
        }
        int removed = 0;
        for (int i = 0; i < indexesForRemoval.size(); i++) {
            int index = indexesForRemoval.get(i);
            workingDemonstrators.remove(index - removed);
            removed++;
        }
        return "start";
    }
    
    public String removeDemonstrator(Demonstrator d) {
        workingDemonstrators.remove(d);
        try {
            App.getInstance().removeDemonstratorFromLab(this, d);
        } catch (DBError ex) {
            return "error";
        }
        return "start";
    }
    
    public String addWorkingDemonstrator() {
        if(workingDemonstrators.contains(demonstrator)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Demonstrator already added", "IGNORED"));
        } else {
            workingDemonstrators.add(demonstrator);
        }
        try {
            App.getInstance().addWorkingDemonstrator(this, demonstrator);
        } catch (DBError ex) {
            return "error";
        }
        return "start";
    }
    
    public boolean thereIsRejected(){
        for(Demonstrator d: workingDemonstrators){
            if(d.isRejected() == true){
                return true;
            }
        }
        return false;
    }
    
    public void closeLab() {
        closed = true;
    }
    
    public void clear(){
        course = null;
        name = null;
        setBegin(null);
        setEnd(null);
        classroom = null;
        type = null;
        maxDemonstrators = null;
        demonstrators = null;
        selectedDemonstrators = new LinkedList<Demonstrator>();
        workingDemonstrators = null;
        demonstrator = null;
    }

}
