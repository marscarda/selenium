package selenium.bearing;
//***************************************************************************
import hydrogen.andromeda.AndromedaTransactorFront;
import hydrogen.credentials.CredentialsTransactor;
import hydrogen.dictionary.DictionaryTransactorFront;
import hydrogen.dictionary.GeographicTransactor;
//import hydrogen.processes.ProcessTransactor;
import hydrogen.root.DBConnector;
import java.sql.Connection;
//***************************************************************************
public class Transactors 
{
    //====================================================================
    String dbHost = null;
    String dbuser = null;
    String dbpass = null;
    String dbCredentials = null;
    String dbGeodata = null;
    String dbProcess = null;
    String dbAndromeda = null;
    Connection dbconnection = null;
    //--------------------------------------------------------------------
    CredentialsTransactor credtransc = null;
    DictionaryTransactorFront dictadmintrns = null;
    GeographicTransactor geogrtransc = null;
    //ProcessTransactor proctransac = null;
    AndromedaTransactorFront andrtransac = null;
    //====================================================================
    public void setDBHost (String h) { dbHost = h; }
    public void setDBUser (String u) { dbuser = u; }
    public void setDBPass (String p) { dbpass = p; }
    public void setDBGeoData (String db) { dbGeodata = db; }
    public void setDBCredentials (String db) { dbCredentials = db; }
    public void setDBProcess (String db) { dbProcess = db; }
    public void setDBAndromeda (String db) { dbAndromeda = db; }
    //====================================================================
    public void openDBConnection () throws Exception
    {
        dbconnection = DBConnector.createDBConnection(dbHost, dbuser, dbpass);
    }
    //====================================================================
    protected Connection getDBConnection () throws Exception
    {
        if (dbconnection == null) openDBConnection();
        return dbconnection;
    }
    //====================================================================
    public void disposeDBConnection ()
    {
        //=========================================
        try { if (dbconnection != null) DBConnector.destroyDBConnection(dbconnection); }
        catch (Exception e)
        {
            System.out.println("appmsg: When closing connection");
            System.out.println(e.getMessage());
        }
        //=========================================
        //=========================================
    }
    //********************************************************************
    public CredentialsTransactor getCredentialsTransactors () throws Exception
    {
        if (credtransc == null) {
            if (dbconnection == null) openDBConnection();
            credtransc = new CredentialsTransactor();
            credtransc.setDBConnection(dbconnection);
            credtransc.setDatabaseName(dbCredentials);
        }
        return credtransc;
    }
    //********************************************************************
    public GeographicTransactor getGeographicTransactor () throws Exception
    {
        if (geogrtransc == null) {
            if (dbconnection == null) openDBConnection();
            geogrtransc = new GeographicTransactor();
            geogrtransc.setDBConnection(dbconnection);
            geogrtransc.setDatabaseName(dbGeodata);
        }
        return geogrtransc;
    }
    //********************************************************************
    public DictionaryTransactorFront getDictionaryAdminTransactor () throws Exception
    {
        if (dictadmintrns == null) {
            if (dbconnection == null) openDBConnection();
            dictadmintrns = new GeographicTransactor();
            dictadmintrns.setDBConnection(dbconnection);
            dictadmintrns.setDatabaseName(dbGeodata);
        }
        return dictadmintrns;
    }
    //********************************************************************
    /*
    public ProcessTransactor getProcessTransactor () throws Exception
    {
        if (proctransac == null) {
            if (dbconnection == null) openDBConnection();
            proctransac = new ProcessTransactor();
            proctransac.setDBConnection(dbconnection);
            proctransac.setDatabaseName(dbProcess);
        }
        return proctransac;
    }
    */
    //********************************************************************
    public AndromedaTransactorFront getAndromedaTransactor () throws Exception
    {
        if (andrtransac == null) {
            if (dbconnection == null) openDBConnection();
            andrtransac = new AndromedaTransactorFront();
            andrtransac.setDBConnection(dbconnection);
            andrtransac.setDatabaseName(dbAndromeda);
        }
        return andrtransac;
    }
    //********************************************************************
}
//***************************************************************************







