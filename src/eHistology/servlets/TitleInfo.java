package eHistology.servlets;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import eHistology.util.JsonUtil;

public class TitleInfo {

    public String oid;
    public String identifier;
    public String stage;
    public String dpc;
    public String witschi;
    public String carnegie;
    public String sectionType;
    public String crLength;
    public String description;
    public ArrayList<String> img_AL;

    public String getOid() {
       return oid;
    }

    public String getIdentifier() {
       return identifier;
    }

    public String getStage() {
       return stage;
    }

    public String getdpc() {
       return dpc;
    }

    public String getWitschi() {
       return witschi;
    }

    public String getCarnegie() {
       return carnegie;
    }

    public String getSectionType() {
       return sectionType;
    }

    public String getLength() {
       return crLength;
    }

    public String getDescription() {
       return description;
    }

    public ArrayList<String> getImg_AL() {
       return img_AL;
    }

    //--------------------------------------------------
    public void setOid(String id) {
       oid = id;
    }

    public void setIdentifier(String str) {
       //System.out.printf("setIdentifier: %s\n",str);
       identifier = str;
    }

    public void setStage(String str) {
       //System.out.printf("setStage: %s\n",str);
       stage = str;
    }

    public void setDpc(String str) {
       dpc = str;
    }

    public void setWitschi(String str) {
       witschi = str;
    }

    public void setCarnegie(String str) {
       carnegie = str;
    }

    public void setSectionType(String str) {
       sectionType = str;
    }

    public void setLength(String str) {
       crLength = str;
    }

    public void setDescription(String str) {
       description = str;
    }

    public void setImg_AL(ArrayList<String> al) {
       img_AL = al;
    }

    //--------------------------------------------------
    public String getFromStage() {
       return getFromStr(stage);
    }

    //--------------------------------------------------
    public String getToStage() {
       return getToStr(stage);
    }

    //--------------------------------------------------
    public String getFromDpc() {
       return getFromStr(dpc);
    }

    //--------------------------------------------------
    public String getToDpc() {
       return getToStr(dpc);
    }

    //--------------------------------------------------
    public String getFromStr(String str) {
       String[] stray = parseStringWithEmdash(stage);
       return stray[0];
    }

    //--------------------------------------------------
    public String getToStr(String str) {
       String[] stray = parseStringWithEmdash(stage);
       return stray[1];
    }

    //--------------------------------------------------
    public String[] parseStringWithEmdash(String str) {
       
       int indx = -1;
       String[] stray = new String[2];;

       indx = str.indexOf("—");

       //System.out.printf("Index of emdash in %s = %d\n",str,indx);

       if(indx == -1) {
          stray[0] = str;
          stray[1] = str;
       } else {
          stray[0] = str.substring(0,indx);
          stray[1] = str.substring(indx+1);
       }

       //System.out.printf("stray[0] %s, stray[1] %s\n",stray[0],stray[1]);

       return stray;
    }

    //--------------------------------------------------
    public void print() {
       /*
       String[] imgArr = new String[img_AL.size()];
       imgArr = img_AL.toArray(imgArr);
       String imgs = Arrays.toString(imgArr);
       */

       Iterator it = img_AL.iterator();
       String img = "";
       StringBuffer imgbuf = new StringBuffer();

       while(it.hasNext()) {
          img = (String) it.next();
          imgbuf.append(img + ":");
       }

       System.out.printf("TitleInfo: %s, %s, theiler TS%s, dpc %s, images %s\nwitschi %s, carnegie %s, section type %s, length %s, description %s\n", oid, identifier, stage, dpc, imgbuf.toString(),witschi,carnegie,sectionType,crLength,description);
    }

    //--------------------------------------------------
    public void printJson(StringBuffer strbuf, int ind, boolean LAST_PLATE) {

       boolean NEWLINE = true;
       boolean LAST = true;
       boolean LAST_IMG = true;
       int indent = ind;
       Iterator img_it = null;
       String val = "";

       JsonUtil.writeStartOfObject(strbuf, "", NEWLINE, indent);  /// plate
          JsonUtil.writeObjectElement(strbuf, "plate", identifier, !LAST, !NEWLINE, indent); 
          JsonUtil.writeObjectElement(strbuf, "stage", stage, !LAST, !NEWLINE, indent); 
          JsonUtil.writeObjectElement(strbuf, "dpc", dpc, !LAST, !NEWLINE, indent); 
          JsonUtil.writeStartOfArray(strbuf, "images", NEWLINE, indent);   // start images
             img_it = img_AL.iterator();
             while(img_it.hasNext()) {
                val = (String) img_it.next();
                LAST_IMG = img_it.hasNext() ? false : true;
                JsonUtil.writeArrayElement(strbuf, val, LAST_IMG, !NEWLINE, indent); 
             }
          JsonUtil.writeEndOfArray(strbuf, !LAST, !NEWLINE, indent);       // end images
          JsonUtil.writeObjectElement(strbuf, "witschi", witschi, !LAST, !NEWLINE, indent); 
          JsonUtil.writeObjectElement(strbuf, "carnegie", carnegie, !LAST, !NEWLINE, indent); 
          JsonUtil.writeObjectElement(strbuf, "sectionType", sectionType, !LAST, !NEWLINE, indent); 
          JsonUtil.writeObjectElement(strbuf, "crLength", crLength, !LAST, !NEWLINE, indent); 
          JsonUtil.writeObjectElement(strbuf, "description", description, LAST, !NEWLINE, indent); 

       JsonUtil.writeEndOfObject(strbuf, LAST_PLATE, NEWLINE, indent);  /// plate
    }

} // class TitleInfo
