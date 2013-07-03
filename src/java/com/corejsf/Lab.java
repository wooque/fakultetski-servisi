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
    
    private Integer id;
    private Course course;
    private String name;
    private Date date;
    private Integer beginHour;
    private Integer endHour;
    private Classroom classroom;
    private Integer type;
    private Integer maxDemonstrators;
    private LinkedList<User> demonstrators;
    private LinkedList<Student> demonstratorsInfo;
    private LinkedList<User> selectedDemonstrators = new LinkedList<User>();
    private LinkedList<Demonstrator> workingDemonstrators;
    private User demonstrator;
    private static SelectItem[] begin;
    private static SelectItem[] end;
    public static final SelectItem[] types = {new SelectItem(1, "Duty hours"),
                                            new SelectItem(2, "Duty hours with grading"),
                                            new SelectItem(3, "Duty hours with questioning and grading"),
                                            new SelectItem(4, "Duty hours on exam")};
    
    static {
        begin = new SelectItem[14];
        end = new SelectItem[14];
        for(int i = 8; i < 22; i++){
            begin[i-8] = new SelectItem(i, Integer.toString(i));
            end[i-8] = new SelectItem(i + 1, Integer.toString(i + 1));
        }
    }
    
    public SelectItem[] getBegin() { return begin; }
    public SelectItem[] getEnd() { return end; }
    public SelectItem[] getTypes() { return types; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date retDate = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return retDate;
    }
    
    public Date getMaxDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        Date retDate = cal.getTime();
        cal.add(Calendar.MONTH, -3);
        return retDate;
    }

    public Integer getBeginHour() { return beginHour; }
    public void setBeginHour(Integer beginHour) { this.beginHour = beginHour; }

    public Integer getEndHour() { return endHour; }
    public void setEndHour(Integer endHour) { this.endHour = endHour; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    
    public String getTypeString() { return types[type-1].getLabel(); }

    public Integer getMaxDemonstrators() { return maxDemonstrators; }
    public void setMaxDemonstrators(Integer maxDemonstrators) { this.maxDemonstrators = maxDemonstrators; }

    public LinkedList<User> getDemonstrators() { return demonstrators; }
    public void setDemonstrators(LinkedList<User> demonstrators) { this.demonstrators = demonstrators; }
    
    public LinkedList<Student> getDemonstratorsInfo() { return demonstratorsInfo; }
    public void setDemonstratorsInfo(LinkedList<Student> demonstratorsInfo) { this.demonstratorsInfo = demonstratorsInfo; }
    
    public LinkedList<User> getSelectedDemonstrators() { return selectedDemonstrators; }
    public void setSelectedDemonstrators(LinkedList<User> selectedDemonstrators) { this.selectedDemonstrators = selectedDemonstrators; }

    public User getDemonstrator() { return demonstrator; }
    public void setDemonstrator(User demonstrator) { this.demonstrator = demonstrator; }
    
    public LinkedList<Demonstrator> getWorkingDemonstrators() { return workingDemonstrators; }
    public void setWorkingDemonstrators(LinkedList<Demonstrator> workingDemonstrators) { this.workingDemonstrators = workingDemonstrators; }
    
    public void changeEnd() {
        end = new SelectItem[22 - beginHour];
        for(int i = beginHour + 1; i < 23; i++){
            end[i - beginHour - 1] = new SelectItem(i, Integer.toString(i));
        }
    }
    
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
    
    public String addDemonstrators(Nav nav) {
        try {
             App.getInstance().loadDemonstrators(this);
        } catch (DBError dbe){}
        nav.setPage("/sections/start/labDemonstrators.xhtml");
        return "start";
    }
    
    public void clear(){
        course = null;
        name = null;
        date = null;
        beginHour = null;
        endHour = null;
        classroom = null;
        type = null;
        maxDemonstrators = null;
        demonstrators = null;
        demonstratorsInfo = null;
        selectedDemonstrators = new LinkedList<User>();
        demonstrator = null;
    }

}
