package com.corejsf;

import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = Teacher.class)
public class TeacherConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value);
        Teacher teacher = new Teacher();
        if(tokenizer.hasMoreTokens()){
            teacher.setName(tokenizer.nextToken());
        }
        if(tokenizer.hasMoreTokens()){
            teacher.setSurname(tokenizer.nextToken());
        }
        if(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            teacher.setUsername(token.substring(1, token.length()-1));
        }
        return teacher;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder builder = new StringBuilder();
        Teacher teacher = (Teacher)value;
        builder.append(teacher.getName());
        builder.append(" ");
        builder.append(teacher.getSurname());
        builder.append(" (");
        builder.append(teacher.getUsername());
        builder.append(")");
        return builder.toString();
    }
    
    public static void main(String[] args) {
        TeacherConverter conv = new TeacherConverter();
        Teacher teacher = (Teacher)conv.getAsObject(null, null, "Vuk Mirovic (wooque)");
        String output = conv.getAsString(null, null, teacher);
        System.out.println(output);
    }
}
