package selenium.dictionary.back;
//*************************************************************************
import hydrogen.dictionary.GeographicTransactor;
import hydrogen.dictionary.NewRoadItem;
import hydrogen.dictionary.NewUnion;
import hydrogen.root.DBConnector;
import hydrogen.root.HydroException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import selenium.BackMessagesHolder;
//*************************************************************************
public class RoadsUpldProcess extends Thread
{
    //*********************************************************************
    static final int FIELDSEXPECTED = 15;
    static final int UNIONSFIELDIND = FIELDSEXPECTED - 1;
    //---------------------------------------------------------------------
    private String dbhost = null;
    private String dbuser = null;
    private String dbpass = null;
    private String dbdictionary = null;
    //---------------------------------------------------------------------
    private String country = null;
    private String pakage = null;
    //---------------------------------------------------------------------
    private byte[] inputdata = null;
    //---------------------------------------------------------------------
    long userid = 0;
    //=====================================================================
    private Connection dbconnection = null;
    private GeographicTransactor geotrns = null;
    //---------------------------------------------------------------------
    private String[] inputrows = null;
    //=====================================================================
    public void setDbConnectionData (String host, String user, String pass) {
        dbhost = host;
        dbuser = user;
        dbpass = pass;
    }
    //---------------------------------------------------------------------
    public void setDictionaryDatabase (String database) { dbdictionary = database; }
    //---------------------------------------------------------------------
    public void setTarget (String country, String pakcage) {
        this.country = country;
        this.pakage = pakcage;
    }
    //=====================================================================
    public void setData (byte[] data) { inputdata = data; }
    //=====================================================================
    public void setUserId (long userid) { this.userid = userid; }
    //*********************************************************************
    public void execute() {
        //-------------------------------------------
        boolean goingok = true;
        //-------------------------------------------
        if (goingok) 
            if (!initialize()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!startTx()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!splitToRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!loopRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!commitTx()) goingok = false;
        //-------------------------------------------
        if (!goingok) RollbackTx();
        //-------------------------------------------
        cleanUp();
        //-------------------------------------------
        if (goingok)
            BackMessagesHolder.addMessage(userid, "Roads added", false);
    }
    //*********************************************************************
    /*
    @Override
    public void run() {
        //-------------------------------------------
        boolean goingok = true;
        //-------------------------------------------
        if (goingok) 
            if (!initialize()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!startTx()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!splitToRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!loopRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!commitTx()) goingok = false;
        //-------------------------------------------
        if (!goingok) RollbackTx();
        //-------------------------------------------
        cleanUp();
        //-------------------------------------------
        if (goingok)
            BackMessagesHolder.addMessage(userid, "Roads added", false);
    }
    */
    //*********************************************************************
    //=====================================================================
    private boolean initialize () {
        //=========================================================
        try {
            dbconnection = DBConnector.createDBConnection(dbhost, dbuser, dbpass);
        }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Internal Server error", true);
            System.out.println("Failed to insert roads. Trying to open the db connection");
            System.out.println(e.getMessage());
            return false;
        }
        //=========================================================
        geotrns = new GeographicTransactor();
        geotrns.setDBConnection(dbconnection);
        geotrns.setDatabaseName(dbdictionary);
        //=========================================================
        return true;
        //=========================================================
    }
    //=====================================================================
    private boolean startTx () {
        try { geotrns.startTransaction(); }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Internal Server error", true);
            System.out.println("Failed to start transactions creating roads");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    //=====================================================================
    private boolean commitTx () {
        try { geotrns.startTransaction(); }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Internal Server error", true);
            System.out.println("Failed to commit transactions creating roads");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    //=====================================================================
    private void RollbackTx () {
        try { geotrns.rollBackTransaction(); }
        catch (Exception e) {
            System.out.println("On boundary upload failure there was a failure to rollback the transaction");
            System.out.println(e.getMessage());
        }
    }
    //=====================================================================
    private boolean splitToRows () {
        String uplddata;
        try { uplddata = new String(inputdata, "UTF-8"); }
        catch (Exception e) 
        { 
            System.out.println(e.getMessage()); 
            return false;
        }
        //-----------------------------------------------------
        inputrows = uplddata.split("\\r?\\n");
        inputdata = null;
        return true;
        //-----------------------------------------------------
    }
    //=====================================================================
    private boolean loopRows () {
        //--------------------------------------------------------
        String[] fields;
        NewRoadItem roaditem;
        //--------------------------------------------------------
        for (String row : inputrows) {
            
            System.out.println(row);
            
            fields = row.split("\\|");
            if (fields.length < FIELDSEXPECTED) continue;
            removeQuotes(fields);
            //-----------------------------------------------------
            try { roaditem = doRoadItem(fields); }
            catch (Exception e) {
                BackMessagesHolder.addMessage(userid, e.getMessage(), true);
                return false;
            }
            //-----------------------------------------------------
            try { geotrns.addRoadItem(roaditem); }
            catch (HydroException e) {
                BackMessagesHolder.addMessage(userid, e.getMessage(), true);
                return false;
            }
            catch (Exception e) {
                BackMessagesHolder.addMessage(userid, "Internal Server error", true);
                System.out.println("could not insert road item");
                System.out.println(e.getMessage());
                return false;
            }
            //-----------------------------------------------------
            try {
                NewUnion[] unions = doUnions(fields);
                for (NewUnion union : unions) {
                    geotrns.addRoadUnion(union);
                }
            }
            catch (HydroException e) {
                BackMessagesHolder.addMessage(userid, e.getMessage(), true);
                return false;
            }
            catch (Exception e) {
                BackMessagesHolder.addMessage(userid, "Internal Server error", true);
                System.out.println("could not insert road union");
                System.out.println(e.getMessage());
                return false;
            }
            //-----------------------------------------------------
        }
        return true;
    }
    //=====================================================================
    private NewRoadItem doRoadItem (String[] fields) throws HydroException
    {
        NewRoadItem road = new NewRoadItem();
        road.setIdPath(fields[0]);
        road.setIdItem(fields[1]);
        road.setName(fields[2]);
        road.setSide(fields[3]);
        road.setAllowEven(fields[4]);
        road.setAllowOdd(fields[5]);
        road.setNumbers(fields[6], fields[7]);
        road.setPostaIn(fields[8]);
        road.setPostalOut(fields[9]);
        road.setFromCoordinate(fields[10], fields[11]);
        road.setToCoordinate(fields[12], fields[13]);
        //-----------------------------
        road.setCountry(country);
        road.setPackage(pakage);
        //-----------------------------
        return road;
    }
    //=====================================================================
    private NewUnion[] doUnions (String[] fields) throws HydroException
    {
        
        System.out.println("Unions field: " + fields[UNIONSFIELDIND]);
        
        String[] parents = fields[UNIONSFIELDIND].split(",");
        List<NewUnion> unions = new ArrayList<>();
        NewUnion union;
        for (String parent : parents) {
            
            System.out.println("Parent " + parent);
            
            if (parent.length() < 1) continue;
            union = new NewUnion();
            union.setParent(parent);
            union.setChild(fields[0]);
            union.setPathItem(fields[1]);
            //------------------------
            union.setCountry(country);
            union.setPackage(pakage);
            //------------------------
            unions.add(union);
        }
        return unions.toArray(new NewUnion[0]);
    }
    //*********************************************************************
    private void cleanUp () {
        if (dbconnection == null) return;
        DBConnector.destroyDBConnection(dbconnection);
    }
    //*********************************************************************
    private static void removeQuotes (String[] strin) {
        int indfrom, indto;
        int fcount = strin.length;
        String nstr;
        for (int n = 0; n < fcount; n++) {
            nstr = strin[n];
            indfrom = 0;
            indto = nstr.length();
            if (indto == 0) continue;
            if (nstr.codePointAt(0) == 34) indfrom++;
            if (nstr.codePointAt(indto - 1) == 34) indto--;
            if (indto < indfrom) strin[n] = "";
            else strin[n] = nstr.substring(indfrom, indto);
        }
    }
    //*********************************************************************
}
//*************************************************************************

