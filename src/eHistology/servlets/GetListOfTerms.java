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

public class GetListOfTerms extends HttpServlet {

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
  public GetListOfTerms() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in GetListOfTerms");

    project = DbUtil.getProject(req);
    if(_debug) System.out.println("GetListOfTerms: project " + project);

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
       dataStrBuf = getData();
       json = dataStrBuf.toString();
       //System.out.println("GetListOfTerms json " + json);
       writer.println(json);
       writer.close();
       DbUtil.closeDBConnection(db);
       if(_debug) System.out.println("GetListOfTerms finished");
    }
    catch(Exception e) {
       System.out.println("GetListOfTerms.getData: Exception occurred");
    }
  }

  //---------------------------------------------------------------------
  private StringBuffer getData() {
     dataStrBuf = new StringBuffer();

     ArrayList<Map<String,String>> term_AL;
     Map<String,String> term;
     Iterator term_it = null;
     boolean LAST_TERM = false;

     if(_debug) System.out.println("GetListOfTerms: getData +++++++++++++++++++++++++++++++++");
     //System.out.println("GetListOfTerms: getData +++++++++++++++++++++++++++++++++");

     //writeStartOfArray(dataStrBuf, "");
     //indent++;

     try {
         writeStartOfObject(dataStrBuf, "");
         writeStartOfArray(dataStrBuf, "terms");
         indent++;
         term_AL = getTermsForProject();
         term_it = term_AL.iterator();
         while(term_it.hasNext()) {
            writeStartOfObject(dataStrBuf, "");
            indent++;
            term = (Map<String,String>) term_it.next();
            writeObjectElement(dataStrBuf, "term_id", term.get("term_oid"), false);
            writeObjectElement(dataStrBuf, "description", term.get("description"), false);
            writeObjectElement(dataStrBuf, "sequence", term.get("sequence"), false);
            writeObjectElement(dataStrBuf, "provenance_id", term.get("provenance_oid"), true);
            indent--;
            LAST_TERM = (term_it.hasNext()) ? false : true;
            writeEndOfObject(dataStrBuf, LAST_TERM);
         }
         indent--;
         writeEndOfArray(dataStrBuf, true);
         writeEndOfObject(dataStrBuf, true);
     }
     catch(Exception e) {
        System.out.println("GetListOfTerms problem");
     }

     //indent--;
     //writeEndOfArray(dataStrBuf, true);

     return dataStrBuf;
  } // getDataForAtlasSubplate

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getTermsForProject() {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> mapVec = new ArrayList<Map<String,String>>();
     String query;

     if(_debug) System.out.println("GetListOfTerms: +++++++++++++++++++++++++++++++ getTermsForProject project = " + project);

     try {
        if(project == null || project.equals("")) {
           if(_debug) System.out.println("GetListOfTerms: ---------no project specified-------------");
	   return null;
        }

        statement = db.createStatement();
	query = "select a.TRM_OID, a.TRM_DESCRIPTION, a.TRM_SEQVAL, a.TRM_PROVENANCE_FK from TERM a , PROJECT p where p.PRJ_NAME = '" + project + "' and a.TRM_PROJECT_FK = p.PRJ_OID;";
        if(_debug) System.out.println("GetListOfTerms: +++++++++++++++++++++++++++++++ getTermsForProject query = " + query);
	rs = statement.executeQuery(query);

        if(rs == null) {
           if(_debug) System.out.println("GetListOfTerms: terms for project " + project);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   mapVec.add(map);
	   map.put("term_oid", rs.getString("TRM_OID"));
	   map.put("description", rs.getString("TRM_DESCRIPTION"));
	   map.put("sequence", rs.getString("TRM_SEQVAL"));
	   map.put("provenance_oid", rs.getString("TRM_PROVENANCE_FK"));
	} // while
	return mapVec;
     }
     catch(Exception e) {
        System.out.println("GetListOfTerms: Exception in GetListOfTerms.getTermsForProject()");
	return null;
     }
  } // getTermsForProject

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

} // class GetListOfTerms
