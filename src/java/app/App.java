package app;

import com.corejsf.Admin;
import com.corejsf.Apply;
import com.corejsf.Course;
import com.corejsf.Search;
import com.corejsf.Search.Elem;
import com.corejsf.Signup;
import com.corejsf.Student;
import com.corejsf.Teacher;
import com.corejsf.User;
import db.DB;
import db.DBError;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
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
    
    public boolean login(Admin admin, Teacher teacher, Student student, User user) throws DBError{

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
                    query = "select * from Apply where username='"+user.getUsername()+"';";
                    rs = stat.executeQuery(query);
                    if(rs.next()){
                        student.setApplied(true);
                    } else {
                        student.setApplied(false);
                    }
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
            } else if(user.getType().equals("teacher")) {
                LinkedList<Integer> courseIDs = new LinkedList<Integer>();
                LinkedList<Course> courses = new LinkedList<Course>();
                query = "select * from Teaches where username='"+user.getUsername()+"';";
                rs = stat.executeQuery(query);
                while(rs.next()){
                    courseIDs.add(rs.getInt("CourseID"));
                }
                for(Integer courseID: courseIDs){
                    query = "select * from Course where CourseID='"+courseID+"';";
                    rs = stat.executeQuery(query);
                    if(rs.next()){
                        Course course = new Course();
                        course.setId(courseID);
                        course.setCode(rs.getString("code"));
                        course.setDepartment(rs.getString("department"));
                        course.setName(rs.getString("name"));
                        if(rs.getInt("semester") == 0){
                            course.setSemester("summer");
                        } else {
                            course.setSemester("winter");
                        }
                        course.setTeachingYear(rs.getInt("teachyear"));
                        course.setYear(rs.getInt("year"));
                        courses.add(course);
                    }
                }
                teacher.setCourses(courses);
                loadApplies(teacher);
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
    
    public boolean add(Course course) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from Course where department='"+course.getDepartment()+"' and teachyear='"+course.getTeachingYear()
                            +"' and code='"+course.getCode()+"' and year='"+course.getYear()+"';";
            ResultSet rs = stat.executeQuery(query);
            if(rs.next()){
                stat.close();
                DB.getInstance().putConnection(conn);
                return false;
            }
            query = "insert into Course (department, teachyear, code, name, semester, year) values ('"
                    +course.getDepartment()+"','"+course.getTeachingYear()+"','"+course.getCode()+"','"+course.getName()+"',"
                    +(course.getSemester().equals("winter")?1:0)+",'"+course.getYear()+"');";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return true;
    }
    
    public LinkedList<Teacher> completeTeacher(String input) throws DBError {
        Connection conn = db.getConnection();
        LinkedList<Teacher> teacherlist = new LinkedList<Teacher>();
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select username, name, surname from User u where u.type='teacher' and (username like '"+input+"%'"
                            +" or name like '"+input+"%'"+" or surname like '"+input+"%'"+");";
            ResultSet rs = stat.executeQuery(query);
            while(rs.next()){
                Teacher teacher = new Teacher();
                teacher.setName(rs.getString("name"));
                teacher.setSurname(rs.getString("surname"));
                teacher.setUsername(rs.getString("username"));
                teacherlist.add(teacher);
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return teacherlist;
    }
    
    public void addTeachers(Course course) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from Course where department='"+course.getDepartment()+"' and teachyear='"+course.getTeachingYear()
                            +"' and code='"+course.getCode()+"' and year='"+course.getYear()+"';";
            ResultSet rs = stat.executeQuery(query);
            if(!rs.next()){
                stat.close();
                DB.getInstance().putConnection(conn);
                throw new DBError();
            }
            int id = rs.getInt("CourseID");
            for(Teacher teacher: course.getTeachers()){
                query = "insert into Teaches (CourseID, username) values ('"+id+"','"+teacher.getUsername()+"');";
                stat.executeUpdate(query);
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadSurveys(Student student) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            if(cal.get(Calendar.MONTH) < 9) {
                year--;
            }
            Statement stat = conn.createStatement();
            String query = "select * from Course where department='"+student.getDepartment()+"' and teachyear<'"+student.getYear()
                            +"' and ((year='"+year+"' and semester=1) or (year='"+(year+1)+"' and semester=0));";
            ResultSet rs = stat.executeQuery(query);
            LinkedList<Course> courses = new LinkedList<Course>();
            while(rs.next()){
                Course course = new Course();
                course.setId(rs.getInt("CourseID"));
                course.setCode(rs.getString("code"));
                course.setDepartment(rs.getString("department"));
                course.setName(rs.getString("name"));
                if(rs.getInt("semester") == 0){
                    course.setSemester("summer");
                } else {
                    course.setSemester("winter");
                }
                course.setTeachingYear(rs.getInt("teachyear"));
                course.setYear(year);
                courses.add(course);
            }
            student.setAvailableCourses(courses);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void apply(User user, Student student) throws DBError{
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            LinkedList<Course> selectedCourses = new LinkedList<Course>();
            for(Course course: student.getAvailableCourses()){
                if(course.isSelected()){
                    query = "insert into Apply (username, CourseID) values ('"+user.getUsername()+"','"+course.getId()+"');";
                    stat.executeUpdate(query);
                    selectedCourses.add(course);
                }
            }
            student.setSelectedCourses(selectedCourses);
            student.setAvailableCourses(null);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadApplies(Teacher teacher) throws DBError {
        
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            LinkedList<LinkedList<Apply>> applies = new LinkedList<LinkedList<Apply>>();
            for(Course course: teacher.getCourses()){
                query = "select * from Apply where CourseID='"+course.getId()+"';";
                ResultSet rs = stat.executeQuery(query);
                LinkedList<Apply> courseApplies = new LinkedList<Apply>();
                LinkedList<String> usernames = new LinkedList<String>();
                while(rs.next()){
                   usernames.add(rs.getString("username"));
                }
                for(String username: usernames) {
                    Apply apply = new Apply();
                    apply.setUsername(username);
                    apply.setCourseID(course.getId());
                    query = "select * from User where username='"+username+"';";
                    rs = stat.executeQuery(query);
                    if(rs.next()){
                        apply.setName(rs.getString("name"));
                        apply.setSurname(rs.getString("surname"));
                        apply.setPhone(rs.getString("phone"));
                        apply.setEmail(rs.getString("email"));
                    }
                    query = "select * from Student where username='"+username+"';";
                    rs = stat.executeQuery(query);
                    if(rs.next()){
                        apply.setYear(rs.getInt("year"));
                        apply.setGPA(rs.getFloat("gpa"));
                    }
                    courseApplies.add(apply);
                }
                applies.add(courseApplies);
            }
            teacher.setApplies(applies);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void accept(Apply apply) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            query = "insert into Demonstrator (CourseID, username) values ('"+apply.getCourseID()+"','"+apply.getUsername()+"');";
            stat.executeUpdate(query);
            query = "delete from Apply where username='"+apply.getUsername()+"' and CourseID='"+apply.getCourseID()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void reject(Apply apply) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "delete from Apply where username='"+apply.getUsername()+"' and CourseID='"+apply.getCourseID()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void searchDemonstrators(User user, Search search) throws DBError{
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            StringBuilder query = new StringBuilder("select u.name, u.surname, u.phone, u.email, s.year, s.gpa from User u, Student s, Demonstrator d ");
            if(search.isMyCourses()){
                query.append(", Teaches t ");
            }
            query.append("where d.username=u.username and d.username=s.username");
            if(search.isMyCourses()){
                query.append(" and t.username='");
                query.append(user.getUsername());
                query.append("' and t.courseID=d.courseID");
            }
            if(!search.getName().isEmpty()){
                query.append(" and u.name='");
                query.append(search.getName());
                query.append("'");
            }
            if(!search.getSurname().isEmpty()){
                query.append(" and u.surname='");
                query.append(search.getSurname());
                query.append("'");
            }
            query.append(" group by d.username;");
            ResultSet rs = stat.executeQuery(query.toString());
            LinkedList<Elem> result = new LinkedList<Elem>();
            while(rs.next()){
                Elem e = new Elem();
                User u = new User();
                Student s = new Student();
                u.setName(rs.getString("name"));
                u.setSurname(rs.getString("name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                s.setYear(rs.getInt("year"));
                s.setGPA(rs.getFloat("gpa"));
                e.setStudent(s);
                e.setUser(u);
                result.add(e);
            }
            search.setResult(result);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
}
