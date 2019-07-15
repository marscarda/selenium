package selenium.joinvalue.back;
//*************************************************************************
import hydrogen.auriga.DBInterfaceAurigaFront;
import hydrogen.auriga.NewDataField;
import hydrogen.root.DBConnector;
import hydrogen.root.HydroException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import selenium.BackMessagesHolder;
//*************************************************************************
public class JoinValueUploader extends Thread 
{
    //*********************************************************************
    static final int FIELDSEXPECTED = 3;
    //---------------------------------------------------------------------
    private long userid = 0;
    //---------------------------------------------------------------------
    int rowcount = 0;
    //---------------------------------------------------------------------
    private String dbhost = null;
    private String dbuser = null;
    private String dbpass = null;
    private String dbdictionary = null;
    //---------------------------------------------------------------------
    private String dictionary = null;
    private String group = null;
    //---------------------------------------------------------------------
    private byte[] inputdata = null;
    //=====================================================================
    private Connection dbconnection = null;
    private DBInterfaceAurigaFront transint = null;
    //---------------------------------------------------------------------
    private String[] inputrows = null;
    private NewDataField[] datafields = null;
    //=====================================================================
    public void setUserId (long userid) { this.userid = userid; }
    //=====================================================================
    public void setDbConnectionData (String host, String user, String pass) {
        dbhost = host;
        dbuser = user;
        dbpass = pass;
    }
    public void setDatabase (String database) { dbdictionary = database; }
    //---------------------------------------------------------------------
    public void setTarget (String dictionary, String pakcage) {
        this.dictionary = dictionary;
        this.group = pakcage;
    }
    //---------------------------------------------------------------------
    public void setData (byte[] data) { inputdata = data; }
    //*********************************************************************
    @Override
    public void run() {
        //-------------------------------------------
        BackMessagesHolder.addMessage(userid, "Join Values started to get processed", false);
        //-------------------------------------------
        //-------------------------------------------
        boolean goingok = true;
        //-------------------------------------------
        if (goingok) 
            if (!initialize()) goingok = false;
        //-------------------------------------------
//        if (goingok)
//            if (!startTx()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!splitToRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!loopRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!addDataFields()) goingok = false;
        //-------------------------------------------
        cleanUp();
        //-------------------------------------------
        if (goingok)
            BackMessagesHolder.addMessage(userid, "Data Fields Added", false);
        //-------------------------------------------
    }
    //=====================================================================
    private boolean initialize () {
        //=========================================================
        try {
            dbconnection = DBConnector.createDBConnection(dbhost, dbuser, dbpass);
        }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Internal Server error", true);
            System.out.println("Failed to insert boundaries. Trying to open the db connection");
            System.out.println(e.getMessage());
            return false;
        }
        //=========================================================
        transint = new DBInterfaceAurigaFront();
        transint.setDBConnection(dbconnection);
        transint.setDatabaseName(dbdictionary);
        //=========================================================
        return true;
        //=========================================================
    }
    //=====================================================================
    private boolean startTx () {
        try { transint.startTransaction(); }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Internal Server error", true);
            System.out.println("Failed to start transactions creating roads");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    //=====================================================================
    private boolean splitToRows () {
        //====================================================
        String uplddata;
        try { uplddata = new String(inputdata, "UTF-8"); }
        catch (Exception e) 
        {
            BackMessagesHolder.addMessage(userid, "Processing values. failed. Invalid File", true);
            System.out.println(e.getMessage()); 
            return false;
        }
        //-----------------------------------------------------
        inputrows = uplddata.split("\\r?\\n");
        inputdata = null;
        rowcount = inputrows.length;
        return rowcount > 0;
        //-----------------------------------------------------
    }
    //=====================================================================
    private boolean loopRows () {
        //--------------------------------------------------------
        String[] fields;
        NewDataField datafield;
        List<NewDataField> items = new ArrayList<>();
        //--------------------------------------------------------
        for (String row : inputrows) {
            fields = row.split("\\|");
            if (fields.length < FIELDSEXPECTED) continue;
            removeQuotes(fields);
            //----------------------------------------------------
            try { datafield = this.doNewDataField(fields); }
            catch (Exception e) {
                BackMessagesHolder.addMessage(userid, e.getMessage(), true);
                return false;
            }
            //----------------------------------------------------
            items.add(datafield);
            //----------------------------------------------------
        }
        //--------------------------------------------------------
        datafields = items.toArray(new NewDataField[0]);
        return true;
        //--------------------------------------------------------
    }
    //=====================================================================
    private boolean addDataFields () {
        try {
            transint.addDataFields(datafields);
            return true;
        }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Internal Server error", true);
            System.out.println("could not insert boundary item");
            System.out.println(e.getMessage());
            return false;
        }
    }
    //*********************************************************************
    private NewDataField doNewDataField (String fields[]) throws HydroException {
        NewDataField datafield = new NewDataField();
        datafield.setIdcode(fields[0]);
        datafield.setIdItem(fields[1]);
        datafield.setFieldName(fields[2]);
        datafield.setData(fields[3]);
        //---------------------------------------------------
        datafield.setDictionary(dictionary);
        datafield.setGroup(group);
        //---------------------------------------------------
        return datafield;
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
