package com.corejsf;

import app.App;
import db.DBError;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = Classroom.class)
public class ClassroomConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, " - ");
        Classroom classroom = new Classroom();
        if(tokenizer.hasMoreTokens()){
            classroom.setLocation(tokenizer.nextToken());
        }
        if(tokenizer.hasMoreTokens()){
            classroom.setType(tokenizer.nextToken());
        }
        if(tokenizer.hasMoreTokens()){
            classroom.setNumber(Integer.parseInt(tokenizer.nextToken()));
        }
        try{
            App.getInstance().fillClassroomID(classroom);
        } catch (DBError dbe){}
        
        return classroom;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder builder = new StringBuilder();
        Classroom classroom = (Classroom)value;
        builder.append(classroom.getLocation());
        builder.append(" - ");
        builder.append(classroom.getType());
        builder.append(" ");
        builder.append(Integer.toString(classroom.getNumber()));
        return builder.toString();
    }
    
    public static void main(String[] args) {
        ClassroomConverter conv = new ClassroomConverter();
        String classroom = "Paviljon - lab 25";
        Classroom cr = (Classroom)conv.getAsObject(null, null, classroom);
        String out = conv.getAsString(null, null, cr);
        System.out.println(out);
    }
    
}
