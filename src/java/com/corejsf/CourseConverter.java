package com.corejsf;

import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = Course.class)
public class CourseConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "-");
        String token;
        String department;
        Integer teachingYear;
        String code;
        if(tokenizer.hasMoreTokens()){
            token = tokenizer.nextToken().trim();
            department = token.substring(0, 2);
            teachingYear = Integer.parseInt(token.substring(2, 3));
            code = token.substring(3, token.length());
            Teacher teacher  = (Teacher)context.getApplication().evaluateExpressionGet(context, "#{teacher}", Teacher.class);
            LinkedList<Course> courses = teacher.getCourses();
            for(Course course: courses){
                if(course.getDepartment().equals(department) && course.getTeachingYear() == teachingYear && course.getCode().equals(code)){
                    return course;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder builder = new StringBuilder();
        Course course = (Course)value;
        builder.append(course.getDepartment());
        builder.append(course.getTeachingYear());
        builder.append(course.getCode());
        builder.append(" - ");
        builder.append(course.getName());
        builder.append(" - ");
        builder.append(course.getSemester());
        builder.append(" - ");
        builder.append(Integer.toString(course.getYear()));
        builder.append("/");
        builder.append(Integer.toString(course.getYear()+1));
        return builder.toString();
    }
    
    public static void main(String[] args) {
        CourseConverter conv = new CourseConverter();
        Course course = (Course)conv.getAsObject(null, null, "ИР4ПИА - Програмирање интернет апликација - summer - 2012/2013");
        String coursestring = conv.getAsString(null, null, course);
        System.out.println(coursestring);
    }
}
