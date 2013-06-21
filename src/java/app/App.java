package app;

import com.corejsf.Admin;
import com.corejsf.Signup;
import com.corejsf.Student;
import com.corejsf.User;
import db.DB;
import db.DBError;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class App {
    
    private static App instance;
    private DB db;
    
    private App(){
        db = DB.getInstance();
    }
    
    public static App getInstance(){
        if(instance == null){
            return new App();
        } else {
            return instance;
        }
    }
    
    public boolean login(Admin admin, Student student, User user) throws DBError{

        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select type, name, surname, phone, email from User u where u.username='"+user.getUsername()+
                            "' and password='"+user.getPassword()+"';";
            ResultSet rs = stat.executeQuery(query);
            if(rs.next()){
                user.setType(rs.getString("type"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
            }else{
                stat.close();
                DB.getInstance().putConnection(conn);
                return false;
            }
            if(user.getType().equals("student")){
                query = "select department, year, gpa from Student where username='"+user.getUsername()+"';";
                rs = stat.executeQuery(query);
                if(rs.next()){
                    student.setDepartment(rs.getString("department"));
                    student.setYear(rs.getInt("year"));
                    student.setGPA(rs.getFloat("gpa"));
                } else {
                    stat.close();
                    DB.getInstance().putConnection(conn);
                    throw new DBError();
                }
            } else if(user.getType().equals("admin")) {
                query = "select * from Signup;";
                rs = stat.executeQuery(query);
                Signup signup;
                LinkedList<Signup> signups = new LinkedList<Signup>();
                while(rs.next()){
                    signup = new Signup();
                    signup.setUsername(rs.getString("username"));
                    signup.setPassword(rs.getString("password"));
                    signup.setType(rs.getString("type"));
                    signup.setName(rs.getString("name"));
                    signup.setSurname(rs.getString("surname"));
                    signup.setPhone(rs.getString("phone"));
                    signup.setEmail(rs.getString("email"));
                    if(signup.getType().equals("student")) {
                        signup.setDepartment(rs.getString("department"));
                        signup.setYear(rs.getInt("year"));
                        signup.setGPA(rs.getFloat("gpa"));
                    }
                    signups.add(signup);
                }
                admin.setSignups(signups);
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return true;
    }
    
    public boolean changePassword(User user) throws DBError{

        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from User u where u.username='"+user.getUsername()+
                            "' and password='"+user.getPassword()+"';";
            ResultSet rs = stat.executeQuery(query);
            if(rs.next()){
                query = "update User set password='"+user.getNewpassword()+"' where username='"+user.getUsername()+"';";
                stat.executeUpdate(query);
                user.setPassword(user.getNewpassword());
                user.setNewpassword(null);
            }else{
                stat.close();
                DB.getInstance().putConnection(conn);
                return false;
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return true;
    }
    
    public boolean checkUsername(Signup user) throws DBError{

        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from User where username='"+user.getUsername()+"';";
            ResultSet rs = stat.executeQuery(query);
            if(rs.next()){
                stat.close();
                DB.getInstance().putConnection(conn);
                return false;
            }
            query = "select * from SignUp where username='"+user.getUsername()+"';";
            rs = stat.executeQuery(query);
            if(rs.next()){
                stat.close();
                DB.getInstance().putConnection(conn);
                return false;
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return true;
    }
    
    public void signUp(Signup user) throws DBError{

        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            if(!user.getType().equals("student")){
                query = "insert into Signup (username, password, type, name, surname, phone, email) values ('"
                                +user.getUsername()+"','"+user.getPassword()+"','"+user.getType()+"','"+user.getName()+"','"+user.getSurname()+"','"
                                +user.getPhone()+"','"+user.getEmail()+"');";
            } else {
                query = "insert into Signup (username, password, type, name, surname, phone, email, department, year, gpa) values ('"
                                +user.getUsername()+"','"+user.getPassword()+"','"+user.getType()+"','"+user.getName()+"','"+user.getSurname()+"','"
                                +user.getPhone()+"','"+user.getEmail()+"','"+user.getDepartment()+"','"+user.getYear()+"','"+user.getGPA()+"');";
            }
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void accept(Signup signup) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            query = "insert into User (username, password, type, name, surname, phone, email) values ('"
                                +signup.getUsername()+"','"+signup.getPassword()+"','"+signup.getType()+"','"+signup.getName()+"','"
                                +signup.getSurname()+"','"+signup.getPhone()+"','"+signup.getEmail()+"');";
            stat.executeUpdate(query); 
            if(signup.getType().equals("student")){
                query = "insert into Student (username, department, year, gpa) values ('"
                        +signup.getUsername()+"','"+signup.getDepartment()+"','"+signup.getYear()+"','"+signup.getGPA()+"');";
                stat.executeUpdate(query);
            }
            query = "delete from Signup where username='"+signup.getUsername()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void reject(Signup signup) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "delete from Signup where username='"+signup.getUsername()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadSignUps(Admin admin) throws DBError {
        
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from Signup;";
            ResultSet rs = stat.executeQuery(query);
            Signup signup;
            LinkedList<Signup> signups = new LinkedList<Signup>();
            while(rs.next()){
                signup = new Signup();
                signup.setUsername(rs.getString("username"));
                signup.setPassword(rs.getString("password"));
                signup.setType(rs.getString("type"));
                signup.setName(rs.getString("name"));
                signup.setSurname(rs.getString("surname"));
                signup.setPhone(rs.getString("phone"));
                signup.setEmail(rs.getString("email"));
                if(signup.getType().equals("student")) {
                    signup.setDepartment(rs.getString("department"));
                    signup.setYear(rs.getInt("year"));
                    signup.setGPA(rs.getFloat("gpa"));
                }
                signups.add(signup);
            }
            admin.setSignups(signups);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
}
