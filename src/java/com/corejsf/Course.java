package com.corejsf;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "course")
@SessionScoped
public class Course implements Serializable{
    
    private boolean selected;
    private Integer id;
    private String department;
    private Integer teachingYear;
    private String code;
    private String name;
    private String semester;
    private Integer year = Calendar.getInstance().get(Calendar.YEAR);
    private Teacher teacher;
    private LinkedList<Teacher> teachers = new LinkedList<Teacher>();
    private static String[] departments = {"IR", "OT", "OS", "OG", "OE", "OF", "SI"};
    private static String[] teachingYears = {"1", "2", "3", "4", "M"};
    private static String[] semesters = {"summer", "winter"};
    
    public String[] getTeachingYears() { return teachingYears; }
    public String[] getDepartments() { return departments; }
    public String[] getSemesters(){ return semesters; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getTeachingYear() { return teachingYear; }
    public void setTeachingYear(Integer teachingYear) { this.teachingYear = teachingYear; }
    
    public String getTeachingYearString() { return teachingYear == null? "": Integer.toString(teachingYear); }
    public void setTeachingYearString(String teachingYear) {
        if(teachingYear.equals("M")){
            this.teachingYear = 5;
        } else {
            this.teachingYear = Integer.parseInt(teachingYear);
        }
    }
    
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
        year = Calendar.getInstance().get(Calendar.YEAR);
        teacher = null;
        teachers = new LinkedList<Teacher>();
    }
    
    @Override
    public String toString() {
        return new CourseConverter().getAsString(null, null, this);
    }
}
