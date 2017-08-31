package eHistology.util;

import java.util.Arrays;
import java.util.Vector;
import java.util.Iterator;

public class JsonUtil {

  private static String tab = "   ";

  //---------------------------------------------------------------------
  public static void writeStartOfArray(StringBuffer buf, String name, boolean newline, int indent) {
     
     if(newline) {
        buf.append("\n");
     }
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
  public static void writeArrayElement(StringBuffer buf, String val, boolean last, boolean newline, int indent) {
     
     if(newline) {
        buf.append("\n");
        for(int i=0; i<indent; i++) {
           buf.append(tab);
        }
     }
     if(last) {
        buf.append("\"" + val + "\"");
     } else {
        buf.append("\"" + val + "\",");
     }

  } // writeArrayElement

  //---------------------------------------------------------------------
  public static void writeEndOfArray(StringBuffer buf, boolean last, boolean newline, int indent) {
     
     if(newline) {
        buf.append("\n");
        for(int i=0; i<indent; i++) {
           buf.append(tab);
        }
     }
     if(last) {
        buf.append("]");
     } else {
        buf.append("],");
     }

  } // writeEndOfArray

  //---------------------------------------------------------------------
  public static void writeStartOfObject(StringBuffer buf, String name, boolean newline, int indent) {
     
     if(newline) {
        buf.append("\n");
        for(int i=0; i<indent; i++) {
           buf.append(tab);
        }
     }
     if(name == null || name.equals("")) {
        buf.append("{");
     } else {
        buf.append("\"" + name + "\": {"); 
     }

  } // writeStartOfObject

  //---------------------------------------------------------------------
  public static void writeObjectElement(StringBuffer buf, String key, String val, boolean last, boolean newline, int indent) {

     // for debugging UTF-8 issues
     //JsonUtil.showUnicode(val);

     if(newline) {
        buf.append("\n");
        for(int i=0; i<indent; i++) {
           buf.append(tab);
        }
     }
     if(last) {
        buf.append("\"" + key + "\": \"" + val + "\""); 
     } else {
        buf.append("\"" + key + "\": \"" + val + "\","); 
     }
     
  } // writeObjectElement

  //---------------------------------------------------------------------
  public static void writeEndOfObject(StringBuffer buf, boolean last, boolean newline, int indent) {

     if(newline) {
        buf.append("\n");
        for(int i=0; i<indent; i++) {
           buf.append(tab);
        }
     }
     if(last) {
        buf.append("}");
     } else {
        buf.append("},");
     }

  } // writeEndOfObject

  //---------------------------------------------------------------------
  public static void showUnicode(String str) {

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

} // class JsonUtil
