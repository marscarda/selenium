package selenium;
//******************************************************************
public class BackMessage 
{
    long userid = 0;
    String message = null;
    boolean red = false;
    public String getText () {
        if (message == null) return "";
        return message;
    }
    public boolean isRed () { return red; }
}
//******************************************************************