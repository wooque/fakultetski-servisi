package com.corejsf;

import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("teacherEditsDemonstrator")
public class DemonstratorConverter2 implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value);
        String username;
        if(tokenizer.hasMoreTokens()){
            tokenizer.nextToken("(");
        }
        if(tokenizer.hasMoreTokens()){
            username = tokenizer.nextToken("()");
            Lab lab = (Lab)context.getApplication().evaluateExpressionGet(context, "#{teacher.currlab}", Lab.class);
            LinkedList<Demonstrator> demonstrators = lab.getDemonstrators();
            for(Demonstrator demonstrator: demonstrators){
                if(demonstrator.getUser().getUsername().equals(username)){
                    return demonstrator;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder builder = new StringBuilder();
        Demonstrator demonstrator = (Demonstrator)value;
        builder.append(demonstrator.getUser().getName());
        builder.append(" ");
        builder.append(demonstrator.getUser().getSurname());
        builder.append(" (");
        builder.append(demonstrator.getUser().getUsername());
        builder.append(")");
        return builder.toString();
    }
    
}
