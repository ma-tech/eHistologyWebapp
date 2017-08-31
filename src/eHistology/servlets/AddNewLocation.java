package eHistology.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.DbUtil;

public class AddNewLocation extends HttpServlet {

  private String project;
  private Connection db;
  private boolean _debug;

  //---------------------------------------------------------------------
  public AddNewLocation() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in AddNewLocation");
    //System.out.println("doGet in AddNewLocation");

    String img_ident = "";
    String trm_id = "";
    String knum = "";
    String newx = "0.0";
    String newy = "0.0";

    project = DbUtil.getProject(req);
    if(_debug) System.out.println("AddNewLocation: bundleName " + project);

    img_ident = req.getParameter("image_ident");
    trm_id = req.getParameter("term_id");
    knum = req.getParameter("knum");
    newx = req.getParameter("x");
    newy = req.getParameter("y");

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }

       addNewLocation(img_ident, trm_id, newx, newy, knum);
       DbUtil.closeDBConnection(db);
    }
    catch(Exception e) {
       System.out.println("AddNewLocation: connectToDB: Exception occurred");
    }

  }

   //---------------------------------------------------------------------
  public void addNewLocation(String img_ident, String trm_id, String newx, String newy, String knum) {

      Statement statement = null;
      ResultSet rs = null;
      int status = 0;
      int loc_oid;
      int num_annotations = -1;
      String sql;
      String img_fk_str = "";
      String newz = "0.0";
      boolean _deb;

      _deb = _debug;
      //_debug = true;
      
      try {

         statement = db.createStatement();
         //-------------------------------------------
         rs = statement.executeQuery("select IMG_OID from IMAGE where IMG_IDENT = \"" + img_ident + "\"");
         
         if(rs == null) {
           if(_debug) System.out.println("AddNewLocation: no image oid");
           return;
         }
         
         while(rs.next()) {
           img_fk_str = rs.getString("IMG_OID");
         } // while

         //-------------------------------------------
	 // insert a new location
         //-------------------------------------------
         sql = "insert into LOCATION (LOC_X_AXIS, LOC_Y_AXIS, LOC_Z_AXIS, LOC_IMAGE_FK, LOC_K_NUM, LOC_TERM_FK) values (\"" + newx + "\",\"" + newy + "\",\"" + newz + "\"," + img_fk_str + ",\"" + knum + "\",\"" + trm_id + "\")";
         if(_debug) System.out.println("sql = " + sql);
         status = statement.executeUpdate(sql);
         if(_debug) System.out.println("addNewLocation: insert returned " + status);
         //-------------------------------------------
         
      }
      catch(Exception e) {
        System.out.println("addNewLocation: " + e.getMessage());
        return;
      }

      if(_debug) System.out.println("addNewLocation " + knum + ", " + img_fk_str);
      System.out.println("addNewLocation " + knum + ", " + img_fk_str);

      _debug = _deb;
      
  } // addNewLocation

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    AddNewLocation app = new AddNewLocation();
  }

} // class AddNewLocation
