package selenium.process.front;
//**************************************************************************
import hydrogen.andromeda.AndrProcess;
import hydrogen.andromeda.AndromedaTransactorFront;
import hydrogen.andromeda.ProcessCode;
import hydrogen.andromeda.TxtTable;
import hydrogen.root.ErrorCode;
import hydrogen.root.HydroException;
//**************************************************************************
public class ProcessRemove {
    //==============================================================
    private AndromedaTransactorFront andrtransac = null;
    public void setAndromedaTransactor (AndromedaTransactorFront trans) { andrtransac = trans; }
    //==============================================================
    private long processid = 0;
    public void setProcessID (long processid) { this.processid = processid; }
    //==============================================================
    private long userid = 0;
    private boolean ignoreuser = false;
    public void setUserID (long userid, boolean ignoreuser) { 
        this.userid = userid; 
        this.ignoreuser = ignoreuser;
    }
    //==============================================================
    public void execute () throws Exception
    {
        //----------------------------------------------------------
        AndrProcess process = andrtransac.getProcessByID(processid);
        if (process.userID() != userid && !ignoreuser)
            throw new HydroException("Not authorized", ErrorCode.NOTAUTHORIZED);
        //----------------------------------------------------------
        TxtTable table;
        try { table = andrtransac.getTableById(process.tableID()); }
        catch (HydroException e) { table = new TxtTable(true); }
        table.setProcessCode(ProcessCode.NONE);
        //----------------------------------------------------------
        andrtransac.startTransaction();
        andrtransac.deleteProcess(processid);
        if (table.isValid()) andrtransac.updateTxtTable(table);
            //andrtransac.resetProcessRows(table.tableID());
        andrtransac.commitTransaction();
        //----------------------------------------------------------
    }
    //==============================================================
}
//**************************************************************************
