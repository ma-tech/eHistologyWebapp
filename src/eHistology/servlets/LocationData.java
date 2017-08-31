package eHistology.servlets;
public class LocationData {

    public String locationId;
    public String x;
    public String y;
    public String z;

    public String getLocationId() {
       return locationId;
    }

    public String getX() {
       return x;
    }
    public String getY() {
       return y;
    }
    public String getZ() {
       return z;
    }
//--------------------------------------------------
    public void setLocationId(String id) {
       //System.out.println("setLocationId " + id);
       locationId = id;
    }

    public void setX(String val) {
       x = val;
    }
    public void setY(String val) {
       y = val;
    }
    public void setZ(String val) {
       z = val;
    }
//--------------------------------------------------
    public void printObj() {
       System.out.println("locationId " + locationId);
       System.out.println("x " + x);
       System.out.println("y " + y);
       System.out.println("z " + z);
       System.out.println(".....................");
    }

} // class LocationData
