package eHistology.servlets;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.DbUtil;
import eHistology.util.JsonUtil;

public class GetAnnotationForImage extends HttpServlet {

  private PrintWriter writer = null;
  private String auth;
  private String tab = "   ";
  private int indent = 0;
  private String json;
  private String plt_oid = "";
  private String project;
  private String img_oid = "";
  private String image_id = "";
  private Connection db;
  private boolean _debug;

  //---------------------------------------------------------------------
  public GetAnnotationForImage() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in GetAnnotationForImage");

    project = DbUtil.getProject(req);
    if(_debug) System.out.println("GetAnnotationForImage: bundleName " + project);

    String plate = req.getParameter("plate");
    image_id = req.getParameter("image_id");
    //System.out.println("doGet in GetAnnotationForImage subplate: " + subplate + " image_id: " + image_id);

    StringBuffer annotBuf = null;

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
       plt_oid = getPlateOID(plate);
       //System.out.println("GetAnnotationForImage.doGet: plt_oid " + plt_oid);
       //img_oid = getImageOID(image_id);
       //System.out.println("GetAnnotationForImage.doGet: img_oid " + img_oid);
       annotBuf = getAnnotations();
       json = annotBuf.toString();
       writer.println(json);
       writer.close();
       DbUtil.closeDBConnection(db);
    }
    catch(Exception e) {
       System.out.println("GetAnnotationForImage.doGet: Exception occurred");
       System.out.println(e.getMessage());
    }
  }

  //---------------------------------------------------------------------
  private StringBuffer getAnnotations() {

     if(_debug) System.out.println("getAnnotations");

     StringBuffer annotBuf = null;

     AnnotationInfo annotInfo = null;;
     ArrayList<ArrayList<AnnotationInfo>> plateAnnotAL = null;
     ArrayList<AnnotationInfo> imgAnnotAL = null;
     ArrayList<Map<String,String>> imgAL = null;

     plateAnnotAL = new ArrayList<ArrayList<AnnotationInfo>>();

     Map<String,String> imgMap = null;

     Iterator it = null;

     imgAL = getImagesForPlate();

     it = imgAL.iterator();
     while(it.hasNext()) {
        imgMap = (Map<String,String>) it.next();
        imgAnnotAL = getAnnotationForImage(imgMap);
	//System.out.println("getAnnotations image_id: " + image_id + " imgMap.get(\"imag_ident\"): " + imgMap.get("img_ident"));
	if ( imgMap.get("img_ident").equals(image_id) ) {
	    plateAnnotAL.add(imgAnnotAL);
	}
     }

     //annotInfo.print();

     annotBuf = toJson(plateAnnotAL);
     return annotBuf;

  } // getAnnotations

  //---------------------------------------------------------------------
  public String getPlateOID(String plate) {

     Statement statement = null;
     ResultSet rs = null;

     String oid = "";

     try {
        if(plate == null || plate.equals("")) {
           System.out.println("---------no plate specified-------------");
	   return null;
	}

	//System.out.println("plt_oid " + plt_oid);

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT GRP_OID FROM IMAGE_GROUP WHERE GRP_IDENT = \"" + plate + "\"");

        if(rs == null) {
           System.out.println("no plt_oid for " + plate);
	   return null;
        }

	while(rs.next()) {
	   oid = rs.getString("GRP_OID");
	} // while

	return oid;
     }
     catch(Exception e) {
        System.out.println("Exception in getPlateOID()");
	return null;
     }

  } // getPlateOID

 //---------------------------------------------------------------------
  public String getImageOID(String image_id) {

     Statement statement = null;
     ResultSet rs = null;

     String oid = "";

     try {
        if(image_id == null || image_id.equals("")) {
           System.out.println("---------no image_id specified-------------");
	   return null;
	}

	//System.out.println("image_oid " + oid);

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT IMG_OID, FROM IMAGE WHERE IMG_GROUP_FK=" + plt_oid +"AND IMG_IDENT = \"" + image_id + "\"");

        if(rs == null) {
           System.out.println("no image_oid for " + image_id);
	   return null;
        }

	while(rs.next()) {
	   oid = rs.getString("IMG_OID");
	} // while

	return oid;
     }
     catch(Exception e) {
        System.out.println("Exception in getIMAGEOID()");
	return null;
     }

  } // getImageOID

  //---------------------------------------------------------------------
  public ArrayList<Map<String,String>> getImagesForPlate() {

     Statement statement = null;
     ResultSet rs = null;

     Map<String,String> map;
     ArrayList<Map<String,String>> mapAL = new ArrayList<Map<String,String>>();
     String img_ident = "";
     String img_oid = "";

     try {
        if(plt_oid == null || plt_oid.equals("")) {
           System.out.println("---------no plate specified-------------");
	   return null;
	}

	//System.out.println("plt_oid " + plt_oid);

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT IMG_OID, IMG_IDENT FROM IMAGE WHERE IMG_GROUP_FK=" + plt_oid);

        if(rs == null) {
           System.out.println("no images for " + plt_oid);
	   return null;
        }

	while(rs.next()) {
	   map = new HashMap<String,String>();
	   mapAL.add(map);
	   img_oid = rs.getString("IMG_OID");
	   map.put("img_oid", img_oid);
	   img_ident = rs.getString("IMG_IDENT");
	   map.put("img_ident", img_ident);
	   //System.out.println("img_oid " + img_oid);
	   //System.out.println("img_ident " + img_ident);
	} // while
	return mapAL;
     }
     catch(Exception e) {
        System.out.println("Exception in getImagesForSubPlate()");
	return null;
     }
  } // getImagesForSubPlate

  //---------------------------------------------------------------------
  public ArrayList<AnnotationInfo> getAnnotationForImage(Map<String,String> imgMap) {

     String img_ident = "";
     String img_oid = "";
     String img_fk = "";
     String knum = "";
     String ont_fk = "";
     String kdesk = "";
     AnnotationInfo annotInfo = null;;
     ArrayList<AnnotationInfo> annotAL = new ArrayList<AnnotationInfo>();
     Statement statement = null;
     ResultSet rs = null;

     img_oid = imgMap.get("img_oid");
     img_ident = imgMap.get("img_ident");

     if(_debug) System.out.println("getting annotation for image " + img_ident);
     //System.out.println("getting annotation for image " + img_oid + ", " + img_ident);

     try {
        if(plt_oid == null || plt_oid.equals("")) {
           System.out.println("---------no plate specified-------------");
	   return null;
	}

	//System.out.println("plt_oid " + plt_oid);

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT DISTINCT LOC_OID, LOC_TERM_FK, LOC_K_NUM FROM LOCATION WHERE LOC_IMAGE_FK=" + img_oid);

        if(rs == null) {
           System.out.println("no annotations for image " + img_oid);
	   return null;
        }


	while(rs.next()) {
	   annotInfo = new AnnotationInfo();
	   img_fk = img_oid;
	   knum = rs.getString("LOC_K_NUM");
	   //ont_fk = rs.getString("ANN_ONTOLOGY_COMP_FK");
	   kdesk = getDescriptionForTerm(rs.getString("LOC_TERM_FK"));
	   if(!img_fk.equals(img_oid)) {
              System.out.println("img_fk " + img_fk + " doesn't match img_oid " + img_oid);
	   }

	   annotInfo.setImgKey(img_fk);
	   annotInfo.setImgIdent(img_ident);
	   annotInfo.setKNum(knum);
	   //annotInfo.setOncKey(ont_fk);
	   annotInfo.setKDesk(kdesk);

           //getWikiUrl(annotInfo);

	   if(_debug) {
              System.out.println("img_fk " + img_fk);
              System.out.println("img_ident " + img_ident);
              System.out.println("knum " + knum);
              //System.out.println("ont_fk " + ont_fk);
              System.out.println("kdesk " + kdesk);
	   }

	   annotAL.add(annotInfo);
	} // while

	return annotAL;

     }
     catch(Exception e) {
        System.out.println("Exception in getAnnotationForImage()");
	return null;
     }

  } // getAnnotationForImage

  //---------------------------------------------------------------------
  public String getDescriptionForTerm(String term_id) {

     String ont_id = "";
     String desc = "";
     Statement statement = null;
     ResultSet rs = null;

     if(_debug) System.out.println("getting term description for " + term_id);

     try {
        if(term_id == null || term_id.equals("")) {
           System.out.println("---------no term identifier supplied-------------");
	   return "";
	}

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT TRM_DESCRIPTION FROM TERM WHERE TRM_OID=\"" + term_id + "\"");

        if(rs == null) {
           System.out.println("no description for %s" + term_id);
	   return "";
        }
	
	desc = rs.getString("TRM_DESCRIPTION");
     }
     catch(Exception e) {
        System.out.println("Exception in getDescriptionForTerm()");
	return "";
     }

     return desc;

  } // getDescriptionForTerm

  //---------------------------------------------------------------------
  public void getWikiUrl(AnnotationInfo annotInfo) {

     String wiki = "";
     ArrayList<String> wiki_AL = null;
     String emapa = "";
     Statement statement = null;
     ResultSet rs = null;

     emapa = annotInfo.getOncKey();

     if(_debug) System.out.println("getting external annotation for " + emapa);

     try {
        if(annotInfo == null) {
           System.out.println("---------no annotation object supplied-------------");
	   return;
	}

        statement = db.createStatement();
	rs = statement.executeQuery("SELECT WIK_URL FROM ANP_WIKI WHERE WIK_ONTOLOGY_COMP_FK=\"" + emapa + "\"");

        if(rs == null) {
           System.out.println("no wiki for %s" + emapa);
	   return;
        }

        wiki_AL = new ArrayList<String> ();

	while(rs.next()) {
	   wiki = rs.getString("WIK_URL");
	   if(_debug) {
              System.out.println("wiki " + wiki);
	   }
	   wiki_AL.add(wiki);
	} // while

	annotInfo.setWikiArr(wiki_AL);
     }
     catch(Exception e) {
        System.out.println("Exception in getWikiUrl()");
	return;
     }

  } // getWikiUrl

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
  private StringBuffer toJson(ArrayList<ArrayList<AnnotationInfo>> plateAnnotAL) {

     AnnotationInfo annot = null;
     ArrayList<AnnotationInfo> imgAnnotAL = null;
     StringBuffer annotBuf = new StringBuffer();
     Iterator img_it = null;
     Iterator annot_it = null;
     boolean LAST_IMG;
     boolean LAST_ANN;
     boolean LAST;
     int countImg = 0;
     int countAnn = 0;

     JsonUtil.writeStartOfArray(annotBuf, "", false, indent);
     indent++;

     img_it = plateAnnotAL.iterator();
     while(img_it.hasNext()) {
        //System.out.println("---------------------------------------- img ---------------------------------" + ++countImg);
        imgAnnotAL = (ArrayList<AnnotationInfo>) img_it.next(); 
	LAST_IMG = img_it.hasNext() ? false : true;
        annot_it = imgAnnotAL.iterator();
        while(annot_it.hasNext()) {
           //System.out.println(".......................... annot .........................." + ++countAnn);
           annot = (AnnotationInfo) annot_it.next();
	   LAST_ANN = annot_it.hasNext() ? false : true;
	   LAST = LAST_IMG && LAST_ANN;
	   //System.out.println("LAST_IMG = " + LAST_IMG + " LAST_ANNOT " + LAST_ANN + " LAST " + LAST);
           annot.printJson(annotBuf, indent, LAST);
	   annotBuf.append("\n");
        }
     }

     indent--;
     JsonUtil.writeEndOfArray(annotBuf, true, true, indent);

     return annotBuf;
  }

  //---------------------------------------------------------------------
  void printInfo() {
  }

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    GetAnnotationForImage app = new GetAnnotationForImage();
  }

} // class GetAnnotationForImage
