package eHistology.servlets;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;

import eHistology.util.JsonUtil;

public class AnnotationInfo {

    public String img_fk;
    public String img_ident;
    public String knum;
    public String ont_fk;
    public String kdesk;
    public String ont_desk;
    public String wiki;
    public ArrayList<String> wiki_AL;

//--------------------------------------------------
    public AnnotationInfo() {
    }

//--------------------------------------------------
    public String getImgKey() {
       return img_fk;
    }

    public String getImgIdent() {
       return img_ident;
    }

    public String getKNum() {
       return knum;
    }

    public String getOncKey() {
       return ont_fk;
    }

    public String getKDesk() {
       return kdesk;
    }

    public String getOntDesk() {
       return ont_desk;
    }

    public ArrayList<String> getWikiArr() {
       return wiki_AL;
    }
//--------------------------------------------------
    public void setImgKey(String imgk) {
       img_fk =  imgk;
    }

    public void setImgIdent(String id) {
       img_ident =  id;
    }

    public void setKNum(String num) {
       knum =  num;
    }

    public void setOncKey(String ofk) {
       ont_fk =  ofk;
    }

    public void setKDesk(String desk) {
       kdesk =  desk;
    }

    public void setOntDesk(String desk) {
       ont_desk =  desk;
    }

    public void setWikiArr(ArrayList<String> al) {
        wiki_AL = al;
    }

//--------------------------------------------------
    public void printJson(StringBuffer strbuf, int ind, boolean LAST_ANNOTATION) {

       boolean NEWLINE = true;
       boolean LAST = true;
       boolean LAST_WIKI = false;
       int indent = ind;
       Iterator wik_it = null;
       String val = "";

       wik_it = wiki_AL.iterator();

       JsonUtil.writeStartOfObject(strbuf, "", NEWLINE, indent);  /// start annotation
	  indent++;
	  JsonUtil.writeObjectElement(strbuf, "img_fk", img_fk, !LAST, NEWLINE, indent); 
	  JsonUtil.writeObjectElement(strbuf, "img_ident", img_ident, !LAST, NEWLINE, indent); 
	  JsonUtil.writeObjectElement(strbuf, "knum", knum, !LAST, NEWLINE, indent); 
	  JsonUtil.writeObjectElement(strbuf, "ont_fk", ont_fk, !LAST, NEWLINE, indent); 
	  JsonUtil.writeObjectElement(strbuf, "kdesk", kdesk, !LAST, NEWLINE, indent); 
	  JsonUtil.writeStartOfArray(strbuf, "wiki", NEWLINE, indent);  // start wiki entries
          while(wik_it.hasNext()) {
	     val = (String) wik_it.next();
	     //System.out.println(val);
             LAST_WIKI = wik_it.hasNext() ? false : true;
	     JsonUtil.writeArrayElement(strbuf, val, LAST_WIKI, NEWLINE, indent); 
	  }
	  JsonUtil.writeEndOfArray(strbuf, LAST, NEWLINE, indent);  // end wiki entries
	  indent--;
       JsonUtil.writeEndOfObject(strbuf, LAST_ANNOTATION, NEWLINE, indent);   /// end annotation
    }

} // class AnnotationInfo
