package selenium;
//*************************************************************************

//*************************************************************************
public class SeleniumSettings 
{
    //========================================================
    private static String dbhost = null;
    private static String dbuser = null;
    private static String dbpass = null;
    //--------------------------------------------------------
    public static void setDBHost (String dbhost) { SeleniumSettings.dbhost = dbhost; }
    public static void setDBUser (String dbuser) { SeleniumSettings.dbuser = dbuser; }
    public static void setDBPass (String dbpass) { SeleniumSettings.dbpass = dbpass; }
    public static String getDBHost () {
        if (dbhost == null) return "";
        return dbhost; 
    }
    public static String getDbUser () {
        if (dbuser == null) return "";
        return dbuser; 
    }
    public static String getDBPass () {
        if (dbpass == null) return "";
        return dbpass; 
    }
    //========================================================
    private static String dbgeographicdata = null;
    private static String dbcredentials = null;
    private static String dbprocesses = null;
    //--------------------------------------------------------
    public static void setDBGeographicData (String db) { dbgeographicdata = db; }
    public static void setDBCredentials (String db) { dbcredentials = db; }
    public static void setDBProcess (String db) { dbprocesses = db; }
    public static String getDBGeaographicData () {
        if (dbgeographicdata == null) return "";
        return dbgeographicdata;
    }
    public static String getDBCredentials () {
        if (dbcredentials == null) return "";
        return dbcredentials;
    }
    public static String getDBProcess () {
        if (dbprocesses == null) return "";
        return dbprocesses;
    }
    //========================================================
    private static int maxbatchthreads = 5;
    private static int rowsperthread = 50;
    public static void setMaxBatchThreads (int max) { maxbatchthreads = max; }
    public static void setRowsPerBatchThread (int numrows) { rowsperthread = numrows; }
    public static int maxBatchThreads () { return maxbatchthreads; }
    public static int rowsPertBatchThread () { return rowsperthread; }
    //========================================================
}
//*************************************************************************

