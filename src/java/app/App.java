package app;

import com.corejsf.Admin;
import com.corejsf.Apply;
import com.corejsf.Classroom;
import com.corejsf.Course;
import com.corejsf.Demonstrator;
import com.corejsf.Lab;
import com.corejsf.Payment;
import com.corejsf.Search;
import com.corejsf.Signup;
import com.corejsf.Student;
import com.corejsf.Teacher;
import com.corejsf.User;
import db.DB;
import db.DBError;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Properties;
import javax.faces.context.FacesContext;

public class App {
    
    private static App instance;
    private static float PRICE = 100;
    private DB db;
    
    private App() {
        db = DB.getInstance();
        Properties properties = new Properties();
        try {
            properties.load(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/conf.properties"));
        } catch (IOException ex) {}
        PRICE = Float.parseFloat((String)properties.getProperty("price"));
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
                    query = "select labID from InvitedDemons where username='"+user.getUsername()+"' and rejected is null;";
                    rs = stat.executeQuery(query);
                    LinkedList<Integer> labIDs = new LinkedList<Integer>();
                    while(rs.next()){
                        labIDs.add(rs.getInt("LabID"));
                    }
                    LinkedList<Lab> labs = new LinkedList<Lab>();
                    for(Integer labID: labIDs){
                        query = "select count(username) as numofaccepted from LabDemons where labID='"+labID+"';";
                        rs = stat.executeQuery(query);
                        int numOfAccepted = 0;
                        if(rs.next()){
                            numOfAccepted = rs.getInt("numofaccepted");
                        }
                        query = "select maxDemons from Lab where labID='"+labID+"';";
                        rs = stat.executeQuery(query);
                        int maxDemons = 0;
                        if(rs.next()){
                            maxDemons = rs.getInt("maxDemons");
                        }
                        if(numOfAccepted == maxDemons)
                            continue;
                        query = "select l.name as labname, l.begin, l.end, l.type as labtype, l.closed, "
                                + "cr.ClassroomID, cr.location, cr.type as classtype, cr.number , "
                                + "c.CourseID, c.department, c.teachyear, c.code, c.name as coursename"
                                + "  from Lab l, Classroom cr, Course c where l.LabID='"+labID
                                + "' and l.ClassroomID=cr.ClassroomID and l.CourseID=c.CourseID;";
                        rs = stat.executeQuery(query);
                        Lab lab = new Lab();
                        Classroom cs = new Classroom();
                        Course course = new Course();
                        long today = Calendar.getInstance().getTimeInMillis();
                        if(rs.next()){
                            cs.setId(rs.getInt("ClassroomID"));
                            cs.setLocation(rs.getString("location"));
                            cs.setType(rs.getString("classtype"));
                            cs.setNumber(rs.getInt("number"));
                            course.setId(rs.getInt("CourseID"));
                            course.setDepartment(rs.getString("department"));
                            course.setTeachingYear(rs.getInt("teachyear"));
                            course.setCode(rs.getString("code"));
                            course.setName(rs.getString("coursename"));
                            lab.setId(labID);
                            lab.setClassroom(cs);
                            lab.setCourse(course);
                            lab.setName(rs.getString("labname"));
                            Timestamp begin = rs.getTimestamp("begin");
                            lab.setBegin(new java.util.Date(begin.getTime()));
                            Timestamp end = rs.getTimestamp("end");
                            lab.setEnd(new java.util.Date(end.getTime()));
                            if(end.getTime() < today){
                                lab.setPast(true);
                            }
                            lab.setType(rs.getInt("labtype"));
                            lab.setClosed(rs.getInt("closed") == 1? true: false);
                        }
                        labs.add(lab);
                    }
                    student.setInvitations(labs);
                    query = "select labID from LabDemons where username='"+user.getUsername()+"';";
                    rs = stat.executeQuery(query);
                    labIDs = new LinkedList<Integer>();
                    while(rs.next()){
                        labIDs.add(rs.getInt("LabID"));
                    }
                    labs = new LinkedList<Lab>();
                    LinkedList<Payment> payments = new LinkedList<Payment>();
                    for(int i = 0; i < labIDs.size(); i++){
                        Integer labID = labIDs.get(i);
                        query = "select l.name as labname, l.begin, l.end, l.type as labtype, l.closed, "
                                + "ld.dateOfPayment, ld.amount, "
                                + "cr.ClassroomID, cr.location, cr.type as classtype, cr.number, "
                                + "c.CourseID, c.department, c.teachyear, c.code, c.name as coursename"
                                + "  from Lab l, Classroom cr, Course c, LabDemons ld where l.LabID='"+labID+"' and ld.LabID='"+labID
                                + "' and ld.username='"+user.getUsername()+"' and l.ClassroomID=cr.ClassroomID and l.CourseID=c.CourseID;";
                        rs = stat.executeQuery(query);
                        Payment payment = new Payment();
                        Lab lab = new Lab();
                        Classroom cs = new Classroom();
                        Course course = new Course();
                        long today = Calendar.getInstance().getTimeInMillis();
                        if(rs.next()){
                            cs.setId(rs.getInt("ClassroomID"));
                            cs.setLocation(rs.getString("location"));
                            cs.setType(rs.getString("classtype"));
                            cs.setNumber(rs.getInt("number"));
                            course.setId(rs.getInt("CourseID"));
                            course.setDepartment(rs.getString("department"));
                            course.setTeachingYear(rs.getInt("teachyear"));
                            course.setCode(rs.getString("code"));
                            course.setName(rs.getString("coursename"));
                            lab.setId(labID);
                            lab.setClassroom(cs);
                            lab.setCourse(course);
                            lab.setName(rs.getString("labname"));
                            Timestamp begin = rs.getTimestamp("begin");
                            lab.setBegin(new java.util.Date(begin.getTime()));
                            Timestamp end = rs.getTimestamp("end");
                            lab.setEnd(new java.util.Date(end.getTime()));
                            if(end.getTime() < today){
                                lab.setPast(true);
                            }
                            lab.setType(rs.getInt("labtype"));
                            lab.setClosed(rs.getInt("closed") == 1? true: false);
                            payment.setLab(lab);
                            payment.setDate(rs.getDate("dateOfPayment"));
                            payment.setAmount(rs.getFloat("amount"));
                        }
                        if(payment.getDate() == null){
                            labs.add(lab);
                        } else {
                            payments.add(payment);
                        }
                    }
                    student.setPayments(payments);
                    student.setAcceptedInvitations(labs);
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
    
    public void searchDemonstrators(User user, Teacher teacher, Search search) throws DBError{
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
            if(search.isSpecific()){
                query.append(" and(");
                LinkedList<Course> teacherCourses = teacher.getCourses();
                for(int i = 0; i < teacherCourses.size(); i++){
                    Course course = teacherCourses.get(i);
                    if(course.isSelected()){
                        if(i != 0){
                            query.append(" or ");
                        }
                        query.append("d.courseID='");
                        query.append(course.getId());
                        query.append("'");
                        
                    }
                }
                query.append(")");
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
            LinkedList<Demonstrator> result = new LinkedList<Demonstrator>();
            while(rs.next()){
                Demonstrator d = new Demonstrator();
                User u = new User();
                Student s = new Student();
                u.setName(rs.getString("name"));
                u.setSurname(rs.getString("name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                s.setYear(rs.getInt("year"));
                s.setGPA(rs.getFloat("gpa"));
                d.setStudent(s);
                d.setUser(u);
                result.add(d);
            }
            search.setResult(result);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public LinkedList<Classroom> completeClassroom(String part) throws DBError{
        Connection conn = db.getConnection();
        LinkedList<Classroom> classrooms = new LinkedList<Classroom>();
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select ClassroomID, location, type, number from Classroom where (location like '"+part+"%'"+
                            " or type like '"+part+"%'"+" or number like '"+part+"%');";
            ResultSet rs = stat.executeQuery(query);
            while(rs.next()){
                Classroom cr = new Classroom();
                cr.setId(rs.getInt("ClassroomID"));
                cr.setLocation(rs.getString("location"));
                cr.setType(rs.getString("type"));
                cr.setNumber(rs.getInt("number"));
                classrooms.add(cr);
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return classrooms;
    }
    
    public void fillClassroomID(Classroom classroom) throws DBError{
        Connection conn = db.getConnection();
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select ClassroomID from Classroom where (location like '"+classroom.getLocation()+"'"+
                            " and type like '"+classroom.getType()+"'"+" and number = '"+classroom.getNumber()+"');";
            ResultSet rs = stat.executeQuery(query);
            if(rs.next()){
                classroom.setId(rs.getInt("ClassroomID"));
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadDemonstrators(Lab lab) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from Demonstrator d, User u, Student s where d.username=u.username and d.username=s.username and d.CourseID='"+lab.getCourse().getId()+"';";
            ResultSet rs = stat.executeQuery(query);
            LinkedList<Demonstrator> demons = new LinkedList<Demonstrator>();
            while(rs.next()){
                User user= new User();
                Student student = new Student();
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                student.setDepartment(rs.getString("department"));
                student.setYear(rs.getInt("year"));
                student.setGPA(rs.getFloat("gpa"));
                Demonstrator demonstrator = new Demonstrator();
                demonstrator.setUser(user);
                demonstrator.setStudent(student);
                demons.add(demonstrator);
            }
            lab.setDemonstrators(demons);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public boolean saveLab(Lab lab) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "select * from Lab where CourseID='"+lab.getCourse().getId()+"' and name='"+lab.getName()+"';";
            ResultSet rs = stat.executeQuery(query);
            if(rs.next()){
                stat.close();
                DB.getInstance().putConnection(conn);
                return false;
            }
            query = "insert into Lab (CourseID, name, begin, end, ClassroomID, type, maxDemons) values ('"+
                    lab.getCourse().getId()+"','"+lab.getName()+"','"+
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lab.getBegin())+"','"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lab.getEnd())+"','"+
                    lab.getClassroom().getId()+"','"+lab.getType()+"','"+lab.getMaxDemonstrators()+"');";
            stat.executeUpdate(query);
            query = "select max(LabID) as max from Lab;";
            rs = stat.executeQuery(query);
            Integer labID;
            if(rs.next()){
                labID = rs.getInt("max");
            } else {
                stat.close();
                DB.getInstance().putConnection(conn);
                throw new DBError();
            }
            for(Demonstrator demonstrator: lab.getSelectedDemonstrators()){
                query = "insert into InvitedDemons (LabID, username) values ('"+labID+"','"+demonstrator.getUser().getUsername()+"');";
                stat.executeUpdate(query);
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return true;
    }
    
    public void rejectLab(String username, Student student, Lab lab) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "update InvitedDemons set rejected=1, commentary='"+student.getRejectedComment()+"' where LabID='"+lab.getId()+"' and username='"+username+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void acceptLab(String username, Student student, Lab lab) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "delete from InvitedDemons where LabID='"+lab.getId()+"' and username='"+username+"';";
            stat.executeUpdate(query);
            query = "insert into LabDemons (LabID, username) values ('"+lab.getId()+"','"+username+"');";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadLabs(Teacher teacher) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            LinkedList<Lab> labs = new LinkedList<Lab>();
            for(Course course: teacher.getCourses()){
                String query = "select l.LabID, l.name as labname, l.begin, l.end, l.type as labtype, l.closed, "
                        + "cr.ClassroomID, cr.location, cr.type as classtype, cr.number "
                        + "from Lab l, Classroom cr where l.ClassroomID=cr.ClassroomID and l.CourseID='"+course.getId()+"';";
                ResultSet rs = stat.executeQuery(query);
                long today = Calendar.getInstance().getTimeInMillis();
                while(rs.next()){
                    Lab lab = new Lab();
                    Classroom cs = new Classroom();
                    cs.setId(rs.getInt("ClassroomID"));
                    cs.setLocation(rs.getString("location"));
                    cs.setType(rs.getString("classtype"));
                    cs.setNumber(rs.getInt("number"));
                    lab.setClassroom(cs);
                    lab.setId(rs.getInt("LabID"));
                    lab.setCourse(course);
                    lab.setName(rs.getString("labname"));
                    Timestamp begin = rs.getTimestamp("begin");
                    lab.setBegin(new java.util.Date(begin.getTime()));
                    Timestamp end = rs.getTimestamp("end");
                    lab.setEnd(new java.util.Date(end.getTime()));
                    if(end.getTime() < today){
                        lab.setPast(true);
                    }
                    lab.setType(rs.getInt("labtype"));
                    lab.setClosed(rs.getInt("closed") == 1? true: false);
                    Statement statTemp = conn.createStatement();
                    query = "select u.name, u.surname, u.username, s.year, s.gpa, s.department "
                            + "from User u, Student s, LabDemons ld "
                            + "where ld.LabID='"+lab.getId()+"' and ld.username=u.username and s.username=u.username;";
                    ResultSet rsTemp = statTemp.executeQuery(query);
                    LinkedList<Demonstrator> demonstrators = new LinkedList<Demonstrator>();
                    LinkedList<Demonstrator> workingDemonstrators = new LinkedList<Demonstrator>();
                    while(rsTemp.next()){
                        Demonstrator demonstrator = new Demonstrator();
                        User user = new User();
                        user.setName(rsTemp.getString("name"));
                        user.setSurname(rsTemp.getString("surname"));
                        user.setUsername(rsTemp.getString("username"));
                        Student student = new Student();
                        student.setDepartment(rsTemp.getString("department"));
                        student.setYear(rsTemp.getInt("year"));
                        student.setGPA(rsTemp.getFloat("gpa"));
                        demonstrator.setStudent(student);
                        demonstrator.setUser(user);
                        workingDemonstrators.add(demonstrator);
                    }
                    if(lab.isPast()){
                        query = "delete from InvitedDemons where LabID='"+lab.getId()+"';";
                        statTemp.executeUpdate(query);
                    } else {
                        query = "select u.name, u.surname, u.username, s.year, s.gpa, s.department, id.rejected, id.commentary "
                                + "from User u, Student s, InvitedDemons id "
                                + "where id.LabID='"+lab.getId()+"' and id.username=u.username and s.username=u.username;";
                        rsTemp = statTemp.executeQuery(query);
                        while(rsTemp.next()){
                            Demonstrator demonstrator = new Demonstrator();
                            User user = new User();
                            user.setName(rsTemp.getString("name"));
                            user.setSurname(rsTemp.getString("surname"));
                            user.setUsername(rsTemp.getString("username"));
                            Student student = new Student();
                            student.setDepartment(rsTemp.getString("department"));
                            student.setYear(rsTemp.getInt("year"));
                            student.setGPA(rsTemp.getFloat("gpa"));
                            student.setRejectedComment(rsTemp.getString("commentary"));
                            demonstrator.setStudent(student);
                            demonstrator.setUser(user);
                            demonstrator.setRejected(rsTemp.getInt("rejected") == 1? true: false);
                            workingDemonstrators.add(demonstrator);
                        }
                    }
                    query = "select u.name, u.surname, u.username, s.year, s.gpa, s.department "
                            + "from User u, Student s, Demonstrator d "
                            + "where d.CourseID='"+lab.getCourse().getId()+"' and d.username=u.username and s.username=u.username "
                            + "and not exists (select * from InvitedDemons i where i.username=d.username and i.LabID='"+lab.getId()+"') "
                            + "and not exists (select * from LabDemons l where l.username=d.username and l.LabID='"+lab.getId()+"');";
                    rsTemp = statTemp.executeQuery(query);
                    while(rsTemp.next()){
                        Demonstrator demonstrator = new Demonstrator();
                        User user = new User();
                        user.setName(rsTemp.getString("name"));
                        user.setSurname(rsTemp.getString("surname"));
                        user.setUsername(rsTemp.getString("username"));
                        Student student = new Student();
                        student.setDepartment(rsTemp.getString("department"));
                        student.setYear(rsTemp.getInt("year"));
                        student.setGPA(rsTemp.getFloat("gpa"));
                        demonstrator.setStudent(student);
                        demonstrator.setUser(user);
                        demonstrators.add(demonstrator);
                    }
                    lab.setDemonstrators(demonstrators);
                    lab.setWorkingDemonstrators(workingDemonstrators);
                    labs.add(lab);
                    statTemp.close();
                } 
            }
            teacher.setLabs(labs);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void removeDemonstrator(Lab l, Demonstrator d) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "delete from InvitedDemons where LabID='"+l.getId()+"' and username='"+d.getUser().getUsername()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void removeDemonstratorFromLab(Lab l, Demonstrator d) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "delete from LabDemons where LabID='"+l.getId()+"' and username='"+d.getUser().getUsername()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void addWorkingDemonstrator(Lab lab, Demonstrator demonstrator) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "insert into LabDemons (LabID, username) values ('"+lab.getId()+"','"+demonstrator.getUser().getUsername()+"');";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void closeLab(Lab lab) throws DBError{
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query = "update Lab set end='"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lab.getEnd())+"' where LabID='"+lab.getId()+"';";
            stat.executeUpdate(query);
            query = "update Lab set closed=1 where LabID='"+lab.getId()+"';";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadLabs(Admin admin) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            LinkedList<Lab> labs = new LinkedList<Lab>();
            String query = "select l.LabID, l.name as labname, l.begin, l.end, l.type as labtype, l.closed, "
                    + "c.CourseID, c.department, c.teachyear, c.code, c.name, c.semester, c.year, "
                    + "cr.ClassroomID, cr.location, cr.type as classtype, cr.number "
                    + "from Lab l, Classroom cr, Course c "
                    + "where l.ClassroomID=cr.ClassroomID and l.CourseID=c.CourseID and l.closed=1 and l.accounted is null;";
            ResultSet rs = stat.executeQuery(query);
            long today = Calendar.getInstance().getTimeInMillis();
            while(rs.next()){
                Lab lab = new Lab();
                Course c = new Course();
                Classroom cs = new Classroom();
                cs.setId(rs.getInt("ClassroomID"));
                cs.setLocation(rs.getString("location"));
                cs.setType(rs.getString("classtype"));
                cs.setNumber(rs.getInt("number"));
                lab.setClassroom(cs);
                c.setId(rs.getInt("CourseID"));
                c.setDepartment(rs.getString("department"));
                c.setTeachingYear(rs.getInt("teachyear"));
                c.setCode(rs.getString("code"));
                c.setName(rs.getString("name"));
                c.setSemester(rs.getInt("semester") == 1? "winter": "summer");
                lab.setCourse(c);
                lab.setId(rs.getInt("LabID"));
                lab.setName(rs.getString("labname"));
                Timestamp begin = rs.getTimestamp("begin");
                lab.setBegin(new java.util.Date(begin.getTime()));
                Timestamp end = rs.getTimestamp("end");
                lab.setEnd(new java.util.Date(end.getTime()));
                if(end.getTime() < today){
                    lab.setPast(true);
                }
                lab.setType(rs.getInt("labtype"));
                lab.setClosed(rs.getInt("closed") == 1? true: false);
                
                Statement statTemp = conn.createStatement();
                query = "select u.name, u.surname, u.username, s.year, s.gpa, s.department "
                        + "from User u, Student s, LabDemons ld "
                        + "where ld.LabID='"+lab.getId()+"' and ld.username=u.username and s.username=u.username;";
                ResultSet rsTemp = statTemp.executeQuery(query);
                LinkedList<Demonstrator> workingDemonstrators = new LinkedList<Demonstrator>();
                while(rsTemp.next()){
                    Demonstrator demonstrator = new Demonstrator();
                    User user = new User();
                    user.setName(rsTemp.getString("name"));
                    user.setSurname(rsTemp.getString("surname"));
                    user.setUsername(rsTemp.getString("username"));
                    Student student = new Student();
                    student.setDepartment(rsTemp.getString("department"));
                    student.setYear(rsTemp.getInt("year"));
                    student.setGPA(rsTemp.getFloat("gpa"));
                    demonstrator.setStudent(student);
                    demonstrator.setUser(user);
                    workingDemonstrators.add(demonstrator);
                }
                lab.setWorkingDemonstrators(workingDemonstrators);
                labs.add(lab);
                statTemp.close();
            }
            admin.setLabs(labs);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void account(Admin admin) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            for(Lab lab: admin.getSelectedLabs()) {
                query = "update Lab set accounted=1 where LabID='"+lab.getId()+"';";
                stat.executeUpdate(query);
                float amount = 0;
                query = "select l.begin, l.end, c.coef from Lab l, Coefs c where c.type=l.type and l.LabID='"+lab.getId()+"';";
                ResultSet rs = stat.executeQuery(query);
                if(rs.next()){
                    float duration = (float)(rs.getTimestamp("end").getTime() - rs.getTimestamp("begin").getTime())/1000/60/45;
                    amount = duration * rs.getFloat("coef");
                }
                amount *= PRICE;
                java.util.Date today = Calendar.getInstance().getTime();
                query = "update LabDemons set amount='"+amount+"', dateOfPayment='"+new SimpleDateFormat("yyyy-MM-dd").format(today)+
                        "' where LabID='"+lab.getId()+"';";
                stat.executeUpdate(query);
                query = "select u.name, u.surname, u.username, s.year, s.gpa, s.department, sum(ld.amount) as payment "
                        + "from User u, Student s, LabDemons ld, Lab l "
                        + "where ld.username=u.username and s.username=u.username and ld.LabID=l.LabID and"
                        + "l.begin>='"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(admin.getBeginAccountDate())+"'"
                        + " and l.begin<='"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(admin.getEndAccountDate())+"' "
                        + "group by username;";
                rs = stat.executeQuery(query);
                LinkedList<Demonstrator> demonstrators = new LinkedList<Demonstrator>();
                while(rs.next()){
                    Demonstrator demonstrator = new Demonstrator();
                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));
                    Student student = new Student();
                    student.setDepartment(rs.getString("department"));
                    student.setYear(rs.getInt("year"));
                    student.setGPA(rs.getFloat("gpa"));
                    demonstrator.setStudent(student);
                    demonstrator.setUser(user);
                    demonstrator.setPayment(rs.getFloat("payment"));
                    demonstrators.add(demonstrator);
                }
                admin.setDemonstrators(demonstrators);
            }
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void loadEligibleStudents(String username, Teacher teacher) throws DBError {
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
            LinkedList<LinkedList<Demonstrator>> eligibleStudents = new LinkedList<LinkedList<Demonstrator>>();
            for(Course currCourse: teacher.getCourses()) {
                String query = "select u.username, u.name, u.surname, u.phone, u.email, "
                        + "s.department, s.year, s.GPA, c.CourseID "
                        + "from User u, Student s, Course c "
                        + "where u.username=s.username and c.department=s.department and c.teachyear<s.year and "
                        + "((c.year='"+year+"' and c.semester=1) or (c.year='"+(year+1)+"' and c.semester=0))  and "
                        + "c.CourseID='"+currCourse.getId()+"' and "
                        + "u.username not in (select d.username from Demonstrator d where d.CourseID='"+currCourse.getId()+"');";
                ResultSet rs = stat.executeQuery(query);
                LinkedList<Demonstrator> demons = new LinkedList<Demonstrator>();
                while(rs.next()){
                    User user= new User();
                    Student student = new Student();
                    user.setUsername(rs.getString("username"));
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setPhone(rs.getString("phone"));
                    user.setEmail(rs.getString("email"));
                    student.setDepartment(rs.getString("department"));
                    student.setYear(rs.getInt("year"));
                    student.setGPA(rs.getFloat("gpa"));
                    Demonstrator demonstrator = new Demonstrator();
                    demonstrator.setUser(user);
                    demonstrator.setStudent(student);
                    demons.add(demonstrator);
                }
                eligibleStudents.add(demons);
            }
            teacher.setEligibleStudents(eligibleStudents);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
    
    public void addNewDemonstrator(Course c, Demonstrator d) throws DBError {
        Connection conn = db.getConnection();
        
        if(conn==null){
            throw new DBError();
        }
        try {
            Statement stat = conn.createStatement();
            String query;
            query = "insert into Demonstrator (CourseID, username) values ('"+c.getId()+"','"+d.getUser().getUsername()+"');";
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
    }
}
