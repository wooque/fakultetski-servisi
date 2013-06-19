package db;

import java.sql.*;

public class DB {

  private static DB instance;
  private static final int MAX_CON = 1;
  private static final Connection[] bafer = new Connection[MAX_CON];
  private int first = 0, last = 0, free = MAX_CON;

  private DB() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      for (int i = 0; i < MAX_CON; i++) {
        bafer[i] = DriverManager.getConnection("jdbc:mysql://localhost:3306/fakultet", "root", "");
      }
    } catch (Exception e) {
        System.out.println(e.getStackTrace());
    }
  }

  public static DB getInstance() {
    if (instance == null) {
      instance = new DB();
    }
    return instance;
  }

  public synchronized Connection getConnection() {
    if (free == 0) {
      return null;
    }
    free--;
    Connection con = bafer[first];
    first = (first + 1) % MAX_CON;
    return con;
  }

  public synchronized void putConnection(Connection con) {
    if (con == null) {
      return;
    }
    free++;
    bafer[last] = con;
    last = (last + 1) % MAX_CON;
  }
}
