package com.corejsf;

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
        Course course = new Course();
        if(tokenizer.hasMoreTokens()){
            token = tokenizer.nextToken().trim();
            course.setDepartment(token.substring(0, 2));
            course.setTeachingYear(Integer.parseInt(token.substring(2, 3)));
            course.setCode(token.substring(3, token.length()));
        }
        if(tokenizer.hasMoreTokens()){
            course.setName(tokenizer.nextToken().trim());
        }
        if(tokenizer.hasMoreTokens()){
            course.setSemester(tokenizer.nextToken().trim());
        }
        if(tokenizer.hasMoreTokens()){
            token = tokenizer.nextToken().substring(1, 5);
            course.setYear(Integer.parseInt(token));
        }
        return course;
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
