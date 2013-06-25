package com.corejsf;

import java.io.Serializable;
import java.util.LinkedList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name = "course")
@SessionScoped
public class Course implements Serializable{
    
    private String department = "IR";
    private Integer teachingYear;
    private String code;
    private String name;
    private String semester;
    private Integer year = 2013;
    private Teacher teacher;
    private LinkedList<Teacher> teachers = new LinkedList<Teacher>();
    private static SelectItem[] years;
    public static SelectItem[] teachingYears;
    public static SelectItem[] departments;
    private static SelectItem[] semesters = new SelectItem[]{
                                                                new SelectItem("summer", "summer"),
                                                                new SelectItem("winter", "winter")
                                                            };
    static {
        teachingYears = new SelectItem[5];
        //teachingYears[0] = new SelectItem(null, "Select year", "", false, false, true);
        for(int i = 0; i < 5; i++) {
            teachingYears[i] = new SelectItem(i+1, Integer.toString(i+1));
        }
        
        departments = new SelectItem[7];
        //departments[0] = new SelectItem(null, "Select one", "", false, false, true);
        departments[0] = new SelectItem("IR", "IR");
        departments[1] = new SelectItem("OT", "OT");
        departments[2] = new SelectItem("OS", "OS");
        departments[3] = new SelectItem("OG", "OG");
        departments[4] = new SelectItem("OE", "OE");
        departments[5] = new SelectItem("OF", "OF");
        departments[6] = new SelectItem("SI", "SI");
    
        years = new SelectItem[50];
        //years[0] = new SelectItem(null, "Select year", "", false, false, true);
        for(int i = 0; i < 50; i++) {
            years[i] = new SelectItem(2000+i, Integer.toString(2000+i));
        }
    }

    public SelectItem[] getSemesters(){ return semesters; }
    public SelectItem[] getYears(){ return years; }
    public SelectItem[] getDepartments(){ return departments; }
    public SelectItem[] getTeachingYears(){ return teachingYears; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getTeachingYear() { return teachingYear; }
    public void setTeachingYear(Integer teachingYear) { this.teachingYear = teachingYear; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    
    public LinkedList<Teacher> getTeachers() { return teachers; }
    public void setTeachers(LinkedList<Teacher> teachers) { this.teachers = teachers; }
   
    public String addTeacher(){
        if(teachers.contains(teacher)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "User already added", "IGNORED"));
        } else {
            teachers.add(teacher);
        }
        teacher = null;
        return "start";
    }
    
    public String removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
        return "start";
    }

    public void clear() {
        department = null;
        teachingYear = null;
        code = null;
        name = null;
        semester = null;
        year = null;
        teacher = null;
        teachers = null;
    }
}
