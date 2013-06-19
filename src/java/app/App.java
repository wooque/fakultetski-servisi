package app;

import com.corejsf.User;
import db.DB;
import db.DBError;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    
    public boolean login(User user) throws DBError{

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
            stat.close();
        } catch (SQLException ex) {
            DB.getInstance().putConnection(conn);
            throw new DBError();
        }
        DB.getInstance().putConnection(conn);
        return true;
    }
}
