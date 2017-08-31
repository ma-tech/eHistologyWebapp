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

public class GetTitleInfo extends HttpServlet {

  private PrintWriter writer = null;
  private String auth;
  private String tab = "   ";
  private int indent = 0;
  private String json;
  private String project;
  private Connection db;
  private TitleInfo infoForSubplate = null;
  private String subplate = "";
  private String subplate_oid = "";
  private boolean _debug;

  //---------------------------------------------------------------------
  public GetTitleInfo() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in GetTitleInfo");

    subplate = req.getParameter("subplate");
    if(subplate == null || subplate.equals("")) {
       System.out.println("GetTitleInfo: ---------no subplate specified-------------");
       return;
    } else {
       if(_debug) System.out.println("GetTitleInfo: subplate " + subplate);
       //System.out.println("GetTitleInfo: subplate " + subplate);
    }

    project = DbUtil.getProject(req);

    if(_debug) System.out.println("GetTitleInfo: bundleName " + project);

    StringBuffer infoBuf = null;

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
       infoBuf = getTitleInfo();
       json = infoBuf.toString();
       writer.println(json);
       writer.close();
       DbUtil.closeDBConnection(db);
    }
    catch(Exception e) {
       System.out.println("GetTitleInfo.doGet: Exception occurred");
    }
  }

  //---------------------------------------------------------------------
  private StringBuffer getTitleInfo() {


     if(_debug) System.out.println("getTitleInfo");
     
     subplate_oid = getSubplate_oid();
     StringBuffer infoBuf = null;
     TitleInfo title_info = null;

     title_info = getTitleInfoForSubPlate();

     infoBuf = toJson(title_info);

     return infoBuf;

  } // getTitleInfo

  //---------------------------------------------------------------------
  private TitleInfo getTitleInfoForSubPlate() {

     if(_debug) System.out.println("getTitleInfoForSubPlate");

     TitleInfo titleInfo = null;

     ArrayList<Map<String,String>> img_AL = null;
     Map<String,String> imgMap = null;
     ArrayList<Map<String,String>> stage_AL = null;
     Map<String,String> stageMap = null;
     ArrayList<Map<String,String>> aux_AL = null;
     Map<String,String> auxMap = null;

     ArrayList tmp_AL = null;
     Iterator it = null;

     img_AL = getImagesForSubPlate();
     stage_AL = getStagesForSubPlate();
     aux_AL = getAuxiliaryInfoForSubPlate();

     titleInfo = new TitleInfo();
     //titleInfo.setOid(subplate_oid);
     titleInfo.setIdentifier(subplate);

     tmp_AL = new ArrayList();
     it = img_AL.iterator();
     while(it.hasNext()) {
        imgMap = (Map<String,String>)it.next();
        tmp_AL.add(imgMap.get("img_identifier"));
     }
     titleInfo.setImg_AL(tmp_AL);
     tmp_AL = null;

     it = stage_AL.iterator();
     while(it.hasNext()) {
        stageMap = (Map<String,String>)it.next();
        titleInfo.setStage(stageMap.get("stage"));
	//showUnicode(stageMap.get("stage"));         // comment this out 
        titleInfo.setDpc(stageMap.get("dpc"));
     }

     it = aux_AL.iterator();
     while(it.hasNext()) {
        auxMap = (Map<String,String>)it.next();
        titleInfo.setWitschi(auxMap.get("witschi"));
        titleInfo.setCarnegie(auxMap.get("carnegie"));
        titleInfo.setSectionType(auxMap.get("section"));
        titleInfo.setLength(auxMap.get("length"));
        titleInfo.setDescription(auxMap.get("desc"));
     }

     //titleInfo.print();
     return titleInfo;

  } // getTitleInfoForSubPlate

  //---------------------------------------------------------------------
  public String getSubplate_oid() {

     Statement statement = null;
     ResultSet rs = null;
     String query;

     String _id;

     try {
        statement = db.createStatement();
	query = "SELECT PLT_OID FROM ANP_PLATE WHERE PLT_IDENTIFIER='" + subplate + "'";
        if(_debug) System.out.println("GetTitleInfo: getSubplate_oid query = " + query);
	rs = statement.executeQuery(query);

        if(rs == null) {
           if(_debug) System.out.println("GetTitleInfo: getSubplate_oid FAILED");
	   return null;
        }

	rs.next(); // there should only be 1 result
        _id = rs.getString("PLT_OID");
        if(_debug) System.out.println("GetTitleInfo: +++++++++++++++++++++++++++++++++ plt_oid = " + _id);
	return _id;
     }
     catch(Exception e) {
        System.out.println("GetTitleInfo: Exception in getSubplate_oid()");
        System.out.println(e.getMessage());
	return null;
     }
  } // getSubplate_oid

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getImagesForSubPlate() {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> map_AL = new ArrayList<Map<String,String>>();
     String img_identifier = "";
     String img_oid = "";

     try {

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT IMG_OID, IMG_IDENTIFIER FROM ANP_IMAGE WHERE IMG_PLATE_FK=" + subplate_oid);

        if(rs == null) {
           System.out.println("GetTitleInfo: no images for " + subplate);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   map_AL.add(map);
	   img_oid = rs.getString("IMG_OID");
	   map.put("img_oid", img_oid);
	   img_identifier = rs.getString("IMG_IDENTIFIER");
	   map.put("img_identifier", img_identifier);
	} // while
	return map_AL;
     }
     catch(Exception e) {
        System.out.println("GetTitleInfo: Exception in getImagesForSubPlate()");
	return null;
     }
  } // getImagesForSubPlate

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getStagesForSubPlate() {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> map_AL = new ArrayList<Map<String,String>>();
     String stageStr = "";
     String dpcStr = "";

     try {

        statement = db.createStatement();
	rs = statement.executeQuery("select ANP_PLATE_GROUP.PGR_THEILER, ANP_PLATE_GROUP.PGR_DPC from ANP_PLATE_GROUP inner join ANP_PLATE on ANP_PLATE.PLT_GROUP_FK = ANP_PLATE_GROUP.PGR_OID && ANP_PLATE.PLT_OID = " + subplate_oid);

        if(rs == null) {
           System.out.println("GetTitleInfo: no stages for " + subplate);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   map_AL.add(map);
	   stageStr = rs.getString("PGR_THEILER");
	   // get rid of leading zeros,
	   // for an explanation see http://stackoverflow.com/questions/2800739/how-to-remove-leading-zeros-from-alphanumeric-text
	   //stageStr = stageStr.replaceFirst("^0+(?!$)", "");
	   map.put("stage", stageStr);
	   dpcStr = rs.getString("PGR_DPC");
	   map.put("dpc", dpcStr);
	} // while

	return map_AL;
     }
     catch(Exception e) {
        System.out.println("GetTitleInfo: Exception in getStagesForSubPlate()");
	return null;
     }

  } // getStagesForSubPlate

  //---------------------------------------------------------------------
  public void showUnicode(String str) {

     char ch;
     int len;
     int i;
     String unic;

     len = str.length();

     System.out.println(str);
     for (i=0; i<len; i++) {
        ch = str.charAt(i);
	unic = String.format ("\\u%04x", (int)ch);
	System.out.println(unic);
     }
  }

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getAuxiliaryInfoForSubPlate() {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> map_AL = new ArrayList<Map<String,String>>();
     String witschiStr = "";
     String carnegieStr = "";
     String sectionStr = "";
     String lengthStr = "";
     String descStr = "";

     try {

        statement = db.createStatement();
	rs = statement.executeQuery("select a.PLT_IDENTIFIER, a.PLT_GROUP_FK, b.PGR_WITSCHI, b.PGR_CARNEGIE, b.PGR_SECTION_TYPE, b.PGR_LENGTH, b.PGR_DESCRIPTION from ANP_PLATE a, ANP_PLATE_GROUP b where a.PLT_GROUP_FK = b.PGR_OID && a.PLT_OID = " + subplate_oid);

        if(rs == null) {
           System.out.println("GetTitleInfo: no auxiliary info for " + subplate_oid);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   map_AL.add(map);
	   witschiStr = rs.getString("b.PGR_WITSCHI");
	   map.put("witschi", witschiStr);
	   carnegieStr = rs.getString("b.PGR_CARNEGIE");
	   map.put("carnegie", carnegieStr);
	   sectionStr = rs.getString("b.PGR_SECTION_TYPE");
	   map.put("section", sectionStr);
	   lengthStr = rs.getString("b.PGR_LENGTH");
	   map.put("length", lengthStr);
	   descStr = rs.getString("b.PGR_DESCRIPTION");
	   map.put("desc", descStr);

	   //System.out.printf("GetTitleInfo: getAuxiliaryInfoForSubPlate: %s, %s, %s, %s, %s\n",witschiStr,carnegieStr,sectionStr,lengthStr,descStr); 
	   //System.out.printf("map: plate %s, witschi %s\n",subplate_oid,map.get("witschi"));
	} // while

	return map_AL;
     }
     catch(Exception e) {
        System.out.println("GetTitleInfo: Exception in getAuxiliaryInfoForSubPlate()");
	System.out.println(e.getMessage());
	return null;
     }

  } // getAuxiliaryInfoForSubPlate

  //---------------------------------------------------------------------
  private StringBuffer toJson(TitleInfo info) {

     StringBuffer infoBuf = new StringBuffer();
     boolean LAST = true;

     info.printJson(infoBuf, indent, LAST);
     infoBuf.append("\n");

     return infoBuf;
  }

  //---------------------------------------------------------------------
  void printInfo() {
  }

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    GetTitleInfo app = new GetTitleInfo();
  }

} // class GetTitleInfo
