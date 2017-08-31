package eHistology.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.DbUtil;

public class UpdateLocation extends HttpServlet {

  //private PrintWriter writer = null;
  private String tab = "   ";
  private int indent = 0;
  private String project;
  private Connection db;
  private boolean _debug;

  //---------------------------------------------------------------------
  public UpdateLocation() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in UpdateLocation");
    //System.out.println("doGet in UpdateLocation");

    String loc_oid = "";
    String newx = "0.0";
    String newy = "0.0";

    project = DbUtil.getProject(req);
    if(_debug) System.out.println("UpdateLocation: bundleName " + project);

    loc_oid = req.getParameter("loc_oid");
    newx = req.getParameter("x");
    newy = req.getParameter("y");

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }

       updateLocation(loc_oid, newx, newy);
       DbUtil.closeDBConnection(db);
    }
    catch(Exception e) {
       System.out.println("UpdateLocation: connectToDB: Exception occurred");
    }

  }

  //---------------------------------------------------------------------
  public void updateLocation(String loc_oid, String newx, String newy) {

      Statement statement = null;
      ResultSet rs = null;
      int status = 0;
      String sql;
      String newz = "0.0";
      boolean _deb;

      _deb = _debug;
      _debug = true;
      
      try {

         statement = db.createStatement();
         //-------------------------------------------

         //-------------------------------------------
	 // update the location
         //-------------------------------------------
         sql = "update LOCATION set LOC_X_AXIS = \"" + newx + "\", LOC_Y_AXIS = \"" + newy + "\" where LOC_OID = " + loc_oid;
         if(_debug) System.out.println("sql = " + sql);
         status = statement.executeUpdate(sql);
         if(_debug) System.out.println("updateLocation: returned " + status);
         //-------------------------------------------

      }
      catch(Exception e) {
        System.out.println("updateLocation: " + e.getMessage());
        return;
      }

      if(_debug) System.out.println("updateLocation");

      _debug = _deb;
      
  } // updateLocation

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    UpdateLocation app = new UpdateLocation();
  }

} // class UpdateLocation
