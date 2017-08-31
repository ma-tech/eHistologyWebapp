package eHistology.util;

import java.io.*;
import java.util.ResourceBundle;
import javax.servlet.http.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DbUtil {

  //---------------------------------------------------------------------
  static public ResourceBundle getResources(String bundleName) {
      ResourceBundle bundle = null;
     try {
	bundle = ResourceBundle.getBundle(bundleName);
     }
     catch(Exception e) {
        System.out.println("GetPlateData: Woops, GetPlateData.getResources problem: "+bundleName);
        System.out.println(e.getMessage());
     }
     return bundle;
  }

  //---------------------------------------------------------------------
  static public Connection getDBConnection(String project) {

     Connection conn = null;
     try {
	ResourceBundle bundle = getResources("eHistology");
	Class.forName(bundle.getString("db_driver"));
	String url = bundle.getString("host") + bundle.getString("database");
	String userName = bundle.getString("user");
	String passWord = bundle.getString("password");
        //if(_debug) System.out.println("GetPlateData: url " + url);
	conn = DriverManager.getConnection(url, userName, passWord);
     } catch (SQLException se) {
	se.printStackTrace();
     } catch (ClassNotFoundException cfe) {
	cfe.printStackTrace();
     }

     return conn;
  } 

  //---------------------------------------------------------------------
  static public void closeDBConnection(Connection db) {

     if (db != null) {
        try {
	   db.close();
	} catch (Exception e) {
	   System.out.println("GetPlateData: Exception closing DB connection()");
	   System.out.println(e.getMessage());
	}
     }
  } 

  //---------------------------------------------------------------------
  static public String getProject(HttpServletRequest req) {

     String ret;
     String param = req.getParameter("project");

     ret = (param == null || param == "") ? "eHistology" : param;

     return ret;
  }


} 
