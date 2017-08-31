package eHistology.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

import eHistology.util.DbUtil;

public class GetDownloadInfo extends HttpServlet {

  private PrintWriter writer = null;
  private String auth;
  private String tab = "   ";
  private int indent = 0;
  private String json;
  private boolean byStage;
  private String project;
  private Connection db;
  private String imgId = "";
  private boolean _debug = false;

  //---------------------------------------------------------------------
  public GetDownloadInfo() {
  }

  //---------------------------------------------------------------------
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     doGet(req, res);
  }

  //---------------------------------------------------------------------
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    _debug = false;

    if(_debug) System.out.println("doGet in GetDownloadInfo");

    project = DbUtil.getProject(req);
    imgId = req.getParameter("image");

    res.setHeader("Cache-Control", "no-cache");
    res.setHeader("Content-Type", "text/html; charset=UTF-8");
    writer = res.getWriter();

    try {
       db = DbUtil.getDBConnection(project);
       if(db == null) {
          if(_debug) System.out.println("********** couldn't connect to DB ************");
       }


       //if(_debug) System.out.println("catalog is called " +  md.getCatalogTerm());
       json = getInfo();
       writer.println(json);
       writer.close();
       DbUtil.closeDBConnection(db);
    }
    catch(Exception e) {
       System.out.println("GetDownloadInfo: doGet: Exception occurred");
    }
  }

  //---------------------------------------------------------------------
  private String getInfo() {

     if(_debug) System.out.println("GetDownloadInfo: getInfo");
     
     String info = getDownloadInfoForImage();;

     return info;

  } // getInfo

  //---------------------------------------------------------------------
  public String getDownloadInfoForImage() {

      if(imgId == null || imgId.equals("")) {
	  System.out.println("getDownloadInfoForImage:---------no image specified-------------");
	  return null;
      }
      
      if(_debug) System.out.println("imgId " + imgId);

      Statement statement = null;
      String url = null;
      ResultSet rs = null;
      
      try {
	  DatabaseMetaData md = db.getMetaData();
	  rs = md.getColumns(null, null, "ANP_IMAGE", "IMG_DOI_URL");
	  // has table ANP_IMAGE which has column IMG_DOI_URL
	  if (rs.next() && null != rs) {
	      statement = db.createStatement();
	      rs = statement.executeQuery("select IMG_DOI_URL from ANP_IMAGE where IMG_IDENTIFIER = '" + imgId + "';");
	      
	      if(rs.next() && null != rs)
		  url = rs.getString("IMG_DOI_URL");
	  }

	  if (null == url) {
	      rs = md.getColumns(null, null, "ANP_DOWNLOAD", null);
	      // has table ANP_DOWNLOAD
	      if (rs.next() && null != rs) {
		  statement = db.createStatement();
		  rs = statement.executeQuery("select a.IMG_IDENTIFIER, b.DLD_URL from ANP_IMAGE a, ANP_DOWNLOAD b where a.IMG_OID = b.DLD_IMAGE_FK and IMG_IDENTIFIER = '" + imgId + "';");

		  if(rs.next() && null != rs)
		      url = rs.getString("DLD_URL");
	      }
	  }
      } catch(Exception e) {
        System.out.println("Exception in getDownloadInfoForImage");
        System.out.println(e.getMessage());
	url = null;
     }

     if(_debug) System.out.println(">>>>>>>>>  getDownloadInfoForImage: url " + url);

     return url;

  } // getDownloadInfoForImage


  //---------------------------------------------------------------------
  void printInfo() {
  }

  //---------------------------------------------------------------------
  public static void main(String[] arguments) throws Exception {
    GetDownloadInfo app = new GetDownloadInfo();
  }

} // class GetDownloadInfo
