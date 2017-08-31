package eHistology.servlets;

import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.JsonUtil;;
import eHistology.util.DbUtil;


public class GetListOfPlates extends HttpServlet {

  private PrintWriter writer = null;
  private String auth;
  private String tab = "   ";
  private int indent = 0;
  private String json;
  private String project;
  private Connection db;
  private Vector<String> PlateListVec = null;
  private boolean _debug;

  //---------------------------------------------------------------------
  public GetListOfPlates() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in GetListOfPlates");

    StringBuffer infoBuf = null;

    project = DbUtil.getProject(req);

    if(_debug) System.out.println("GetListOfPlates: project " + project);

    res.setHeader("Cache-Control", "no-cache");
    res.setHeader("Content-Type", "text/html; charset=UTF-8");
    writer = res.getWriter();

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }

       DatabaseMetaData md = db.getMetaData();
       //if(_debug) System.out.println("catalog is called " +  md.getCatalogTerm());
       infoBuf = getInfo();
       json = infoBuf.toString();
       writer.println(json);
       writer.close();
       DbUtil.closeDBConnection(db);
       if(_debug) System.out.println("GetListOfPlates finished");
    }
    catch(Exception e) {
       System.out.println("GetListOfPlates: doGet: Exception occurred");
    }
  }

  //---------------------------------------------------------------------
  private StringBuffer getInfo() {

     if(_debug) System.out.println("GetListOfPlates: getInfo");
     
     Vector<String> plateVec;
     StringBuffer infoBuf = null;
     Statement statement = null;
     ResultSet rs = null;
     String identifier = "";
     String id = "";

     plateVec = new Vector<String>();

     try {

        statement = db.createStatement();
	rs = statement.executeQuery("select a.GRP_IDENT from IMAGE_GROUP a , PROJECT p where p.PRJ_NAME = '" + project + "' and a.GRP_PROJECT_FK = p.PRJ_OID");

        if(rs == null) {
           System.out.println("GetListOfPlates: no plates");
	   return null;
        }

	while(rs.next()) {
	   //identifier = rs.getString("PGR_IDENTIFIER");
	   //groupVec.add(identifier);
	   id = rs.getString("GRP_IDENT");
	   plateVec.add(id);
	} // while
     }
     catch(Exception e) {
        System.out.println("GetListOfPlates: Exception in getInfo()");
	return null;
     }

     infoBuf = toJson(plateVec);

     return infoBuf;

  } // getInfo

  //---------------------------------------------------------------------
  private StringBuffer toJson(Vector<String> vec) {

     String info = "";
     StringBuffer infoBuf = new StringBuffer();
     Iterator it = null;
     boolean LAST = true;

     JsonUtil.writeStartOfArray(infoBuf, "", false, indent);
     indent++;

     it = vec.iterator();
     while(it.hasNext()) {
        info = (String) it.next();
	LAST = it.hasNext() ? false : true;
        JsonUtil.writeArrayElement(infoBuf, info, LAST, true, indent);
     }

     indent--;
     JsonUtil.writeEndOfArray(infoBuf, true, true, indent);

     return infoBuf;
  }

  //---------------------------------------------------------------------
  void printInfo() {
  }

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    GetListOfPlates app = new GetListOfPlates();
  }

} // class GetListOfPlates
