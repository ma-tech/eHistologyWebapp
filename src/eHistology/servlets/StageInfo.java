package eHistology.servlets;

import java.util.Arrays;
import java.util.Vector;
import java.util.Iterator;

import eHistology.util.JsonUtil;

public class StageInfo {

    public String fromStage;
    public String toStage;
    public Vector<Plate> plateVec;

//--------------------------------------------------
    public StageInfo(String from, String to) {
       //System.out.printf("StageInfo constructor from %s, to %s \n",from,to);
       fromStage = from;
       toStage = to;
    }
//--------------------------------------------------
    public String getFromStage() {
       //System.out.printf("StageInfo.getFromStage returning %s\n",fromStage);
       return fromStage;
    }

    public String getToStage() {
       //System.out.printf("StageInfo.getToStage returning %s\n",toStage);
       return toStage;
    }

    public Vector<Plate> getPlateVec() {
       return plateVec;
    }
//--------------------------------------------------
    public void setFromStage(String stg) {
       fromStage = stg;
    }

    public void setToStage(String stg) {
       toStage = stg;
    }

    public void addPlate(String id, Vector<String> iVec, String from, String to) {

       Plate plate = new Plate(id, iVec, from, to);

       if(plateVec == null) {
          plateVec = new Vector<Plate>();
       }

       plateVec.addElement(plate);
    }
//--------------------------------------------------
    public void printJson(StringBuffer strbuf, int ind, boolean LAST_STAGE_INFO) {

       Plate plate = null;
       boolean NEWLINE = true;
       boolean LAST = true;
       boolean LAST_PLATE = true;
       boolean LAST_IMG = true;
       int indent = ind;
       Iterator it = null;
       Iterator img_it = null;
       String val = "";
       Vector<String> imgVec;

       JsonUtil.writeStartOfObject(strbuf, "", NEWLINE, indent);  /// stage range
       indent++;
       JsonUtil.writeStartOfArray(strbuf, "stage", NEWLINE, indent);   // start stage
          JsonUtil.writeArrayElement(strbuf, fromStage, !LAST, !NEWLINE, indent); 
          JsonUtil.writeArrayElement(strbuf, toStage, LAST, !NEWLINE, indent); 
          JsonUtil.writeEndOfArray(strbuf, !LAST, !NEWLINE, indent);    // end stage
          JsonUtil.writeStartOfArray(strbuf, "plates", NEWLINE, indent);  // start plates
          //........................................
          indent++;
          it = plateVec.iterator();
          while(it.hasNext()) {
             plate = (Plate) it.next();
	     LAST_PLATE = it.hasNext() ? false : true;
      	     val = plate.identifier;
      	     imgVec = plate.imgVec;
             JsonUtil.writeStartOfObject(strbuf, "", NEWLINE, indent);  /// start plate
	        indent++;
                JsonUtil.writeObjectElement(strbuf, "plate", val, !LAST, NEWLINE, indent); 
                JsonUtil.writeStartOfArray(strbuf, "images", NEWLINE, indent);   // start images
                   img_it = imgVec.iterator();
                   while(img_it.hasNext()) {
                      val = (String) img_it.next();
	              LAST_IMG = img_it.hasNext() ? false : true;
                      JsonUtil.writeArrayElement(strbuf, val, LAST_IMG, !NEWLINE, indent); 
                   }
                JsonUtil.writeEndOfArray(strbuf, !LAST, !NEWLINE, indent);       // end images
                JsonUtil.writeStartOfArray(strbuf, "dpc", NEWLINE, indent);   // start dpc
                   JsonUtil.writeArrayElement(strbuf, plate.fromDpc, !LAST, !NEWLINE, indent); 
                   JsonUtil.writeArrayElement(strbuf, plate.toDpc, LAST, !NEWLINE, indent); 
                JsonUtil.writeEndOfArray(strbuf, LAST, !NEWLINE, indent);       // end dpc
	        indent--;
             JsonUtil.writeEndOfObject(strbuf, LAST_PLATE, NEWLINE, indent);   /// end plate
          }
   	  indent--;
          //........................................
          JsonUtil.writeEndOfArray(strbuf, LAST, NEWLINE, indent);     // end plates
       indent--;
       JsonUtil.writeEndOfObject(strbuf, LAST_STAGE_INFO, NEWLINE, indent);  /// stage range
    }

//==================================================
    private class Plate {

       public String identifier;
       public String fromDpc;
       public String toDpc;
       public Vector<String> imgVec;

//--------------------------------------------------
       public Plate(String id, Vector<String> iVec, String from, String to) {
          identifier = id;
	  imgVec = iVec;
	  fromDpc = from;
          toDpc = (to == null || to.equals("")) ? from : to;
       }

       public Plate(String id) {
          identifier = id;
       }
//--------------------------------------------------
       public String getIdentifier() {
          return identifier;
       }
   
       public Vector<String> getImgVec() {
          return imgVec;
       }

       public String getFromDpc() {
          return fromDpc;
       }

       public String getToDpc() {
          return toDpc;
       }
//--------------------------------------------------
       public void setIdentifier(String id) {
          identifier = id;
       }
   
       public void setImgVec(Vector<String> vec) {
          imgVec = vec;
       }
   
       public void setFromDpc(String from) {
          fromDpc = from;
       }
   
       public void setToDpc(String to) {
          toDpc = (to == null || to.equals("")) ? fromDpc : to;
       }
//--------------------------------------------------
       public void printJson() {
       }
   
    }
//==================================================

} // class StageInfo
