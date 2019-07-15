package selenium.file;
//*************************************************************************
import argon.inoutdata.address.AddressFieldType;
//import argon.zresolve_old.objects.AddressFieldType;
//*************************************************************************
@Deprecated
public class selectorAddressField {
    
    //==========================================================
    private AddressFieldType type = AddressFieldType.IGNORED;
    public void setType (AddressFieldType type) { this.type = type; }
    public AddressFieldType getType () { return type; }
    //==========================================================
    private String name = null;
    public void setName (String name) {
        this.name = name; 
        type = AddressFieldType.BOUNDARY;
    }
    public String getName () {
        if (name == null) return "";
        return name;
    }
    //==========================================================
    private String displayname = null;
    public void setDisplayName (String displayname) { this.displayname = displayname; }
    public String getDisplayName () {
        if (displayname == null) return "";
        return displayname;
    }
    //==========================================================
    private int position = -1;
    public void setPosition (int position) { this.position = position; }
    public int getPosition () { return position; }
    //==========================================================
}
//*************************************************************************
