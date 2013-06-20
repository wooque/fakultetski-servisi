package com.corejsf;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@SessionScoped
@ManagedBean(name = "nav")
public class Nav implements Serializable {
    
    private String page = "/sections/login/loginContent.xhtml";
    private static final String adminMenu = "/sections/start/adminMenu.xhtml";
    private static final String studentMenu = "/sections/start/studentMenu.xhtml";
    private static final String teacherMenu = "/sections/start/teacherMenu.xhtml";

    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }
    
    public String getMenu(String type){
        if(type.equals("admin"))
            return adminMenu;
        else if(type.equals("student"))
            return studentMenu;
        else if(type.equals("teacher"))
            return teacherMenu;
        else
            return null;
    }
    
}
