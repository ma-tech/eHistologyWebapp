package eHistology.servlets;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.DbUtil;

public class GetPlateData extends HttpServlet {

  private PrintWriter writer = null;
  private String auth;
  private String tab = "   ";
  private int indent = 0;
  private StringBuffer dataStrBuf;
  private String json;
  private String project;
  private Connection db;
  private boolean _debug;

  //---------------------------------------------------------------------
  public GetPlateData() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in GetPlateData");

    project = DbUtil.getProject(req);
    if(_debug) System.out.println("GetPlateData: eHistology project " + project);

    String plate = req.getParameter("plate");

    plate = (plate == null) ? "" : plate;
    if(_debug) System.out.println("GetPlateData: plate " + plate);

    res.setContentType("text/plain");
    res.setHeader("Cache-Control", "no-cache");
    writer = res.getWriter();

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }

       DatabaseMetaData md = db.getMetaData();
       //if(_debug) System.out.println("catalog is called " +  md.getCatalogTerm());
       dataStrBuf = getData(plate);
       json = dataStrBuf.toString();
       //System.out.println("GetPlateData json " + json);
       writer.println(json);
       writer.close();
       DbUtil.closeDBConnection(db);
       if(_debug) System.out.println("GetPlateData finished");
    }
    catch(Exception e) {
       System.out.println("GetPlateData.getData: Exception occurred");
    }
  }

  //---------------------------------------------------------------------
  private StringBuffer getData(String plate) {

     if(_debug) System.out.println("GetPlateData: getData plate " + plate);

     if(plate == null || plate.equals("") || plate.equals("undefined")) {
        if(_debug) System.out.println("GetPlateData: no plate specified for getData");
	return null;
     } else {
	return getDataForProjectGroup(plate);
     }
  } 

  //---------------------------------------------------------------------
  private StringBuffer getDataForProjectGroup(String plate) {

     dataStrBuf = new StringBuffer();

     ArrayList<Map<String,String>> image_AL;
     Map<String,String> image;
     Iterator img_it = null;
     boolean LAST_IMG = false;

     ArrayList<Map<String,String>> loc_AL;
     Map<String,String> location;
     Iterator loc_it = null;
     boolean LAST_LOC = false;

     String name;
     String plt_oid;

     if(_debug) System.out.println("GetPlateData: getDataForProjectGroup +++++++++++++++++++++++++++++++++ plate = " + plate);
     //System.out.println("GetPlateData: getDataForProjectGroup +++++++++++++++++++++++++++++++++ plate = " + plate);

     //writeStartOfArray(dataStrBuf, "");
     //indent++;

     try {
         writeStartOfObject(dataStrBuf, "");
         plt_oid = getPlate_oid(plate);
         writeStartOfArray(dataStrBuf, "images");
         indent++;
         image_AL = getImagesForPlate(plt_oid);
         img_it = image_AL.iterator();
         while(img_it.hasNext()) {
            writeStartOfObject(dataStrBuf, "");
            indent++;
            image = (Map<String,String>) img_it.next();
            writeObjectElement(dataStrBuf, "id", image.get("image_id"), false);
            writeObjectElement(dataStrBuf, "oid", image.get("oid"), false);
            writeStartOfArray(dataStrBuf, "locations");
            indent++;
            loc_AL = getLocationsForImage(image.get("oid"));
            loc_it = loc_AL.iterator();
            while(loc_it.hasNext()) {
               writeStartOfObject(dataStrBuf, "");
               indent++;
               location = (Map<String,String>) loc_it.next();
               writeObjectElement(dataStrBuf, "oid", location.get("_id"), false);
               writeObjectElement(dataStrBuf, "img_oid", image.get("oid"), false);
               writeObjectElement(dataStrBuf, "img_id", image.get("image_id"), false);
               writeObjectElement(dataStrBuf, "knum", location.get("knum"), false);
               writeObjectElement(dataStrBuf, "term", location.get("term_oid"), false);
               writeObjectElement(dataStrBuf, "x", location.get("x"), false);
               writeObjectElement(dataStrBuf, "y", location.get("y"), false);
               writeObjectElement(dataStrBuf, "z", location.get("z"), true);
               indent--;
               LAST_LOC = (loc_it.hasNext()) ? false : true;
               writeEndOfObject(dataStrBuf, LAST_LOC);
            }
            indent--;
            writeEndOfArray(dataStrBuf, true);
            indent--;
            LAST_IMG = (img_it.hasNext()) ? false : true;
            writeEndOfObject(dataStrBuf, LAST_IMG);
         }
         indent--;
         writeEndOfArray(dataStrBuf, true);
         writeEndOfObject(dataStrBuf, true);
     }
     catch(Exception e) {
        System.out.println("GetPlateData problem");
     }

     //indent--;
     //writeEndOfArray(dataStrBuf, true);

     return dataStrBuf;
  } // getDataForAtlasSubplate

  //---------------------------------------------------------------------
  public String getPlate_oid(String plate) {

     Statement statement = null;
     ResultSet rs = null;
     String query;

     String _id;

     if(_debug) System.out.println("GetPlateData: getSubplate_oid plate = " + plate);
     try {
        if(plate == null || plate.equals("")) {
           if(_debug) System.out.println("GetPlateData: ---------no plate specified-------------");
	   return null;
        }

        statement = db.createStatement();
	query = "SELECT GRP_OID FROM IMAGE_GROUP WHERE GRP_NAME='" + plate + "'";
        if(_debug) System.out.println("GetPlateData: getSubplate_oid query = " + query);
	rs = statement.executeQuery(query);

        if(rs == null) {
           if(_debug) System.out.println("GetPlateData: gePlatelate_oid FAILED");
	   return null;
        }

	rs.next(); // there should only be 1 result
        _id = rs.getString("GRP_OID");
        if(_debug) System.out.println("GetPlateData: +++++++++++++++++++++++++++++++++ plate_oid = " + _id);
	return _id;
     }
     catch(Exception e) {
        System.out.println("GetPlateData: Exception in GetPlateData.getPlate_oid()");
        System.out.println(e.getMessage());
	return null;
     }
  } // getPlate_oid

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getImagesForPlate(String plate_oid) {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> mapVec = new ArrayList<Map<String,String>>();
     String img_oid = "";
     String img_id = "";
     //String plate_id = "";
     String query;

     if(_debug) System.out.println("GetPlateData: +++++++++++++++++++++++++++++++ getImagesForPlate oid = " + plate_oid);

     try {
        if(plate_oid == null || plate_oid.equals("")) {
           if(_debug) System.out.println("GetPlateData: ---------no plate specified-------------");
	   return null;
        }

        statement = db.createStatement();
	query = "SELECT * FROM IMAGE WHERE IMG_GROUP_FK=" + plate_oid;
        if(_debug) System.out.println("GetPlateData: +++++++++++++++++++++++++++++++ getImagesForPlate query = " + query);
	rs = statement.executeQuery(query);

        if(rs == null) {
           if(_debug) System.out.println("GetPlateData: no images for plate oid " + plate_oid);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   mapVec.add(map);
	   map.put("image_id", rs.getString("IMG_IDENT"));
	   map.put("oid", rs.getString("IMG_OID"));
	} // while
	return mapVec;
     }
     catch(Exception e) {
        System.out.println("GetPlateData: Exception in GetPlateData.getImagesForPlate()");
	return null;
     }
  } // getImagesForPlate

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getLocationsForImage(String oid) {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> mapVec = new ArrayList<Map<String,String>>();
     String _id = "";
     String image_id = "";
     String knum = "";
     String term_oid = "";
     String x = "";
     String y = "";
     String z = "";

     if(_debug) System.out.println("getLocationsForImage " + oid);

     try {
        if(oid == null || oid.equals("")) {
           if(_debug) System.out.println("---------no image specified-------------");
	   return null;
        }

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT * FROM LOCATION WHERE LOC_IMAGE_FK=" + oid);
        if(rs == null) {
           if(_debug) System.out.println("no locations for " + oid);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   mapVec.add(map);
	   _id = rs.getString("LOC_OID");
	   map.put("_id", _id);
	   knum = rs.getString("LOC_K_NUM");
	   map.put("knum", knum);
	   term_oid = rs.getString("LOC_TERM_FK");
	   map.put("term_oid", term_oid);
	   image_id = rs.getString("LOC_IMAGE_FK");
	   map.put("image_id", image_id);
	   x = rs.getString("LOC_X_AXIS");
	   map.put("x", x);
	   y = rs.getString("LOC_Y_AXIS");
	   map.put("y", y);
	   z = rs.getString("LOC_Z_AXIS");
	   map.put("z", z);
	} // while
	//_debug = deb;
	return mapVec;
     }
     catch(Exception e) {
        System.out.println("GetPlateData: Exception in GetPlateData.getLocationsForImage()");
	return null;
     }
  } // getLocationsForImage

  //---------------------------------------------------------------------
  public ArrayList <Map<String,String>> getRefsForLocation(String loc_id) {

     if(_debug) System.out.println("GetPlateData: getRefForLocation " + loc_id);

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> mapVec = new ArrayList<Map<String,String>>();

     //return null;

     String _id = "";
     //String loc_fk = "";
     String term_fk = "";
     String emap = "";
     String emapa = "";
     String stage_fk = "";
     String emap_name = "";
     String wiki = "";

     try {
        if(loc_id == null || loc_id.equals("")) {
           if(_debug) System.out.println("GetPlateData: --------- getRefForLocation: no _id specified -------------");
	   return null;
        }

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT * FROM ANP_SUPPLEMENTAL WHERE SUP_LOCATION_FK = " + loc_id);

        if(rs == null) {
           if(_debug) System.out.println("GetPlateData: no external refs for location " + loc_id);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   mapVec.add(map);
	   _id = rs.getString("SUP_OID");
	   map.put("_id", _id);
	   //loc_fk = rs.getString("SUP_LOCATION_FK");
	   map.put("loc_fk", loc_id);
	   term_fk = rs.getString("SUP_TERM_FK");
	   map.put("term_fk", term_fk);
	   emap = rs.getString("SUP_EMAP");
	   map.put("emap", emap);
	   emapa = rs.getString("SUP_EMAPA");
	   map.put("emapa", emapa);
	   wiki = rs.getString("SUP_WIKI");
	   map.put("wiki", wiki);
	   stage_fk = rs.getString("SUP_STAGE_FK");
	   map.put("stage_fk", stage_fk);
	   emap_name = rs.getString("SUP_EMAP_NAME");
	   map.put("emap_name", emap_name);
	} // while
	return mapVec;
     }
     catch(Exception e) {
        System.out.println("Exception in GetPlateData.getRefsForTerm()");
	return null;
     }
  } // getRefsForLocation

  //---------------------------------------------------------------------
  public void writeStartOfArray(StringBuffer buf, String name) {
     
     buf.append("\n");
     for(int i=0; i<indent; i++) {
        buf.append(tab);
     }
     if(name == null || name.equals("")) {
        buf.append("[");
     } else {
        buf.append("\"" + name + "\": ["); 
     }

  } // writeStartOfArray

  //---------------------------------------------------------------------
  public void writeEndOfArray(StringBuffer buf, boolean last) {
     
     buf.append("\n");
     for(int i=0; i<indent; i++) {
        buf.append(tab);
     }
     if(last) {
        buf.append("]");
     } else {
        buf.append("],");
     }

  } // writeEndOfArray

  //---------------------------------------------------------------------
  public void writeStartOfObject(StringBuffer buf, String name) {
     
     buf.append("\n");
     for(int i=0; i<indent; i++) {
        buf.append(tab);
     }
     if(name == null || name.equals("")) {
        buf.append("{");
     } else {
        buf.append("\"" + name + "\": {"); 
     }

  } // writeStartOfObject

  //---------------------------------------------------------------------
  public void writeObjectElement(StringBuffer buf, String key, String val, boolean last) {

     buf.append("\n");
     for(int i=0; i<indent; i++) {
        buf.append(tab);
     }
     if(last) {
        buf.append("\"" + key + "\": \"" + val + "\""); 
     } else {
        buf.append("\"" + key + "\": \"" + val + "\","); 
     }
     
  } // writeObjectElement

  //---------------------------------------------------------------------
  public void writeEndOfObject(StringBuffer buf, boolean last) {

     buf.append("\n");
     for(int i=0; i<indent; i++) {
        buf.append(tab);
     }
     if(last) {
        buf.append("}");
     } else {
        buf.append("},");
     }

  } // writeEndOfObject

  //---------------------------------------------------------------------
  void printData() {
     if(_debug) System.out.println(dataStrBuf.toString());
  }

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    GetPlateData app = new GetPlateData();
  }

} // class GetPlateData
