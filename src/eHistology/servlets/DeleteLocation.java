package eHistology.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.DbUtil;

public class DeleteLocation extends HttpServlet {

  //private PrintWriter writer = null;
  private String tab = "   ";
  private int indent = 0;
  private String project;
  private Connection db;
  private boolean _debug;

  //---------------------------------------------------------------------
  public DeleteLocation() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in DeleteLocation");
    //System.out.println("doGet in DeleteLocation");

    String loc_ident = "";

    project = DbUtil.getProject(req);
    if(_debug) System.out.println("DeleteLocation: bundleName " + project);

    loc_ident = req.getParameter("loc_oid");

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }

       deleteLocation(loc_ident);
       DbUtil.closeDBConnection(db);
    }
    catch(Exception e) {
       System.out.println("DeleteLocation: connectToDB: Exception occurred");
    }

  }

  //---------------------------------------------------------------------
  public void deleteLocation(String loc_ident) {

      if(_debug) System.out.println("deleteLocation");

      Statement statement = null;
      ResultSet rs = null;
      int status;
      String sql;
      
      try {

         statement = db.createStatement();
         //-------------------------------------------
	 sql = "delete from LOCATION where LOC_OID = " + loc_ident;
         if(_debug) System.out.println("sql = " + sql);
         status = statement.executeUpdate(sql);
         if(_debug) System.out.println("deleteLocation: delete returned " + status);
         
      }
      catch(Exception e) {
        System.out.println("deleteLocation: " + e.getMessage());
        return;
      }

      if(_debug) System.out.println("deleteLocation " + loc_ident);
      
  } // deleteLocation

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    DeleteLocation app = new DeleteLocation();
  }

} // class DeleteLocation
