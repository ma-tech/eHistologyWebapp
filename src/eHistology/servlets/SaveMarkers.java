package eHistology.servlets;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import org.codehaus.jackson.*;

import eHistology.util.DbUtil;

public class SaveMarkers extends HttpServlet {

  //private PrintWriter writer = null;
  private String tab = "   ";
  private int indent = 0;
  private String json;
  private String project;
  private Connection db;
  private boolean _debug;

  //---------------------------------------------------------------------
  public SaveMarkers() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in SaveMarkers");

    String jsonStr = req.getParameter("markers");
    if(_debug) System.out.println("doGet in SaveMarkers " + jsonStr);

    project = DbUtil.getProject(req);

    if(_debug) System.out.println("SaveMarkers: bundleName " + project);

    LocationData[] locdatArr = parseJsonStr(jsonStr);

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }

       updateLocations(locdatArr);
       DbUtil.closeDBConnection(db);       
    }
    catch(Exception e) {
       System.out.println("SaveMarkers: connectToDB: Exception occurred");
    }

  }

  public LocationData[] parseJsonStr(String json) {

    JsonFactory jf;
    JsonParser jp;
    boolean deb;

    deb = _debug;
    //_debug = true;

    try {
      jf = new JsonFactory();
      if(jf == null) {
        if(_debug) System.out.println("SaveMarkers: jf is null");
      }
      jp = jf.createJsonParser(json);
      if(jp == null) {
        if(_debug) System.out.println("SaveMarkers: jp is null");
      } else {
        //if(_debug) System.out.println("SaveMarkers: jackson json parser version " + jp.version());
      }

      ArrayList<LocationData> locdat_AL = new ArrayList<LocationData>();

      JsonToken theValue = jp.nextValue();
      JsonToken theToken;
      String tokenName;

      tokenName = jp.getCurrentName();
      theToken = jp.getCurrentToken();
      if(_debug) System.out.println("SaveMarkers: initially " + theToken + ", token name " + tokenName + " ==> " + theToken.asString());

      while (jp.nextValue() != null) {
         if(jp.hasCurrentToken()) {
            tokenName = jp.getCurrentName();
            theToken = jp.getCurrentToken();
	    if(_debug) System.out.println(theToken + ", token name " + tokenName + " ==> " + theToken.asString());
            if(theToken == JsonToken.START_OBJECT) {
               LocationData locdat = new LocationData();
               if(_debug) System.out.println("SaveMarkers: start location object");
               while (jp.nextValue() != JsonToken.END_OBJECT) {
                  tokenName = jp.getCurrentName();
                  theToken = jp.getCurrentToken();
	          if(_debug) System.out.println(theToken + ", token name " + tokenName + " ==> " + theToken.asString());
	          if(tokenName == "loc_oid") {
                     //String val = locdat.locationId.valueOf(jp.getText());
                     String val = jp.getText();
		     locdat.setLocationId(val);
		     if(_debug) System.out.println("id = " +locdat.getLocationId());
	          }
		  if(tokenName == "x") {
		     if(_debug) System.out.println(jp.getText());
		     //String val = locdat.x.valueOf(jp.getText());
		     String val = jp.getText();
		     locdat.setX(val);
		     //locdat.getLocationId();
		     //if(_debug) System.out.println("setting x: " + val);
		     //locdat.getX();
		  }
		  if(tokenName == "y") {
		     //String val = locdat.y.valueOf(jp.getText());
		     String val = jp.getText();
		     locdat.setY(val);
		     //locdat.getLocationId();
		     //if(_debug) System.out.println("setting y: " + val);
		     //locdat.getY();
		  }
		  if(tokenName == "z") {
		     //String val = locdat.z.valueOf(jp.getText());
		     String val = jp.getText();
		     locdat.setZ(val);
		     //locdat.getLocationId();
		     //if(_debug) System.out.println("setting z: " + val);
		     //locdat.getZ();
		  }
	       }
               if(_debug) System.out.println("end location object");
               if(_debug) System.out.println("SaveMarkers: adding location object " + locdat.getLocationId());
	       locdat_AL.add(locdat);
               if(_debug) System.out.println("size of locdat_AL now " + locdat_AL.size());
	       //locdat.printObj();
	    }	
	 }
      } // while
        jp.close(); // ensure resources get cleaned up timely and properly
	LocationData[] locdatArr = new LocationData[locdat_AL.size()];
	locdatArr = locdat_AL.toArray(locdatArr);
	//_debug = deb;
	return locdatArr;
    }
    catch(Exception e) {
       System.out.println("SaveMarkers.parseJsonStr: Exception occurred");
       System.out.println(e.getMessage());
       return null;
    }
  } // parseJsonStr

  //---------------------------------------------------------------------
  public void updateLocations(LocationData[] locdatArr) {

     Statement statement = null;
     String newx = "0";
     String newy = "0";
     String newz = "0";
     int status = 0;

     int len = locdatArr.length;
     LocationData locdat;

     try {
        statement = db.createStatement();
   
        for(int i=0; i<len; i++) {
           locdat = locdatArr[i];
           if(_debug) System.out.println("SaveMarkers: updateLocations locationId " + locdat.getLocationId());
           newx = locdat.getX();
           newy = locdat.getY();
           newz = locdat.getZ();
           status = statement.executeUpdate("UPDATE LOCATION SET LOC_X_AXIS=" + newx + 
                                                              ", LOC_Y_AXIS=" + newy +
                                                              ", LOC_Z_AXIS=" + newz +
                                                              " WHERE LOC_OID=" + locdat.getLocationId());
           if(_debug) System.out.println("SaveMarkers: updateLocations  number of rows changed = " + status);
        }
     }
     catch(Exception e) {
        System.out.println("SaveMarkers: updateLocations: " + e.getMessage());
	return;
     }

  } // updateLocations


  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    SaveMarkers app = new SaveMarkers();
  }

} // class SaveMarkers
