package com.corejsf;

import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("demonstrator")
public class UserConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value);
        String username;
        if(tokenizer.hasMoreTokens()){
            tokenizer.nextToken("(");
        }
        if(tokenizer.hasMoreTokens()){
            username = tokenizer.nextToken("()");
            Lab lab = (Lab)context.getApplication().evaluateExpressionGet(context, "#{lab}", Lab.class);
            LinkedList<User> demonstrators = lab.getDemonstrators();
            for(User demonstrator: demonstrators){
                if(demonstrator.getUsername().equals(username)){
                    return demonstrator;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder builder = new StringBuilder();
        User user = (User)value;
        builder.append(user.getName());
        builder.append(" ");
        builder.append(user.getSurname());
        builder.append(" (");
        builder.append(user.getUsername());
        builder.append(")");
        return builder.toString();
    }
    
}
